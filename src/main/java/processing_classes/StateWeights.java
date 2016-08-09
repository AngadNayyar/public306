package processing_classes;

//Possible to have this as apart of the Node class
//needs to represent a schedule AKA 
public class StateWeights implements Comparable<StateWeights> {
	public Path states;
	public Double costF;
	
	//Constructor
	public StateWeights(Path states, Double costF){
		this.states = states;
		this.costF = costF;
	}
	
	public Path getPath(){
		return states;
	}
	
	public int compareTo(StateWeights second){
		return costF.compareTo(second.costF);
	}
}