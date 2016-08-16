import static org.junit.Assert.*;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

import processing_classes.*;


public class TestAStar {

	@Test
	public void settingNodeTimeTest(){		
		Node testNode = new Node("a",2,0,1,2);
		Node expected2 = new Node("b",1,3,2,4);
		Node testNode2 = new Node("b",1,0,0,0);
		Node expected3 = new Node ("c",3,5,2,8);
		Node testNode3 = new Node ("c",3,0,0,0);
		
		MainReadFile.graph.addVertex(testNode);
		MainReadFile.graph.addVertex(testNode2);
		MainReadFile.graph.addVertex(testNode3);
		
		DefaultEdge e = MainReadFile.graph.addEdge(testNode, testNode2);
		MainReadFile.graph.setEdgeWeight(e, 1);
		DefaultEdge e2 = MainReadFile.graph.addEdge(testNode, testNode3);
		MainReadFile.graph.setEdgeWeight(e2, 3);
		
		
		Path testPath = new Path(testNode);
		Astar.setNodeTimes(testPath,testNode2,2);
		assertEquals(expected2.startTime,testNode2.startTime);
		
		Path testPath2 = new Path(testPath, expected2);
		Astar.setNodeTimes(testPath2, testNode3,2);
		assertEquals(expected3.startTime,testNode3.startTime);
	}
	
	@Test
	public void settingNodeTimeTest2(){
		Node testNode = new Node("a",2,0,2,2);
		Node expected2 = new Node("b",1,5,2,6);
		Node testNode2 = new Node("b",1,0,0,0);
		Node testNode3 = new Node ("c",3,2,2,5);
		
		MainReadFile.graph.addVertex(testNode);
		MainReadFile.graph.addVertex(testNode2);
		MainReadFile.graph.addVertex(testNode3);
		
		DefaultEdge e = MainReadFile.graph.addEdge(testNode, testNode2);
		MainReadFile.graph.setEdgeWeight(e, 1);
		DefaultEdge e2 = MainReadFile.graph.addEdge(testNode, testNode3);
		MainReadFile.graph.setEdgeWeight(e2, 3);
		
		Path testPath = new Path(testNode);
		Path testPath2 = new Path(testPath,testNode3);
		Astar.setNodeTimes(testPath2,testNode2,2);
		assertEquals(expected2.startTime,testNode2.startTime);
		
		
	}
	
	
}
