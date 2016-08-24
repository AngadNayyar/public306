package processing_classes;

import java.util.ArrayList;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import a_star_implementation.Path;
import a_star_implementation.StateWeights;

public class VisualisationGraph {

	private SingleGraph visualGraph = new SingleGraph("visual");
	private Viewer viewer;

	public VisualisationGraph(SingleGraph visualGraph){
		this.visualGraph = visualGraph;
	}

	public VisualisationGraph() {
	}

	public void update(StateWeights stateWeight, Options options){
		if (options.getParallel()){
			visualGraph.addAttribute("ui.title", "Parallel Graph Visualization");
		}else {
			visualGraph.addAttribute("ui.title", "Sequential Graph Visualization");
		}
		visualGraph.addAttribute("ui.stylesheet", "graph { fill-color: black; }");
		visualGraph.addAttribute("ui.stylesheet", "node { fill-color: white; }");
		visualGraph.addAttribute("ui.stylesheet", "edge { fill-color: white; }");
		visualGraph.addAttribute("ui.stylesheet", "node { size: 20px; stroke-mode: plain; stroke-color: white; stroke-width: 3; }");
		visualGraph.addAttribute("ui.stylesheet", "node { text-mode: normal; text-visibility-mode: normal;  text-background-mode: plain; }");
		visualGraph.addAttribute("ui.stylesheet", "node { fill-mode: dyn-plain;  text-alignment: center; }");
		visualGraph.addAttribute("ui.stylesheet", "node { text-background-color: white; text-style: bold; text-size: 15px; }");
		visualGraph.addAttribute("ui.stylesheet", "node { text-color: white; }");


		if (options.getVisualisation()) {
			Path current = stateWeight.state;
			ArrayList<TaskNode> nodePath = current.getPath();
			for (TaskNode node : nodePath) {
				if (!node.name.equals("$")) {
					Node n = visualGraph.getNode(node.name);
					n.addAttribute("ui.style", "fill-color: rgb(0, 204, 255);");
					n.addAttribute("ui.label", "Proc " + node.allocProc);
				}
			}
		}
	}

	public void updateNode(TaskNode node) {
		if (!node.name.equals("$")) {
			Node n = visualGraph.getNode(node.name);
			n.addAttribute("ui.style", "fill-color: rgb(255, 0, 102);");
		}
	}

	public void display() {
		this.viewer = this.visualGraph.display();
	}
	
}
