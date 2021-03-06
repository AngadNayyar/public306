package processing_classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

import a_star_implementation.AStarNoVis;
import a_star_implementation.AStarParent;
import a_star_implementation.AStarParr;
import a_star_implementation.AStarParrVis;
import a_star_implementation.Astar;
import a_star_implementation.Path;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.*;

import java.awt.*;

import javax.swing.*;


/*
 * This class MainReadFile is the main class for the project. It processes the provided input file from command line
 * and converts it to a graph data structure.
 */
public class MainReadFile {

	// This is the graph data structure we are building from the input file,
	// that will be used to execute our algorithm
	static DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph = new DefaultDirectedWeightedGraph<TaskNode, DefaultEdge>(DefaultWeightedEdge.class);
	private static SingleGraph visualGraph = new SingleGraph("visual");

	// This class holds the options given at the command line
	public static Options options = new Options();

	public static void main(String[] args) throws IOException,
	InterruptedException {

		// Reads in input file
		File inputfile = null;
		if (0 < args.length) {
			inputfile = new File(args[0]);
		} else {
			System.err.println("Invalid arguments count:" + args.length);
		}

		//create the taskgraph and the corresponding VisualisationGraph object
		graph = createGraph(inputfile); 
		VisualisationGraph visualGraphObj = new VisualisationGraph(visualGraph);

		// Read in number of processors from command line
		options.setNumProcessors(Integer.parseInt(args[1]));
		options.setOutputFileName(args[0]);

		// Read in optional options from command line
		for (int i = 2; i < args.length; i++) {
			// threads in parallel -p N
			if (args[i].equals("-p")) {
				// if () { check that number follows (if not throw exception)
				options.setNumThreads(Integer.parseInt(args[i + 1]));
				options.setParallel(true);
				// }
				// visualisation
			} else if (args[i].equals("-v")) {
				options.setVisualisation(true);
				// name of output file
			} else if (args[i].equals("-o")) {
				// if () { check that name follows (if not throw exception)
				options.setOutputFileName(args[i + 1]);
				// }
			}
		}	

		// Create new instance of Astar solving algorithm, and then run the
		// algorithm.
		if (options.getVisualisation()) {
			visualGraphObj.display();
		}

		//If statements to decide which class file to run.
		if (options.getVisualisation()){
			if (options.getParallel()){
				//Run Parallel with visualization class file
				AStarParrVis astarSolve = new AStarParrVis(graph, options, visualGraphObj);
				astarSolve.solveAstar();
			}else{
				//Run Sequential with visualization class file
				Astar astarSolve = new Astar(graph, options, visualGraphObj);
				astarSolve.solveAstar();
			}
		}else{
			if (options.getParallel()){
				//Run parallel without visual 
				AStarParr astarSolve = new AStarParr(graph, options);
				astarSolve.solveAstar();
			}else {
				//Run Sequential without visual class file
				AStarNoVis astarSolve = new AStarNoVis(graph, options);
				astarSolve.solveAstar();
			}
		}

		//write the solution to the output file
		OutputFile.fileWriter();
		//sleep the thread so user can see the final graph visualisation before the program closes
		Thread.sleep(1000);
		System.exit(0);

	}

	//function to create task graph from inputted data file
	public static DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> createGraph(File inputfile) throws IOException {
		String line, nodeName, edgeOne, edgeTwo;
		int nodeWeight, edgeWeight;
		TaskNode node;
		DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graphTemp = new DefaultDirectedWeightedGraph<TaskNode, DefaultEdge>(
				DefaultWeightedEdge.class);
		HashMap<String, TaskNode> hMap = new HashMap<String, TaskNode>();
		// Loops through each line of input file and creates node or edge, on
		// which regular expression it matches
		BufferedReader br = new BufferedReader(new FileReader(inputfile));
		while ((line = br.readLine()) != null) {
			// System.out.println(line);
			// Matches a node from input file
			// e.g. a [Weight=2];
			Pattern nodePattern = Pattern
					.compile("^[\\s]*([\\w]+)[\\s]*\\[[\\s]*Weight[\\s]*=([\\d]+)\\][\\s]*");
			Matcher nodeMatch = nodePattern.matcher(line);
			// Matches an edge from input file
			// e.g. a -> b [Weight=1];
			Pattern edgePattern = Pattern
					.compile("^[\\s]*([\\w]+)[\\s]*->[\\s]*([\\w]+)[\\s]*\\[[\\s]*Weight[\\s]*=([\\d]+)\\][\\s]*");
			Matcher edgeMatch = edgePattern.matcher(line);

			// If input line matches regular expression of a node add to hash
			// map and graph data structure.
			while (nodeMatch.find()) {
				nodeName = nodeMatch.group(1);
				nodeWeight = Integer.parseInt(nodeMatch.group(2));
				node = new TaskNode(nodeName, nodeWeight, 0, 0, 0);
				hMap.put(nodeName, node);
				graphTemp.addVertex(node);
				visualGraph.addNode(node.name);
			}

			// If input line matches regular expression of an edge add to graph
			// data structure.
			while (edgeMatch.find()) {
				edgeOne = edgeMatch.group(1);
				edgeTwo = edgeMatch.group(2);
				edgeWeight = Integer.parseInt(edgeMatch.group(3));
				DefaultEdge e = graphTemp.addEdge(hMap.get(edgeOne),
						hMap.get(edgeTwo));
				graphTemp.setEdgeWeight(e, edgeWeight);
				String edgeName = edgeOne + edgeTwo;
				visualGraph.addEdge(edgeName, hMap.get(edgeOne).name,
						hMap.get(edgeTwo).name, true);
			}
		}
		br.close();
		return graphTemp; 
	}

}
