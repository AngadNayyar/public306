import static org.junit.Assert.*;

import java.util.ArrayList;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

import processing_classes.Node;

public class TestProcessingClasses {

	/* 
	 * Test to check that the methods used in the processing_classes package are correct  
	 */

	
	// Test to check that the updateAllocation() method in Node is working correctly
	@Test
	public void updateTest() {
		Node testNode = new Node("num1", 2, 0, 0, 0);
		testNode.updateAllocation(20, 3);
		assertEquals(testNode.startTime, 18);
		assertEquals(testNode.finishTime, 20);
		assertEquals(testNode.allocProc, 3);
	}
	
	// Test: Check to see that the findParents() method in Node is working correctly
	@Test
	public void findParentsTest(){
		//create a small DAG to test method on
		DefaultDirectedWeightedGraph <Node, DefaultEdge> testgraph = new DefaultDirectedWeightedGraph <Node, DefaultEdge>(DefaultWeightedEdge.class);
		Node testNodeA = new Node("nodeA", 2, 0, 0, 0);
		Node testNodeB = new Node("nodeB", 2, 0, 0, 0);
		Node testNodeC = new Node("nodeC", 2, 0, 0, 0);
		testgraph.addVertex(testNodeA);
		testgraph.addVertex(testNodeB);
		testgraph.addVertex(testNodeC);
		testgraph.addEdge(testNodeA, testNodeC);
		testgraph.addEdge(testNodeB, testNodeC);
		
		ArrayList<Node> parents = testNodeC.findParents(testgraph);
		
		//Check that the size and contents of the parents ArrayList is correct
		assertEquals(parents.size(), 2);
		assertEquals(parents.get(0).name, "nodeA");
		assertEquals(parents.get(1).name, "nodeB");

	}

}
