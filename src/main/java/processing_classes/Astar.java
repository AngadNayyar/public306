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
		openQueue.add(null);
		
		while (!openQueue.isEmpty()){
			state = openQueue.peek();
			if (isComplete(state)){
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
		// S <- headOf(OPEN)
		//if s complete state then
		// return optimal solution S
		// Expand S to new states NEW
		//for all Si (E) NEW do
		// calculate F(si)
		// Insert si into OPEN, unless duplicate in CLOSED or OPEN
		// CLOSED <- CLOSED + s; OPEN <- OPEN - s;
	}
	
	private void expandState(NodeCostF state) {
		//Expand the state as in pseudo code. (must also add initial state (Si))
		
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
