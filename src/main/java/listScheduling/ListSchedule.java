package listScheduling;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import java.util.ArrayList;
import java.util.Set;

import processing_classes.MainReadFile;
import processing_classes.Node;

public class ListSchedule {
	
	private ArrayList<Node> nodeList = new ArrayList<Node>(); 
	
	public static void makepriorityList(){
		
		Set<Node> nodes = MainReadFile.graph.vertexSet();
		
		for (Node node : nodes){
			// if it is a source 
			if (MainReadFile.graph.inDegreeOf(node) == 0){
				node.setProcessor(1); 
				
			}
		}
		
		
		
	}

}
