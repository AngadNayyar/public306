import static org.junit.Assert.*;

import java.util.ArrayList;

import listScheduling.ListSchedule;
import listScheduling.Processor;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

import processing_classes.MainReadFile;
import processing_classes.Node;


public class TestListScheduling {
	
	
	/* 
	 * Test to check that the methods used in the list scheduling package are correct  
	 */
	
	
	// Test: Check to see that the min finish time of the processor is correctly found by getMinFinishTimeSource()
	@Test
	public void testGetMinFinishTimeSource(){
		ArrayList<Processor> procList = new ArrayList<Processor>();
		
		// Create two processors and store in proc list 
		// make processor one fin time 0
		Processor proc1 = new Processor(1); 
		proc1.setFinTime(0);
		procList.add(proc1); 
		// and processor 2 fin time a lot later - 100 
		Processor proc2 = new Processor(1); 
		procList.add(proc2); 
		proc2.setFinTime(100);
		
		// Check which processor the source task would be added to, it should be the proc 1 as it would 
		// indicate a lot earlier finish time for the task 
		Processor allocProc = ListSchedule.getMinFinishTimeSource(procList); 
		
		assertEquals(allocProc.number , proc1.number);
	
	}
	
	// Test: Check to see that a priority list is correctly made from a graph 
	// This test ensures that all dependencies are respected when generated the topologically ordered priority list 
	@Test
	public void testMakePriorityListSmall(){
		DefaultDirectedWeightedGraph <Node, DefaultEdge> graph = new DefaultDirectedWeightedGraph <Node, DefaultEdge>(DefaultWeightedEdge.class);
		
		// create 5 nodes to add to the graph 
		Node node1 = new Node("a", 0, 0, 0, 0);
		Node node2 = new Node("b", 0, 0, 0, 0);
		Node node3 = new Node("c", 0, 0, 0, 0);
		Node node4 = new Node("d", 0, 0, 0, 0);
		
		graph.addVertex(node1); 
		graph.addVertex(node2); 
		graph.addVertex(node3); 
		graph.addVertex(node4); 

		// Make an edge from a->c b->c and c->d 
		graph.addEdge(node1, node3); 
		graph.addEdge(node2, node3); 
		graph.addEdge(node3, node4); 	
		
		ListSchedule.makepriorityList(graph);
		
		ArrayList<Node> nodeList = ListSchedule.nodeList;
		ArrayList<Node> testList = new ArrayList<Node>(); 
		testList.add(node1); 
		testList.add(node2); 
		testList.add(node3); 
		testList.add(node4); 
				
		assertEquals(testList , nodeList);
	
	}


}
