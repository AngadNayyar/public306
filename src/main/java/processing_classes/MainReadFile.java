package processing_classes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;


public class MainReadFile {

	public static void main(String[] args) throws IOException {
		
		String line; 	
		String nodeName, edgeOne, edgeTwo; 
		int nodeWeight, edgeWeight;
		Node node;
		HashMap <String, Node> hMap = new HashMap<String, Node>();
		DefaultDirectedWeightedGraph <Node, DefaultEdge> graph = new DefaultDirectedWeightedGraph <Node, DefaultEdge>(DefaultEdge.class);
		
		File inputfile = null;
		if (0 < args.length) {
		   inputfile = new File(args[0]);
		} else {
		   System.err.println("Invalid arguments count:" + args.length);
		}
				
		BufferedReader br = new BufferedReader(new FileReader(inputfile));
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			Pattern nodePattern = Pattern.compile("^([\\w]+)[\\s]*\\[[\\s]*Weight[\\s]*=([\\d]+)\\]"); 
			Matcher nodeMatch = nodePattern.matcher(line); 
			
			Pattern edgePattern = Pattern.compile("^([\\w]+)[\\s]*->[\\s]*([\\w]+)[\\s]*\\[[\\s]*Weight[\\s]*=([\\d]+)\\]"); 
			Matcher edgeMatch = edgePattern.matcher(line); 
			
			while (nodeMatch.find()) {
				nodeName = nodeMatch.group(1);
				nodeWeight = Integer.parseInt(nodeMatch.group(2));
				node = new Node(nodeName, nodeWeight, 0,0);
				hMap.put(nodeName, node);
				
		    }	
		
			while (edgeMatch.find()) {
				edgeOne = edgeMatch.group(1);
				edgeTwo = edgeMatch.group(2);
				edgeWeight = Integer.parseInt(edgeMatch.group(3));
				//graph.addEdgeWithVertices()
		        //System.out.println("name: " + edgeOne);
		        //System.out.println("name: " + edgeTwo);
		        //System.out.println("weight: " + edgeWeight);
		    }
		
		}
		
		
		
		br.close(); 
		
		
		
	}
	

}
