package processing_classes;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;

public class Astar {

	private DefaultDirectedWeightedGraph<Node, DefaultEdge> graph;
	private PriorityBlockingQueue<StateWeights> openQueue = new PriorityBlockingQueue<StateWeights>();
	private PriorityBlockingQueue<StateWeights> closedQueue = new PriorityBlockingQueue<StateWeights>();

	
	public Path solveAstar() throws InterruptedException{
		
		
		while (!openQueue.isEmpty()){
			//Gets the state with best f value, top of the queue, without removing it
			StateWeights stateWeight = openQueue.poll();
			if (isComplete(stateWeight)){
				//Returns the optimal path
				return stateWeight.getState();
			} else {
			//Expanding the state. what do with this?
			expandState(stateWeight, 2); //<-- shouldn't this be variable processor or similar			
//			System.out.println(expandedState.toString());
			//Removes the state from open queue and adds to the closed queue.
			openQueue.remove();
			closedQueue.add(stateWeight);
			}
			
		}
		
		
		return null;
		//OPEN priority queue required, ordered by ascending f values
		//OPEN <- S(init)
		//while OPEN =/= Empty
		// 	S <- headOf(OPEN)
		//	if s complete state then
		// 		return optimal solution S
		// 	Expand S to new states NEW
		//	for all Si (E) NEW do
		// 	calculate F(si)
		// 	Insert si into OPEN, unless duplicate in CLOSED or OPEN
		// 	CLOSED <- CLOSED + s; OPEN <- OPEN - s;
	}
	

	//Expands the given stateWeight into all possible new states. Store these in OPEN. We would check if it exists already,
	//or if it exists in CLOSE. Don't know how/the significance of it yet.
	private void expandState(StateWeights stateWeight, int processors){
		Path current = stateWeight.state;
		ArrayList<Node> freeNodes = freeNodes(stateWeight);
		
		for (Node n: freeNodes){
			for (int i = 1; i <= processors; i++){ 
				Node newNode = n;
				newNode.setProc(i);
				setNodeTimes(current, newNode, i); //Sets the start time, finish time for the newNode
				Path temp = new Path(current, newNode);
				double pathWeight = heuristicCost(temp);
				//Would check here to see if it exists in open or closed already, but unsure how to do that yet.
				openQueue.add(new StateWeights(temp, pathWeight));
			}
		}
	}

	//Function to determine the start and finish time for the node
	private void setNodeTimes(Path current, Node newNode, int processor){
		
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
			
			//Needs to search through path to find parent node
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
		//Sets the Finish time
		newNode.setFinish(newNode.weight + newNode.startTime);
	}
	
	//Calculates the end time of the last node (task) to run on the passed processor
	private int latestEndTimeOnProcessor(Path current, int processor) {
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
	private double heuristicCost(Path state) {
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
		
		//Return startTime + the bottomLevel of the node
		return ((double) startTime + (double) bottomLevel);
	}
	
	//Recursive function to get the bottom level of the node for heuristic calculation.
	private int ComputationalBottomLevel(Node node){
		int bottomLevel = 0;
		//Get outgoing edges of node
		Set<DefaultEdge> outgoingEdges = MainReadFile.graph.outgoingEdgesOf(node);
		//If node is a "SINK" (no successors), then return the node weight
		if (outgoingEdges.isEmpty()){
			return node.weight;
		//Otherwise call its successors, and recursively call the bottom level function.
		} else for (DefaultEdge e: outgoingEdges){
			Node successor = MainReadFile.graph.getEdgeSource(e);
			int temp = ComputationalBottomLevel(successor);
			//Choose the highest path bottomLevel and continue.
			if (temp > bottomLevel){
				bottomLevel = temp;
			}
		}
		return (node.weight + bottomLevel);
	}

	
	//Function to get all the freeNodes for the expansion of the current state
	private ArrayList<Node> freeNodes(StateWeights stateWeight){
		//This gets all the used nodes in the current path, then removes these nodes from all the nodes in the graph.
		ArrayList<Node> usedNodes = stateWeight.state.getPath();
		Set<Node> allNodes = MainReadFile.graph.vertexSet();
		allNodes.removeAll(usedNodes);
		
		//This loops through all the remaining nodes, and checks to see if they pass the predecessor constraint.
		for (Node n: allNodes){
			Set<DefaultEdge> incomingEdges = MainReadFile.graph.incomingEdgesOf(n);
			for (DefaultEdge e: incomingEdges){
				Node edgeNode = MainReadFile.graph.getEdgeSource(e);
				if (allNodes.contains(edgeNode)){
					allNodes.remove(edgeNode);
				}
			}
		}
		ArrayList<Node> freeNodes = new ArrayList<Node>();
		freeNodes.addAll(allNodes);
		return freeNodes;
	}

	//Return true if state is the goal state, and thus the optimal solution
	public boolean  isComplete(StateWeights stateWeight) {
		ArrayList<Node> usedNodes = stateWeight.state.getPath();
		Set<Node> allNodes = MainReadFile.graph.vertexSet();
		allNodes.removeAll(usedNodes);
		if (allNodes.isEmpty()){
			return true;
		} else {
			return false;
		}
	}
	
	
}
