package processing_classes;

import java.util.ArrayList;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;

/* 
 * Object for each node of the graph.
 */
public class Node {
	public String name;
	public int weight;
	public int startTime;
	public int allocProc;
	public int finishTime;
	
	
	//Constructor of Node class
	public Node(String n, int w, int s, int a, int f){
		name = n;
		weight = w;
		startTime = s;
		allocProc = a;
		finishTime = f;
	}
	
	public void setProcessor(int proc){
		this.allocProc = proc; 
	}
	
	public void setStartTime(int time){
		this.startTime = time; 
	}
	
	@Override 
	public String toString(){
		return name; 
	}
	
	public void updateAllocation(int finishTime, int allocProc){
		this.startTime = finishTime - this.weight;
		this.allocProc = allocProc;
		this.finishTime = finishTime;
		
	}
	
// This method finds the parent nodes of a vertex and returns them in an ArrayList.	
	public ArrayList<Node> findParents(DefaultDirectedWeightedGraph <Node, DefaultEdge> graph) {

		ArrayList<Node> parents = new ArrayList<Node>();
		Set<Node> vertices = graph.vertexSet(); 
		ArrayList<Node> vertexList = new ArrayList<Node>(vertices); 
		
		for (Node node: vertexList){
			if (node.name.equals(this.name)){
				Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(node);
				for(DefaultEdge edge : incomingEdges){
					parents.add(graph.getEdgeSource(edge));	
				}
			}
		}	
		
		return parents;
	}
	
}