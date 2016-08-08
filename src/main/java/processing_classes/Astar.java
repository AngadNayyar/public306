package processing_classes;

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
		
		NodeCostF state;
		//Puts the initial state in the OPEN list TODO replace the null with initial state?
		openQueue.add(null);
		
		while (!openQueue.isEmpty()){
			//Gets the state with best f value, top of the queue, without removing it
			state = openQueue.peek();
			if (isComplete(state)){
				//Returns the optimal path
				return state.getPath();
			}
			
			//Expanding the state.
			expandState(state);			
			
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
	

	private void expandState(NodeCostF state) {
		//Expand the state to form new states and for each new state check whether it is present in either 
		//the CLOSED or OPEN queue, if YES discard it otherwise insert into OPEN, as in pseudo code. 
		
	}
	
	private void freeStates(NodeCostF state){
		Node currentNode = state.states.getCurrent();
		Set<DefaultEdge> incomingEdges = MainReadFile.graph.incomingEdgesOf(currentNode);
		for (DefaultEdge e: incomingEdges){
			MainReadFile.graph.getEdgeSource(e);
		}
	}

	public boolean  isComplete(NodeCostF state) {
		//If this state is complete ie. everything is assigned return true.
		return false;
	}
	
	//Possible to have this as apart of the Node class
	//needs to represent a schedule AKA 
	private class NodeCostF implements Comparable<NodeCostF> {
		public Path states;
		public Double costF;

		NodeCostF(Path states, Double costF){
			this.states = states;
			this.costF = costF;
		}
		
		public Path getPath(){
			return states;
		}
		
		public int compareTo(NodeCostF second){
			return costF.compareTo(second.costF);
		}
	}
}
