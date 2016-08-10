package listScheduling;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import processing_classes.MainReadFile;
import processing_classes.Node;

public class ListSchedule {

	private static ArrayList<Node> nodeList = new ArrayList<Node>(); 
	public static DefaultDirectedWeightedGraph <Node, DefaultEdge> tempGraph = MainReadFile.graph;

	public static void makepriorityList(){
		
		// Get the set of all the nodes in the graph and convert to an array list 
		Set<Node> nodes = tempGraph.vertexSet();
		ArrayList<Node>tempNodeList = new ArrayList<Node>(nodes); 

		// While there are still nodes in the graph, add them to the array list in order of precedence 
		while (tempGraph.vertexSet().size() > 0){
			int length = nodes.size(); 
			for (int i=0; i<length; i++){
				// If it is a source node then add it to the priority array list.
				// Then delete this node from the graph to find the children by making them sources.  
				if (tempGraph.inDegreeOf(tempNodeList.get(i)) == 0){
					nodeList.add(tempNodeList.get(i));
					tempGraph.removeVertex(tempNodeList.get(i));
				}
			}
		}
		
//		for (Node node : nodeList){
//			System.out.println(node.toString());
//		}

	}
	

}
