package processing_classes;

//Possible future class for Graph, if we decide to implement our own.

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;

public class Graph {
	
	private DefaultDirectedWeightedGraph<Node, DefaultEdge> graph;

	Graph(DefaultDirectedWeightedGraph<Node, DefaultEdge> prevGraph){
		this.graph = prevGraph;
	}
	
	
	
}
