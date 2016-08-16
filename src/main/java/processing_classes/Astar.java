package processing_classes;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import org.jgrapht.graph.DefaultEdge;

public class Astar {

	private PriorityBlockingQueue<StateWeights> openQueue = new PriorityBlockingQueue<StateWeights>();
	private PriorityBlockingQueue<StateWeights> closedQueue = new PriorityBlockingQueue<StateWeights>();

	
	public Path solveAstar() throws InterruptedException{
		
		//Set initial node for openQueue
		Node initialNode = new Node();
		Path initialPath = new Path(initialNode);
		StateWeights initialSW = new StateWeights(initialPath,0.0);
		openQueue.add(initialSW);
		
		while (!openQueue.isEmpty()){
			
			//Gets the state with best f value, remove this state from openQueue
			StateWeights stateWeight = openQueue.poll();
			if (isComplete(stateWeight)){
				//Returns the optimal path
				setScheduleOnGraph(stateWeight.getState());
				return stateWeight.getState();
			} else {
			//Expanding the state to all possible next states
			expandState(stateWeight, 2);
			}
			closedQueue.add(stateWeight);
		}
		
		return null;
	}
	
	//Sets the value of the chosen path onto the nodes of the graph
	private void setScheduleOnGraph(Path state) {
		Set<Node> graphNodes = MainReadFile.graph.vertexSet();		
		//Loops through nodes of the path and then the nodes of the graph
		//setting the values of passed Path into the graphs nodes
		for (Node n : state.getPath() ){
			for (Node g: graphNodes){
				if (n.name.equals(g.name)){
					g.setProc(n.allocProc);
					g.setStart(n.startTime);
				}
			}			
		}		
	}


	//Expands the given stateWeight into all possible new states. Store these in OPEN. We would check if it exists already,
	//or if it exists in CLOSE. Don't know how/the significance of it yet.
	private void expandState(StateWeights stateWeight, int processors){
		Path current = stateWeight.state;
		//Get all the freeNodes available for the path
		ArrayList<Node> freeNodes = freeNodes(stateWeight);
		//Determine new states from the freenodes, get their weights, and add to openQueue
		for (Node n: freeNodes){
			for (int i = 1; i <= processors; i++){ 
				//Assign every free node to every available processor
				Node newNode = n;
				newNode.setProc(i); //Sets the processor for the newNode
				setNodeTimes(current, newNode, i); //Sets the start time, finish time for the newNode
				Path temp = new Path(current, newNode);
				double pathWeight = heuristicCost(temp);
				openQueue.add(new StateWeights(temp, pathWeight)); //Add new StateWeight to the openqueue.
			} 
		}
	}

	//Function to determine the start and finish time for the node
	public static void setNodeTimes(Path current, Node newNode, int processor){
		//Get the set of incoming edges of the newNode
		Set<DefaultEdge> incomingEdges = MainReadFile.graph.incomingEdgesOf(newNode);
		//End time of the last node to run on the processor
		int processorEndTime = latestEndTimeOnProcessor(current, processor);
		int parentEndTime = 0;
		int parentProcessor=processor;
		int latestAllowedTime;
		int t = 0;
		
		//If the set is empty, i.e no parents (dependencies) set start time to processorEndTime.
		if (incomingEdges.isEmpty()){
			newNode.setStart(processorEndTime);
		//If it does have parents, calculate the latest time newNode can run on processor
		}else for (DefaultEdge e: incomingEdges){
			int communicationTime = (int) MainReadFile.graph.getEdgeWeight(e);
			
			
			//Gets the parent node end time and processor
			Node parentNode = MainReadFile.graph.getEdgeSource(e);
			ArrayList<Node> setOfNodesInPath = current.getPath();
			
			//Needs to search through path to find parent node with the latest end time.
			for (Node n: setOfNodesInPath){
				if (n.name.equals(parentNode.name)){
						parentEndTime = n.finishTime;
						parentProcessor = n.allocProc;
				}
			}
			//Checks to see if communication time needs to be added to the latest time allow by current parent
			if (parentProcessor != processor){
				latestAllowedTime = parentEndTime + communicationTime;
			}else{
				latestAllowedTime = parentEndTime;
			}
			
			//If latestAllowed Time is the latest time found it is assigned to t 
			if (latestAllowedTime > t){
				t = latestAllowedTime;
			}
		}
		
		//Sets the start time of the new node to which ever is larger t or processorEndTime
		if (t > processorEndTime){
			newNode.setStart(t);
		}else{
			newNode.setStart(processorEndTime);
		}
		
		for (Node n: current.getPath()){
			System.out.print(n.name);
			System.out.print(" " + n.allocProc);
		}
		
		//Sets the Finish time
		newNode.setFinish(newNode.weight + newNode.startTime);
	}
	
	//Calculates the end time of the last node (task) to run on the passed processor
	private static int latestEndTimeOnProcessor(Path current, int processor) {
		ArrayList<Node> path = current.getPath();
		int currentFinishTime = 0;
		for (Node n: path){
			if (n.allocProc == processor){
				if (n.finishTime > currentFinishTime){
					currentFinishTime = n.finishTime;
				}
			}
		}
		return currentFinishTime;
	}


	//Function to determine heuristic cost f(s) of the state.
	public static double heuristicCost(Path state) {
		int maxTime = 0;
		int startTime = 0;
		Node maxNode = new Node();
		int bottomLevel = 0;
		ArrayList<Node> path = state.getPath();
		//Get the node with the latest finish time from the path.
		for (Node n: path){
			if (n.finishTime >= maxTime){
				maxTime = n.finishTime;
				maxNode = n;
			}
		}
		//Determine bottom level cost of node
		bottomLevel = ComputationalBottomLevel(maxNode);
		
		//Get start time of node
		startTime = maxNode.startTime;
		
		//Return startTime + the bottomLevel of the node, divided by the number of processors
		return (((double) startTime + (double) bottomLevel)/2);
	}
	
	//Recursive function to get the bottom level of the node for heuristic calculation.
	private static int ComputationalBottomLevel(Node node){
		int bottomLevel = 0;
		//Get outgoing edges of node
		Set<DefaultEdge> outgoingEdges = MainReadFile.graph.outgoingEdgesOf(node);
		//If node is a "SINK" (no successors), then return the node weight
		if (outgoingEdges.isEmpty()){
			return node.weight;
		//Otherwise call its successors, and recursively call the bottom level function.
		} else for (DefaultEdge e: outgoingEdges){
			Node successor = MainReadFile.graph.getEdgeTarget(e);
			int temp = ComputationalBottomLevel(successor);
			//Choose the highest path bottomLevel and continue.
			if (temp > bottomLevel){
				bottomLevel = temp;
			}
		}
		return (node.weight + bottomLevel);
	}

	
	//Function to get all the freeNodes for the expansion of the current state
	@SuppressWarnings("unchecked")
	private ArrayList<Node> freeNodes(StateWeights stateWeight){
		//This gets all the used nodes in the current path, then removes these nodes from all the nodes in the graph.
		ArrayList<Node> usedNodes = stateWeight.state.getPath();
		ArrayList<String> used = new ArrayList<String>();
		ArrayList<String> all = new ArrayList<String>();
		ArrayList<String> unused = new ArrayList<String>();
		Set<Node> allNodes = MainReadFile.graph.vertexSet();
		//Get all the nodes from graph in arraylist string format
		for (Node n: allNodes){
			all.add(n.name);
		}
		//Get all the nodes used in the path in arraylist string format
		for (Node n: usedNodes){
			used.add(n.name);
		}
		//Subtracted used from all nodes, to get the remaining nodes.
		all.removeAll(used);
		unused = (ArrayList<String>) all.clone();
		//This loops through all the remaining nodes, and checks to see if they pass the predecessor constraint.
		//Removes them if they don't.
		for (Node n: allNodes){
			Set<DefaultEdge> incomingEdges = MainReadFile.graph.incomingEdgesOf(n);
			for (DefaultEdge e: incomingEdges){
				Node edgeNode = MainReadFile.graph.getEdgeSource(e);
				if (unused.contains(edgeNode.name)){
					all.remove(n.name);	
				}
			}
		}
		//Add the freeNodes into an Arraylist of Nodes.
		ArrayList<Node> freeNodes = new ArrayList<Node>();
		for (Node n: allNodes){
			if (all.contains(n.name)){
				freeNodes.add(n);
			}
		}
		//Return the freeNodes for the path
		return freeNodes;
	}

	//Return true if state is the goal state, and thus the optimal solution
	public boolean  isComplete(StateWeights stateWeight) {
		ArrayList<Node> usedNodes = stateWeight.state.getPath();
		ArrayList<String> used = new ArrayList<String>();
		ArrayList<String> all = new ArrayList<String>();
		//Get all used nodes in the path
		for (Node n: usedNodes){
			used.add(n.name);
		}
		Set<Node> allNodes = MainReadFile.graph.vertexSet();
		//Get all the nodes in the graph
		for (Node n: allNodes){
			all.add(n.name);
		}
		//Check if all the nodes have been used, if yes, the optimal solution has been found.
		all.removeAll(used);
		if (all.isEmpty()){
			for (StateWeights s: closedQueue){
				System.out.print("\n New path");
				for (Node n: s.state.getPath()){
					System.out.print(n.name);
					System.out.print(" " + n.allocProc);
				}
			}
			for (StateWeights s: openQueue){
				System.out.print("\n New path");
				for (Node n: s.state.getPath()){
					System.out.print(n.name);
					System.out.print(" " + n.allocProc);
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
}
