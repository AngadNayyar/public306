package processing_classes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Set;
import org.jgrapht.graph.DefaultEdge;

/*
 * This class OutputFile creates the output and then processes the graph data structure and prints 
 * to the output file (.dot file format) using PrintWriter.
 */
public class OutputFile {

	public static void fileWriter() throws FileNotFoundException {
		File outFile = new File(MainReadFile.options.getOutputFileName());
		PrintWriter writer = new PrintWriter(outFile);

		writer.println("digraph \"output\" {");

		//Loops through nodes and prints to output file.
		Set<TaskNode> nodes = MainReadFile.graph.vertexSet();
		for (TaskNode node : nodes){
			writer.println(node.name+"\t\t[Weight ="+node.weight+",Start="+node.startTime+",Processor="+node.allocProc+"];");
		}

		//Loops through edges and prints to output file.
		Set<DefaultEdge> edges = MainReadFile.graph.edgeSet() ;	
		for (DefaultEdge edge : edges){
			writer.println(MainReadFile.graph.getEdgeSource(edge)+ " -> "+ MainReadFile.graph.getEdgeTarget(edge)+"\t[Weight="+ MainReadFile.graph.getEdgeWeight(edge)+"];");
		}

		writer.println("}");

		writer.close();
	}
}
