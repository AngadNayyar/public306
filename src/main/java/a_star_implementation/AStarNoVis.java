package a_star_implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

//import processing_classes.MainReadFile;
import processing_classes.Options;
import processing_classes.TaskNode;

public class AStarNoVis {

	private PriorityBlockingQueue<StateWeights> openQueue = new PriorityBlockingQueue<StateWeights>();
	private PriorityBlockingQueue<StateWeights> newStates = new PriorityBlockingQueue<StateWeights>();
	private PriorityBlockingQueue<StateWeights> closedQueue = new PriorityBlockingQueue<StateWeights>();
	private int numProc;
	private DefaultDirectedWeightedGraph <TaskNode, DefaultEdge> graph = new DefaultDirectedWeightedGraph <TaskNode, DefaultEdge>(DefaultWeightedEdge.class);; 
	private Options options = new Options(); 
	
	public AStarNoVis(DefaultDirectedWeightedGraph <TaskNode, DefaultEdge> graph, Options options){
		this.graph = graph;
		this.options = options; 
	}
	
	public AStarNoVis(DefaultDirectedWeightedGraph <TaskNode, DefaultEdge> graph){
		this.graph = graph; 
	}
	
	public Path solveAstar() throws InterruptedException{
		
		//Set initial node for openQueue
		TaskNode initialNode = new TaskNode();
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
			expandState(stateWeight, options.getNumProcessors());
			}
			closedQueue.add(stateWeight);
		}
		
		return null;
	}
	
	//Sets the value of the chosen path onto the nodes of the graph
	private void setScheduleOnGraph(Path state) {
		Set<TaskNode> graphNodes = graph.vertexSet();		
		//Loops through nodes of the path and then the nodes of the graph
		//setting the values of passed Path into the graphs nodes
		for (TaskNode n : state.getPath() ){
			for (TaskNode g: graphNodes){
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
		ArrayList<TaskNode> freeNodes = freeNodes(stateWeight);
		//Determine new states from the freenodes, get their weights, and add to openQueue
		for (TaskNode n: freeNodes){
			for (int i = 1; i <= processors; i++){ 
				//Assign every free node to every available processor
				TaskNode newNode = new TaskNode(n);
				newNode.setProc(i); //Sets the processor for the newNode
				setNodeTimes(current, newNode, i); //Sets the start time, finish time for the newNode
				Path temp = new Path(current, newNode);
				double pathWeight = heuristicCost(temp, stateWeight);
				if (removePathDuplicates(new StateWeights(temp, pathWeight))){
					openQueue.add(new StateWeights(temp, pathWeight));
				}
			} 
			newStates.clear();
		}
	}
	
	public boolean removePathDuplicates(StateWeights newState){
		Iterator<StateWeights> itrO = openQueue.iterator();
		Iterator<StateWeights> itrC = closedQueue.iterator();
		ArrayList<TaskNode> newPath = newState.state.getPath();
		
		while(itrO.hasNext()){
			StateWeights temp = itrO.next();
			ArrayList<TaskNode> path = temp.state.getPath();
			if (path.containsAll(newPath)){
				return false;
			}
		}
		
		/*while(itrC.hasNext()){
			StateWeights temp = itrC.next();
			ArrayList<TaskNode> path = temp.state.getPath();
			if (path.containsAll(newPath)){
				return false;
			}
		}*/
		
		return true;
		
	}
	
	public boolean removeCurrentNodeDuplicates(StateWeights newState){
		Iterator<StateWeights> itr = newStates.iterator();
		TaskNode newNode = newState.state.getCurrent();
		
		while (itr.hasNext()){
			StateWeights temp = itr.next();
			TaskNode tempNode = temp.state.getCurrent();
			if (newNode.startTime == tempNode.startTime){
				if (newNode.name == tempNode.name){
					return false;
				}
			}
		}
		
		newStates.add(newState);
		return true;
		
	}
	
	//Function to determine the start and finish time for the node
	public void setNodeTimes(Path current, TaskNode newNode, int processor){
		Set<TaskNode> allNodes = graph.vertexSet();
		TaskNode graphNode = newNode;
		for (TaskNode n: allNodes){
			if (n.name == newNode.name){
				graphNode = n;
			}
		}
		//Get the set of incoming edges of the newNode
		Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(graphNode);
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
			int communicationTime = (int) graph.getEdgeWeight(e);
			
			
			//Gets the parent node end time and processor
			TaskNode parentNode = graph.getEdgeSource(e);
			ArrayList<TaskNode> setOfNodesInPath = current.getPath();
			
			//Needs to search through path to find parent node with the latest end time.
			for (TaskNode n: setOfNodesInPath){
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
	private static int latestEndTimeOnProcessor(Path current, int processor) {
		ArrayList<TaskNode> path = current.getPath();
		int currentFinishTime = 0;
		for (TaskNode n: path){
			if (n.allocProc == processor){
				if (n.finishTime > currentFinishTime){
					currentFinishTime = n.finishTime;
				}
			}
		}
		return currentFinishTime;
	}


	// Function to determine heuristic cost f(s) of the state.
	public double heuristicCost(Path state, StateWeights stateWeight) {
		int maxTime = 0;
		int startTime = 0;
		TaskNode maxNode = new TaskNode();
		int bottomLevel = 0;
		double newPathWeight = 0;
		double idleTime = 0;
		Set<TaskNode> allNodes = graph.vertexSet();
		ArrayList<TaskNode> path = state.getPath();
		double previousPathWeight = stateWeight.pathWeight;
		// Get the node with the latest finish time from the path.
		for (TaskNode n : path) {
			if (n.finishTime >= maxTime) {
				maxTime = n.finishTime;
				maxNode = n;
			}
		}
		// get graph vertex of our node
		TaskNode graphNode = maxNode;
		for (TaskNode n : allNodes) {
			if (n.name == maxNode.name) {
				graphNode = n;
			}
		}
		// Determine bottom level cost of node
		bottomLevel = ComputationalBottomLevel(graphNode);
		// Get start time of node
		startTime = maxNode.startTime;
		// Find the idle time for next node
		idleTime = getIdleTime(state, graphNode, stateWeight);
		// New path weight is startTime + the bottomLevel of the node, divided
		// by the number of processors
		newPathWeight = (double) startTime + (double) (bottomLevel + idleTime); /// MainReadFile.options.getNumProcessors();
		// If new path weight is bigger than previous, select it. Otherwise use
		// the previousPathWeight.
		if (newPathWeight > previousPathWeight) {
			return newPathWeight;
		} else {
			return previousPathWeight;
		}
	}
	// Recursive function to get the bottom level of the node for heuristic
	// calculation.
	private int ComputationalBottomLevel(TaskNode node) {
		int bottomLevel = 0;
		// Get outgoing edges of node
		Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(node);
		// If node is a "SINK" (no successors), then return the node weight
		if (outgoingEdges.isEmpty()) {
			return node.weight;
			// Otherwise call its successors, and recursively call the bottom
			// level function.
		} else
			for (DefaultEdge e : outgoingEdges) {
				TaskNode successor = graph.getEdgeTarget(e);
				int temp = ComputationalBottomLevel(successor);
				// Choose the highest path bottomLevel and continue.
				if (temp > bottomLevel) {
					bottomLevel = temp;
				}
			}
		return (node.weight + bottomLevel);
	}
	
	/*private double IdleTime(Path state){
		//This needs to compute idle time.
	}*/
	
	private double getIdleTime(Path state, TaskNode currentNode, StateWeights stateWeight) {
	// First we need to calculate the free nodes of the current state
	ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();
	ArrayList<TaskNode> parents = new ArrayList<TaskNode>();
	freeNodes = freeNodes(stateWeight);
	double earliestStartTime = Double.MAX_VALUE;
	double criticalParentFinTime = 0;
	ArrayList<Double> idleTime = new ArrayList<Double>();
	double dataReadyTime = 0;
	double nodeIdleTime = 0;
	// Use a for loop to go through each of the free nodes and calculate
	// earliest possible start time
	// for that node and which processor it would be on.
	for (TaskNode f : freeNodes) {
		parents.clear();
		Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(f);
		for (DefaultEdge incomingEdge : incomingEdges) {
			parents.add(graph.getEdgeSource(incomingEdge));
		}
		for (int i = 0; i < options.getNumProcessors(); i++) {
				for (TaskNode parent : parents) {
				if (parent.allocProc == i) {
					dataReadyTime = latestEndTimeOnProcessor(state, i);
				} else {
					DefaultEdge edge = graph.getEdge(parent, f);
					dataReadyTime = Math.max((parent.finishTime + graph.getEdgeWeight(edge)),
							latestEndTimeOnProcessor(state, i));
				}
				if (dataReadyTime > criticalParentFinTime) {
					criticalParentFinTime = dataReadyTime;
				}
			}
			if (criticalParentFinTime < earliestStartTime) {
				earliestStartTime = criticalParentFinTime;
			}
		}
		for (int i = 0; i < options.getNumProcessors(); i++) {
			double temp = earliestStartTime - latestEndTimeOnProcessor(state, i);
			if (temp > 0) {
				nodeIdleTime += temp;
			}
		}
		idleTime.add(nodeIdleTime);
		}
	return (Collections.min(idleTime)) / options.getNumProcessors();
	}
	
	//Function to get all the freeNodes for the expansion of the current state
	@SuppressWarnings("unchecked")
	private ArrayList<TaskNode> freeNodes(StateWeights stateWeight){
		//This gets all the used nodes in the current path, then removes these nodes from all the nodes in the graph.
		ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();
		ArrayList<String> used = new ArrayList<String>();
		ArrayList<String> all = new ArrayList<String>();
		ArrayList<String> unused = new ArrayList<String>();
		Set<TaskNode> allNodes = graph.vertexSet();
		//Get all the nodes from graph in arraylist string format
		for (TaskNode n: allNodes){
			all.add(n.name);
		}
		//Get all the nodes used in the path in arraylist string format
		for (TaskNode n: usedNodes){
			used.add(n.name);
		}
		//Subtracted used from all nodes, to get the remaining nodes.
		all.removeAll(used);
		unused = (ArrayList<String>) all.clone();
		//This loops through all the remaining nodes, and checks to see if they pass the predecessor constraint.
		//Removes them if they don't.
		for (TaskNode n: allNodes){
			Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(n);
			for (DefaultEdge e: incomingEdges){
				TaskNode edgeNode = graph.getEdgeSource(e);
				if (unused.contains(edgeNode.name)){
					all.remove(n.name);	
				}
			}
		}
		//Add the freeNodes into an Arraylist of Nodes.
		ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();
		for (TaskNode n: allNodes){
			if (all.contains(n.name)){
				freeNodes.add(n);
			}
		}
		//Return the freeNodes for the path
		return freeNodes;
	}

	//Return true if state is the goal state, and thus the optimal solution
	public boolean  isComplete(StateWeights stateWeight) {
		ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();
		ArrayList<String> used = new ArrayList<String>();
		ArrayList<String> all = new ArrayList<String>();
		//Get all used nodes in the path
		for (TaskNode n: usedNodes){
			used.add(n.name);
		}
		Set<TaskNode> allNodes = graph.vertexSet();
		//Get all the nodes in the graph
		for (TaskNode n: allNodes){
			all.add(n.name);
		}
		//Check if all the nodes have been used, if yes, the optimal solution has been found.
		all.removeAll(used);
		if (all.isEmpty()){
			/*for (StateWeights s: closedQueue){
				System.out.print("\n Closed path, weight: " + s.pathWeight + " ");
				for (Node n: s.state.getPath()){
					System.out.print(n.name);
					System.out.print(" " + n.allocProc);
				}
			}
			for (StateWeights s: openQueue){
				System.out.print("\n Open path, weight: " + s.pathWeight + " ");
				for (Node n: s.state.getPath()){
					System.out.print(n.name);
					System.out.print(" " + n.allocProc);
				}
			}*/
			return true;
		} else {
			return false;
		}
	}
	
}