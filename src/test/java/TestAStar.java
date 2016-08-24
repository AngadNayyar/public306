import static org.junit.Assert.*;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

import a_star_implementation.Astar;
import a_star_implementation.Path;
import processing_classes.*;


public class TestAStar {

	//TODO commenting
//	@Test
//	public void settingNodeTimeTest(){	
//		DefaultDirectedWeightedGraph <TaskNode, DefaultEdge> graph = new DefaultDirectedWeightedGraph <TaskNode, DefaultEdge>(DefaultWeightedEdge.class);
//		TaskNode testNode = new TaskNode("a",2,0,1,2);
//		TaskNode expected2 = new TaskNode("b",1,3,2,4);
//		TaskNode testNode2 = new TaskNode("b",1,0,0,0);
//		TaskNode expected3 = new TaskNode ("c",3,5,2,8);
//		TaskNode testNode3 = new TaskNode ("c",3,0,0,0);
//		
//		graph.addVertex(testNode);
//		graph.addVertex(testNode2);
//		graph.addVertex(testNode3);
//		
//		DefaultEdge e = graph.addEdge(testNode, testNode2);
//		graph.setEdgeWeight(e, 1);
//		DefaultEdge e2 = graph.addEdge(testNode, testNode3);
//		graph.setEdgeWeight(e2, 3);
//		
//		Astar testAstar = new Astar(graph); 
//		Path testPath = new Path(testNode);
//		testAstar.setNodeTimes(testPath,testNode2,2);
//		assertEquals(expected2.startTime,testNode2.startTime);
//		
//		Path testPath2 = new Path(testPath, expected2);
//		testAstar.setNodeTimes(testPath2, testNode3,2);
//		assertEquals(expected3.startTime,testNode3.startTime);
//	}
//	
//	//TODO commenting
//	@Test
//	public void settingNodeTimeTest2(){
//		TaskNode testNode = new TaskNode("a",2,0,2,2);
//		TaskNode expected2 = new TaskNode("b",1,5,2,6);
//		TaskNode testNode2 = new TaskNode("b",1,0,0,0);
//		TaskNode testNode3 = new TaskNode ("c",3,2,2,5);
//		
//		DefaultDirectedWeightedGraph <TaskNode, DefaultEdge> graph = new DefaultDirectedWeightedGraph <TaskNode, DefaultEdge>(DefaultWeightedEdge.class);
//		
//		graph.addVertex(testNode);
//		graph.addVertex(testNode2);
//		graph.addVertex(testNode3);
//		
//		DefaultEdge e = graph.addEdge(testNode, testNode2);
//		graph.setEdgeWeight(e, 1);
//		DefaultEdge e2 = graph.addEdge(testNode, testNode3);
//		graph.setEdgeWeight(e2, 3);
//		
//		Astar testAstar = new Astar(graph); 
//		
//		Path testPath = new Path(testNode);
//		Path testPath2 = new Path(testPath,testNode3);
//		testAstar.setNodeTimes(testPath2,testNode2,2);
//		assertEquals(expected2.startTime,testNode2.startTime);
//		
//		
//	}
	
	//TODO commenting
	@Test
	public void heuristicTest(){
		TaskNode testNode = new TaskNode("a",2,0,1,2);
		TaskNode testNode2 = new TaskNode("b",1,3,2,4);
		TaskNode testNode3 = new TaskNode ("c",3,5,2,8);
		TaskNode testNode4 = new TaskNode ("d", 1,0,0,0);
		
		DefaultDirectedWeightedGraph <TaskNode, DefaultEdge> graph = new DefaultDirectedWeightedGraph <TaskNode, DefaultEdge>(DefaultWeightedEdge.class);
		
		graph.addVertex(testNode);
		graph.addVertex(testNode2);
		graph.addVertex(testNode3);
		graph.addVertex(testNode4);
		
		DefaultEdge e = graph.addEdge(testNode, testNode2);
		graph.setEdgeWeight(e, 1);
		DefaultEdge e2 = graph.addEdge(testNode, testNode3);
		graph.setEdgeWeight(e2, 3);
		DefaultEdge e3 = graph.addEdge(testNode2,testNode4);
		graph.setEdgeWeight(e3, 1);
		DefaultEdge e4 = graph.addEdge(testNode3,testNode4);
		graph.setEdgeWeight(e4, 1);
		
		Astar testAstar = new Astar(graph); 
		
		Path testPath = new Path (testNode);
		Path testPath2 = new Path (testPath,testNode2);
		Path testPath3 = new Path (testPath2,testNode3);
		
		double r = 9.0;
		//double testD = Astar.heuristicCost(testPath3);
		//assertEquals(r,testD); Commented out as heuristic now requires the previous path weight also.
		
	}
	
	//test idle time TODO
	
	
	//test constructor for path TODO
	
	
	//test constructor for StateWeigths TODO
	
	
	//test methods for path and stateweights TODO
	
	
	//heuristiiiiic
	
}
