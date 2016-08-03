package processing_classes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;


public class OutputFile {
	
	public static void fileWriter() throws FileNotFoundException {
		File outFile = new File("output.dot");
		PrintWriter writer = new PrintWriter(outFile);
		
		Set<Node> nodes = MainReadFile.graph.vertexSet();
		for (Node node : nodes){
			writer.println(node.name+"\t\t[Weight ="+node.weight+",Start="+node.startTime+",Processor="+node.allocProc+"];");
		}
		
		Set<DefaultEdge> edges = MainReadFile.graph.edgeSet() ;	
		for (DefaultEdge edge : edges){
			writer.println(MainReadFile.graph.getEdgeSource(edge)+ " -> "+ MainReadFile.graph.getEdgeTarget(edge)+"\t[Weight="+ MainReadFile.graph.getEdgeWeight(edge)+"]");
		}
		
		writer.close();
			
		
			
	//	PrintWriter writeOutput = new PrintWriter("Output.dot");
	//	writer.println();
	//	writer.close();
		
	//	digraph "outputExample" {
	//		a [Weight=2, St a r t=0, Pr o c e s s o r =1] ;
	//		b [Weight=3, St a r t=2, Pr o c e s s o r =1] ;
	//		a -> b [Weight=1] ;
	//		c [Weight=3, St a r t=4, Pr o c e s so r =2] ;
	//		a -> c [Weight=2] ;
	//		d [Weight=2, St a r t=7, Pr o c e s s o r =2] ;
	//		b -> d [Weight=2] ;
	//		c -> d [Weight=1] ;
	//		}
	}
}
