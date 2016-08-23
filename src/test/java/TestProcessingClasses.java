import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

import processing_classes.MainReadFile;
import processing_classes.Options;
import processing_classes.TaskNode;


/* 
 * Test to check that the methods used in the processing_classes package are correct  
 */
public class TestProcessingClasses {

	
	// Test to check that the task node is created correctly
	@Test
	public void testTaskNodeCorrectlyCreated(){
		TaskNode task = new TaskNode("test", 3, 4, 2, 10);
		assertEquals(task.name, "test");
		assertEquals(task.weight, 3);
		assertEquals(task.startTime, 4);
		assertEquals(task.allocProc, 2);
		assertEquals(task.finishTime, 10);
		
	}
	
	// Test to check the output file name split works and file name correctly created 
	@Test
	public void testOutputFileNameCreatedCorrectly(){
		String inputFileName = "testfilename.dot"; 
		Options option = new Options(); 
		
		option.setOutputFileName(inputFileName);
		assertEquals(option.getOutputFileName(), "testfilename-output.dot"); 
	}
	
	@Test 
	public void testGraphCorrectlyReadIn() throws IOException{
		File file = new File("test.dot"); 
		DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graphTest = MainReadFile.createGraph(file); 
		DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graphcheck = new DefaultDirectedWeightedGraph<TaskNode, DefaultEdge>(
				DefaultWeightedEdge.class);
		
//		HOW TO CHECK IF GRAPHS THE SAME BUT NOT SAME OBJ?
//		TaskNode testNodeA = new TaskNode("a", 2, 0, 0, 0);
//		TaskNode testNodeB = new TaskNode("b", 3, 0, 0, 0);
//		TaskNode testNodeC = new TaskNode("c", 3, 0, 0, 0);
//		TaskNode testNodeD = new TaskNode("d", 2, 0, 0, 0);
//		graphcheck.addVertex(testNodeA);
//		graphcheck.addVertex(testNodeB);
//		graphcheck.addVertex(testNodeC);
//		graphcheck.addVertex(testNodeD);
//		graphcheck.addEdge(testNodeA, testNodeB);
//		graphcheck.addEdge(testNodeA, testNodeC);
//		graphcheck.addEdge(testNodeB, testNodeD);
//		graphcheck.addEdge(testNodeC, testNodeD);
			
		Set<TaskNode> nodes = graphTest.vertexSet();
		assertEquals(nodes.size(), 4); 
		Set<DefaultEdge> edges = graphTest.edgeSet(); 
		assertEquals(edges.size(), 4); 
	}
	
	
	//test for checking getting options correctly and creating object correctly TODO


	//test for correct writing of output file TODO
	
	
	//test for checking visual graph being created correctly (from task graph) TODO
	
	
	//test constructors of each class files? TODO
	
	
	//
	
}