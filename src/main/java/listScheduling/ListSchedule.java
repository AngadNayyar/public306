package listScheduling;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import processing_classes.MainReadFile;
import processing_classes.Node;
import processing_classes.Processor;

public class ListSchedule {

	private static ArrayList<Node> nodeList = new ArrayList<Node>(); 
	public static DefaultDirectedWeightedGraph <Node, DefaultEdge> tempGraph = new DefaultDirectedWeightedGraph <Node, DefaultEdge>(DefaultWeightedEdge.class); 

	public static void createTempGraph(){
		Set<Node> nodes = MainReadFile.graph.vertexSet();
		
		for (Node node: nodes){
			tempGraph.addVertex(node); 	
		}
		
		Set<DefaultEdge> edges = MainReadFile.graph.edgeSet() ;	
		for (DefaultEdge edge : edges){
			tempGraph.addEdge(MainReadFile.graph.getEdgeSource(edge), MainReadFile.graph.getEdgeTarget(edge)); 
		}	
		
	}
	
	
	public static void makepriorityList(){
		
		createTempGraph(); 
		
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
		
		scheduleList(); 
		
	}
	
	public static void scheduleList(){
		
		int numProc = MainReadFile.options.getNumProcessors(); 
		ArrayList<Processor> procList = new ArrayList<Processor>(); 
		
		for (int i=0; i<numProc; i++){
			Processor proc = new Processor(i); 
			procList.add(proc); 
		}
	
				
		// for each node in the node list schedule it to a processor 
		for (Node node: nodeList){
				// if it is a source it can be simply scheduled to any processor - this would be the processor with the earliest finish time 
			if (node.findParents(MainReadFile.graph).size() == 0){
				System.out.println(node.name);
				Processor allocatedProc = getMinFinishTime(procList); 
				allocatedProc.addTask(node); 
				System.out.println(allocatedProc.schedule.size());
			}		
			
		}
		
		
				
		
		
	}
	
	public static Processor getMinFinishTime(ArrayList<Processor> procList){
		
		int minTime = procList.get(0).FinTime; 
		Processor minProc = procList.get(0); 
		
		for (Processor proc : procList){
			if (proc.FinTime < minTime){
				minTime = proc.FinTime; 
				minProc = proc; 
			}
		}
		
		return minProc; 
		
		
		
	}
	

}
