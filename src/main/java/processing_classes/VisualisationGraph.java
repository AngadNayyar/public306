package processing_classes;

import java.util.ArrayList;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import a_star_implementation.Path;
import a_star_implementation.StateWeights;

public class VisualisationGraph {
	
	private SingleGraph visualGraph = new SingleGraph("visual");
	
	public VisualisationGraph(SingleGraph visualGraph){
		this.visualGraph = visualGraph;
	}
	
	public VisualisationGraph() {
	}

	public void update(StateWeights stateWeight, Options options){
		if (options.getVisualisation()) {
			Path current = stateWeight.state;
			ArrayList<TaskNode> nodePath = current.getPath();
			for (TaskNode node : nodePath) {
				if (!node.name.equals("$")) {
					Node n = visualGraph.getNode(node.name);
					n.addAttribute("ui.style", "fill-color: rgb(255,0,0);");
					n.addAttribute("ui.label", node.allocProc);
				}
			}
		}
	}

	public void display() {
		this.visualGraph.display();
	}


}
