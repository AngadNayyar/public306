package processing_classes;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;

public class Astar {

	private DefaultDirectedWeightedGraph<Node, DefaultEdge> graph;
	private PriorityBlockingQueue<NodeCostF> openQueue = new PriorityBlockingQueue<NodeCostF>();
	private PriorityBlockingQueue<NodeCostF> closedQueue = new PriorityBlockingQueue<NodeCostF>();

	public void setGraph(DefaultDirectedWeightedGraph<Node, DefaultEdge> new_Graph){
		graph = new_Graph;
	}
	
	public Path solveAstar() throws InterruptedException{
		
		//Create the initial path and adds to the OPEN queue.
		Set<Node> nodeSet = graph.vertexSet();
		Node firstNode = nodeSet.iterator().next();			//Is this the first node? Its a set.
		Path firstPath = new Path(firstNode); 				//Creates the first path
		NodeCostF state = new NodeCostF(firstPath, 0.0);	//Create the state with initial path
		openQueue.add(state);								//Puts the initial state in the OPEN list.
		
		while (!openQueue.isEmpty()){
			//Gets the state with best f value, top of the queue, without removing it
			state = openQueue.peek();
			if (isComplete(state)){
				//Returns the optimal path
				return state.getPath();
			}
			
			//Expanding the state. what do with this?
			NodeCostF expandedState = expandState(state, firstPath);			
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
	private NodeCostF expandState(NodeCostF state, Path path) {
		
		
		//Gets the free nodes connected to the current state
		ArrayList<Node> freeNodes = freeStates(state);
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
		NodeCostF newState = new NodeCostF(newPath, newWeight);
		return newState;
		
	}
	
	private ArrayList<Node> freeStates(NodeCostF state){
		Node currentNode = state.states.getCurrent();
		ArrayList<Node> nodeList = new ArrayList<Node>();
		Set<DefaultEdge> incomingEdges = MainReadFile.graph.incomingEdgesOf(currentNode);
		for (DefaultEdge e: incomingEdges){
			Node n = MainReadFile.graph.getEdgeSource(e);
			nodeList.add(n);
		}
		return nodeList;
	}

	public boolean  isComplete(NodeCostF state) {
		//If this state is a goal state.
		return false;
	}
	
	
}
