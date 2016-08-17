package processing_classes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

/*
 * This class MainReadFile is the main class for the project. It processes the provided input file from command line
 * and converts it to a graph data structure.
 */
public class MainReadFile {
	
	//This is the graph data structure we are building from the input file,
	//that will be used to execute our algorithm
	public static DefaultDirectedWeightedGraph <Node, DefaultEdge> graph = new DefaultDirectedWeightedGraph <Node, DefaultEdge>(DefaultWeightedEdge.class);
	
	//This class holds the options given at the command line
	public static Options options = new Options();

	public static void main(String[] args) throws IOException, InterruptedException {
		
		String line;
		String nodeName, edgeOne, edgeTwo; 
		int nodeWeight, edgeWeight;
		Node node;
		HashMap <String, Node> hMap = new HashMap<String, Node>();
		
		//Reads in input file
		File inputfile = null;
		if (0 < args.length) {
		   inputfile = new File(args[0]);
		} else {
		   System.err.println("Invalid arguments count:" + args.length);
		}
		
		//Loops through each line of input file and creates node or edge, on which regular expression it matches
		BufferedReader br = new BufferedReader(new FileReader(inputfile));
		while ((line = br.readLine()) != null) {
			//Matches a node from input file
			//e.g. a [Weight=2]; 
			Pattern nodePattern = Pattern.compile("^([\\w]+)[\\s]*\\[[\\s]*Weight[\\s]*=([\\d]+)\\]"); 
			Matcher nodeMatch = nodePattern.matcher(line); 
			//Matches an edge from input file
			//e.g. a -> b [Weight=1];
			Pattern edgePattern = Pattern.compile("^([\\w]+)[\\s]*->[\\s]*([\\w]+)[\\s]*\\[[\\s]*Weight[\\s]*=([\\d]+)\\]"); 
			Matcher edgeMatch = edgePattern.matcher(line); 
			
			//If input line matches regular expression of a node add to hash map and graph data structure.
			while (nodeMatch.find()) {
				nodeName = nodeMatch.group(1);
				nodeWeight = Integer.parseInt(nodeMatch.group(2));
				node = new Node(nodeName, nodeWeight, 0,0,0);
				hMap.put(nodeName, node);
				graph.addVertex(node); 				
		    }	
			
			//If input line matches regular expression of an edge add to graph data structure.
			while (edgeMatch.find()) {
				edgeOne = edgeMatch.group(1);
				edgeTwo = edgeMatch.group(2);
				edgeWeight = Integer.parseInt(edgeMatch.group(3));
				DefaultEdge e = graph.addEdge(hMap.get(edgeOne), hMap.get(edgeTwo));
				graph.setEdgeWeight(e, edgeWeight);
		    }
		}
		
		//Read in number of processors from command line
		options.setNumProcessors(Integer.parseInt(args[1]));
		options.setOutputFileName(args[0]);
		
		//Read in optional options from command line
		for (int i = 2; i < args.length; i++) {
			//threads in parallel -p N
			if (args[i] == "-p") {
//				if () { check that number follows (if not throw exception)
					options.setNumThreads(Integer.parseInt(args[i+1]));
//				}
			//visualisation
			} else if (args[i] == "-v") {
				options.setVisualisation(true);
			//name of output file
			} else if (args[i] == "-o") {
//				if () { check that name follows (if not throw exception)
					options.setOutputFileName(args[i + 1]);
//				}
			}
		}
		
		//Create new instance of Astar solving algorithm, and then run the algorithm.
		Astar astarSolve = new Astar();
		astarSolve.solveAstar();
		
		OutputFile.fileWriter();
		br.close(); 
	}
	

}
