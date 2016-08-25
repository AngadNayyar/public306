package a_star_implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AbstractElement;
import org.graphstream.graph.implementations.SingleGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

//import processing_classes.MainReadFile;
import processing_classes.Options;
import processing_classes.TaskNode;
import processing_classes.VisualisationGraph;

public class Astar extends AStarParent{

	//constructor
	public Astar(DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph, Options options, VisualisationGraph visualGraphObj) {
		this.graph = graph;
		this.options = options;
		this.visualGraphObj = visualGraphObj;
	}

	//constructor
	public Astar(DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph) {
		this.graph = graph;
	}

	public Path solveAstar() throws InterruptedException {

		long startTime = System.currentTimeMillis();
		long counter = 500;

		// Property is set for rendering the visual graph
		System.setProperty("org.graphstream.ui.renderer",
				"org.graphstream.ui.j2dviewer.J2DGraphRenderer");

		// Set initial node for openQueue
		TaskNode initialNode = new TaskNode();
		Path initialPath = new Path(initialNode);
		StateWeights initialSW = new StateWeights(initialPath, 0.0);
		openQueue.add(initialSW);
		while (!openQueue.isEmpty()) {
			// Gets the state with best f value, remove this state from
			// openQueue
			StateWeights stateWeight = openQueue.poll();
			visualGraphObj.update(stateWeight, options);
			if (isComplete(stateWeight)) {
				// Returns the optimal path
				setScheduleOnGraph(stateWeight.getState());
				Thread.sleep(Math.max(counter, 0));
				counter -= 10;
				visualGraphObj.update(stateWeight, options);
				return stateWeight.getState();

			} else {
				// Expanding the state to all possible next states
				visualGraphObj.updateNode(stateWeight.state.getCurrent());
				expandState(stateWeight, options.getNumProcessors());
				Thread.sleep(Math.max(counter, 0));
				counter -= 10;
				visualGraphObj.update(stateWeight, options);
			}

			closedQueue.add(stateWeight);
		}
		return null;
	}
}
