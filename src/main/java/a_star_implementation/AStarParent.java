package a_star_implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

import processing_classes.Options;
import processing_classes.TaskNode;
import processing_classes.VisualisationGraph;

public class AStarParent {

	protected PriorityBlockingQueue<StateWeights> openQueue = new PriorityBlockingQueue<StateWeights>();
	protected PriorityBlockingQueue<StateWeights> newStates = new PriorityBlockingQueue<StateWeights>();
	protected PriorityBlockingQueue<StateWeights> closedQueue = new PriorityBlockingQueue<StateWeights>();
	protected int numProc;
	protected DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph = new DefaultDirectedWeightedGraph<TaskNode, DefaultEdge>(DefaultWeightedEdge.class);;
	protected Options options = new Options();
	protected VisualisationGraph visualGraphObj = new VisualisationGraph();
	protected CopyOnWriteArrayList<Path> threadPathList = new CopyOnWriteArrayList<Path>();

	// Sets the value of the chosen path onto the nodes of the graph
	protected void setScheduleOnGraph(Path state) {
		Set<TaskNode> graphNodes = graph.vertexSet();
		// Loops through nodes of the path and then the nodes of the graph
		// setting the values of passed Path into the graphs nodes
		for (TaskNode n : state.getPath()) {
			for (TaskNode g : graphNodes) {
				if (n.name.equals(g.name)) {
					g.setProc(n.allocProc);
					g.setStart(n.startTime);
				}
			}
		}
	}

	//Expands the given stateWeight into all possible new states. Store these in OPEN. We would check if it exists already,
	//or if it exists in CLOSE. Don't know how/the significance of it yet.
	protected void expandState(StateWeights stateWeight, int processors){
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
				StateWeights newState = new StateWeights(temp, 0.0);
				double pathWeight = heuristicCost(newState, stateWeight);
				boolean add = checkIfPathExists(newState.state, pathWeight);
				if (add){
					if (removeCurrentNodeDuplicates(newState)){
						openQueue.add(newState);
					}
				}
			} 
			newStates.clear();
		}
	}

	// This function checks to see if a equivalent schedule is already in the open queue or closed queue.
	// If an equivalent schedule exists then we can discard this current schedule (dont add to open queue)
	// Returns true if should add, false if already exists and dont add
	public boolean checkIfPathExists(Path temp, double pathWeight) {

		// if there are no states in the open queue, just add it 
		if (openQueue.isEmpty()) {
			return true;
		}

		ArrayList<StateWeights> similarSchedules = new ArrayList<StateWeights>();
		Set<Set> tempProcSet = new HashSet<Set>();

		// Create the sets for the schedule we want to add to the queue
		// One set is created per processor 
		for (int j = 0; j < options.getNumProcessors(); j++) {
			Set<TaskNode> nodes = new HashSet<TaskNode>();
			Path tempPath = new Path(temp.getPath());
			for (TaskNode tempNode : tempPath.getPath()) {
				if (tempNode.allocProc == j) {
					TaskNode copyNode = new TaskNode(tempNode);
					copyNode.setProc(1);
					nodes.add(copyNode);
				}
			}
			tempProcSet.add(nodes);
		}

		//Loop through the open queue and add any schedules that are of the same time length
		// Add them to similar schedules to inspect further
		Iterator<StateWeights> itr = openQueue.iterator();
		while (itr.hasNext()) {
			StateWeights schedule = itr.next();
			if (schedule.pathWeight == pathWeight
					&& (schedule.state.getPath().size() == temp.getPath()
					.size())) {
				similarSchedules.add(schedule);
			}
		}

		//Loop through the closed queue and add any schedules that are of the same time length
		// Add them to similar schedules to inspect further
		Iterator<StateWeights> closedITR = closedQueue.iterator();
		while (closedITR.hasNext()) {
			StateWeights schedule = closedITR.next();
			if (schedule.pathWeight == pathWeight
					&& (schedule.state.getPath().size() == temp.getPath()
					.size())) {
				similarSchedules.add(schedule);
			}
		}

		// for each of the similar schedules, check if the schedule is the same as 
		// the one we are trying to add 
		for (int k = 0; k < similarSchedules.size(); k++) {
			Set<Set> currentSet = new HashSet<Set>();
			for (int j = 0; j < options.getNumProcessors(); j++) {
				Set<TaskNode> nodes = new HashSet<TaskNode>();
				Path tempPath = new Path(
						similarSchedules.get(k).state.getPath());
				for (TaskNode tempNode : tempPath.getPath()) {
					if (tempNode.allocProc == j) {
						TaskNode copyNode = new TaskNode(tempNode);
						copyNode.setProc(1);
						nodes.add(copyNode);
					}
				}
				currentSet.add(nodes);
			}
			if (tempProcSet.containsAll(currentSet)
					&& currentSet.containsAll(tempProcSet)) {
				return false;
			}
		}

		return true;
	}

	//function to remove duplicate paths
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

		return true;

	}

	//function to remove duplicate paths
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

	// Function to determine the start and finish time for the node
	public void setNodeTimes(Path current, TaskNode newNode, int processor) {
		Set<TaskNode> allNodes = graph.vertexSet();
		TaskNode graphNode = newNode;
		for (TaskNode n : allNodes) {
			if (n.name == newNode.name) {
				graphNode = n;
			}
		}
		// Get the set of incoming edges of the newNode
		Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(graphNode);
		// End time of the last node to run on the processor
		int processorEndTime = latestEndTimeOnProcessor(current, processor);
		int parentEndTime = 0;
		int parentProcessor = processor;
		int latestAllowedTime;
		int t = 0;

		// If the set is empty, i.e no parents (dependencies) set start time to
		// processorEndTime.
		if (incomingEdges.isEmpty()) {
			newNode.setStart(processorEndTime);
			// If it does have parents, calculate the latest time newNode can
			// run on processor
		} else
			for (DefaultEdge e : incomingEdges) {
				int communicationTime = (int) graph.getEdgeWeight(e);

				// Gets the parent node end time and processor
				TaskNode parentNode = graph.getEdgeSource(e);
				ArrayList<TaskNode> setOfNodesInPath = current.getPath();

				// Needs to search through path to find parent node with the
				// latest end time.
				for (TaskNode n : setOfNodesInPath) {
					if (n.name.equals(parentNode.name)) {
						parentEndTime = n.finishTime;
						parentProcessor = n.allocProc;
					}
				}
				// Checks to see if communication time needs to be added to the
				// latest time allow by current parent
				if (parentProcessor != processor) {
					latestAllowedTime = parentEndTime + communicationTime;
				} else {
					latestAllowedTime = parentEndTime;
				}

				// If latestAllowed Time is the latest time found it is assigned
				// to t
				if (latestAllowedTime > t) {
					t = latestAllowedTime;
				}
			}

		// Sets the start time of the new node to which ever is larger t or
		// processorEndTime
		if (t > processorEndTime) {
			newNode.setStart(t);
		} else {
			newNode.setStart(processorEndTime);
		}

		// Sets the Finish time
		newNode.setFinish(newNode.weight + newNode.startTime);
	}

	// Calculates the end time of the last node (task) to run on the passed
	// processor
	private static int latestEndTimeOnProcessor(Path current, int processor) {
		ArrayList<TaskNode> path = current.getPath();
		int currentFinishTime = 0;
		for (TaskNode n : path) {
			if (n.allocProc == processor) {
				if (n.finishTime > currentFinishTime) {
					currentFinishTime = n.finishTime;
				}
			}
		}
		return currentFinishTime;
	}

	// Function to determine heuristic cost f(s) of the state.
	public double heuristicCost(StateWeights newState, StateWeights oldState) {
		int maxTime = 0;
		int startTime = 0;
		TaskNode maxNode = new TaskNode();
		double nodeBottomLevel = 0;
		double bottomLevel = 0;
		double idleTime = 0;
		Set<TaskNode> allNodes = graph.vertexSet();
		ArrayList<TaskNode> path = newState.state.getPath();
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
		nodeBottomLevel = ComputationalBottomLevel(graphNode);
		// Get start time of node
		startTime = maxNode.startTime;
		bottomLevel = (double) (nodeBottomLevel + startTime);

		idleTime = addToIdleTime(oldState, newState.state.getCurrent());

		newState.idleTime = oldState.idleTime + idleTime;

		if (bottomLevel > oldState.bottomLevel){
			newState.bottomLevel = bottomLevel;
		} else {
			newState.bottomLevel = oldState.bottomLevel;
		}

		if (newState.idleTime > newState.bottomLevel){
			newState.pathWeight = newState.idleTime;
			return newState.idleTime;
		} else {
			newState.pathWeight = newState.bottomLevel;
			return newState.bottomLevel;
		}
	}

	//Returns the idle time to be added to the expanded state
	private double addToIdleTime(StateWeights state, TaskNode nodeAdded){
		Path path = state.getState();
		int lastTimeOnProc = latestEndTimeOnProcessor(path, nodeAdded.allocProc);
		int idleTimeToAdd = nodeAdded.startTime - lastTimeOnProc;
		double idleTimeOnProc = (idleTimeToAdd + nodeAdded.weight) / options.getNumProcessors();
		return idleTimeOnProc;
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
						dataReadyTime = Math
								.max((parent.finishTime + graph
										.getEdgeWeight(edge)),
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
				double temp = earliestStartTime
						- latestEndTimeOnProcessor(state, i);
				if (temp > 0) {
					nodeIdleTime += temp;
				}
			}
			idleTime.add(nodeIdleTime);
		}
		return (Collections.min(idleTime)) / options.getNumProcessors();
	}

	// Function to get all the freeNodes for the expansion of the current state
	@SuppressWarnings("unchecked")
	private ArrayList<TaskNode> freeNodes(StateWeights stateWeight) {
		// This gets all the used nodes in the current path, then removes these
		// nodes from all the nodes in the graph.
		ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();
		ArrayList<String> used = new ArrayList<String>();
		ArrayList<String> all = new ArrayList<String>();
		ArrayList<String> unused = new ArrayList<String>();
		Set<TaskNode> allNodes = graph.vertexSet();
		// Get all the nodes from graph in arraylist string format
		for (TaskNode n : allNodes) {
			all.add(n.name);
		}
		// Get all the nodes used in the path in arraylist string format
		for (TaskNode n : usedNodes) {
			used.add(n.name);
		}
		// Subtracted used from all nodes, to get the remaining nodes.
		all.removeAll(used);
		unused = (ArrayList<String>) all.clone();
		// This loops through all the remaining nodes, and checks to see if they
		// pass the predecessor constraint.
		// Removes them if they don't.
		for (TaskNode n : allNodes) {
			Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(n);
			for (DefaultEdge e : incomingEdges) {
				TaskNode edgeNode = graph.getEdgeSource(e);
				if (unused.contains(edgeNode.name)) {
					all.remove(n.name);
				}
			}
		}
		// Add the freeNodes into an Arraylist of Nodes.
		ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();
		for (TaskNode n : allNodes) {
			if (all.contains(n.name)) {
				freeNodes.add(n);
			}
		}
		// Return the freeNodes for the path
		return freeNodes;
	}

	// Return true if state is the goal state, and thus the optimal solution
	public boolean isComplete(StateWeights stateWeight) {
		ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();
		ArrayList<String> used = new ArrayList<String>();
		ArrayList<String> all = new ArrayList<String>();
		// Get all used nodes in the path
		for (TaskNode n : usedNodes) {
			used.add(n.name);
		}
		Set<TaskNode> allNodes = graph.vertexSet();
		// Get all the nodes in the graph
		for (TaskNode n : allNodes) {
			all.add(n.name);
		}
		// Check if all the nodes have been used, if yes, the optimal solution
		// has been found.
		all.removeAll(used);
		if (all.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
}
