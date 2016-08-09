package processing_classes;

//Possible to have this as apart of the Node class
//needs to represent a schedule AKA 
public class StateWeights implements Comparable<StateWeights> {
	public Path state;
	public Double costF;
	
	//Constructor
	public StateWeights(Path state, Double costF){
		this.state = state;
		this.costF = costF;
	}
	
	public Path getPath(){
		return state;
	}
	
	public int compareTo(StateWeights second){
		return costF.compareTo(second.costF);
	}
}