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
	

	//Expands the given stateWeight into all possible new states. Store these in OPEN. We would check if it exists already,
	//or if it exists in CLOSE. Don't know how/the significance of it yet.
	private StateWeights expandState(StateWeights stateWeight, int processors){
		Path current = stateWeight.state;
		ArrayList<Node> freeNodes = freeNodes(stateWeight);
		
		for (Node n: freeNodes){
			for (int i = 1; i <= processors; i++){
				Node newNode = n;
				newNode.setProc(processors);
				
			}
		}
		
		return stateWeight;
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
