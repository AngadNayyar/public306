package processing_classes;

import java.util.concurrent.PriorityBlockingQueue;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;

public class Astar {

	private DefaultDirectedWeightedGraph<Node, DefaultEdge> graph;
	private PriorityBlockingQueue<NodeCostF> openQueue = new PriorityBlockingQueue<NodeCostF>();

	public void setGraph(DefaultDirectedWeightedGraph<Node, DefaultEdge> new_Graph){
		graph = new_Graph;
	}
	
	public void solveAstar() throws InterruptedException{
		
		NodeCostF state;
		openQueue.add(null);
		
		while (!openQueue.isEmpty()){
			state = openQueue.take();
			if (isComplete(state)){
				//return st
			}
		}
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
	
	public boolean  isComplete(NodeCostF state) {
		//If this state is complete ie. everything is assigned return true.
		return false;
	}
	
	//Possible to have this as apart of the Node class
	private class NodeCostF implements Comparable<NodeCostF> {
		public Path states;
		public Double costF;

		NodeCostF(Node nodes, Double costF){
			this.states = states;
			this.costF = costF;
		}

		public int compareTo(NodeCostF second){
			return costF.compareTo(second.costF);
		}
	}
}
