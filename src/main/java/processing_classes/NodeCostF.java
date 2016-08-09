package processing_classes;

//Possible to have this as apart of the Node class
//needs to represent a schedule AKA 
public class NodeCostF implements Comparable<NodeCostF> {
	public Path states;
	public Double costF;
	
	//Constructor
	public NodeCostF(Path states, Double costF){
		this.states = states;
		this.costF = costF;
	}
	
	public Path getPath(){
		return states;
	}
	
	public int compareTo(NodeCostF second){
		return costF.compareTo(second.costF);
	}
}