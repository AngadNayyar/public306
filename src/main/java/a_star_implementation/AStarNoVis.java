package a_star_implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;



//import processing_classes.MainReadFile;
import processing_classes.Options;
import processing_classes.TaskNode;

public class AStarNoVis extends AStarParent{
	
	public AStarNoVis(DefaultDirectedWeightedGraph <TaskNode, DefaultEdge> graph, Options options){
		this.graph = graph;
		this.options = options; 
	}
	
	public AStarNoVis(DefaultDirectedWeightedGraph <TaskNode, DefaultEdge> graph){
		this.graph = graph; 
	}
	
	public Path solveAstar() throws InterruptedException{
		//Set initial node for openQueue
		TaskNode initialNode = new TaskNode();
		Path initialPath = new Path(initialNode);
		StateWeights initialSW = new StateWeights(initialPath,0.0);
		openQueue.add(initialSW);
		
		while (!openQueue.isEmpty()){
			
			//Gets the state with best f value, remove this state from openQueue
			StateWeights stateWeight = openQueue.poll();
			if (isComplete(stateWeight)){				
				//Returns the optimal path
				setScheduleOnGraph(stateWeight.getState());
				return stateWeight.getState();
			} else {
			//Expanding the state to all possible next states
			expandState(stateWeight, options.getNumProcessors());
			}
			closedQueue.add(stateWeight);
		}
		
		return null;
	}
	
}