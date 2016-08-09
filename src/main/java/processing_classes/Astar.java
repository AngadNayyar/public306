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
		
		//Create the initial path and adds to the OPEN queue.
		Set<Node> nodeSet = graph.vertexSet();
		Node firstNode = nodeSet.iterator().next();			//Is this the first node? Its a set.
		Path firstPath = new Path(firstNode); 				//Creates the first path
		StateWeights state = new StateWeights(firstPath, 0.0);	//Create the state with initial path
		openQueue.add(state);								//Puts the initial state in the OPEN list.
		
		while (!openQueue.isEmpty()){
			//Gets the state with best f value, top of the queue, without removing it
			state = openQueue.peek();
			if (isComplete(state)){
				//Returns the optimal path
				return state.getPath();
			}
			
			//Expanding the state. what do with this?
			StateWeights expandedState = expandState(state, firstPath);			
			System.out.println(expandedState.toString());
			//Removes the state from open queue and adds to the closed queue.
			openQueue.remove();
			closedQueue.add(state);
			
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
	
	//Expand the state to form new states and for each new state check whether it is present in either 
	//the CLOSED or OPEN queue, if YES discard it otherwise insert into OPEN, as in pseudo code.
	//NOTE not complete
	private StateWeights expandState(StateWeights state, Path path) {
		
		
		//Gets the free nodes connected to the current state
		ArrayList<Node> freeNodes = freeNodes(state);
		Path cPath = path;
		Double newWeight = 0.0;
		Path newPath = null;
		
		//Create a new path for each of the free nodes on each of the processors? Do we need a nested for loop for each of the 
		//processors? This is just building a path with one processor I think.
		for (Node n: freeNodes){
			newPath = new Path(cPath, n); //does this work? builds on previous path, e.g previous iteration of the loop.
			newWeight = 0.0;
			cPath = newPath;
		}
		
		//Should this be in the for loop, add it to the OPEN queue each iteration or am I dumb.
		StateWeights newState = new StateWeights(newPath, newWeight);
		return newState;
		
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
		ArrayList<Node> freeNodes = null;
		freeNodes.addAll(allNodes);
		return freeNodes;
	}

	public boolean  isComplete(StateWeights state) {
		//If this state is a goal state.
		return false;
	}
	
	
}
