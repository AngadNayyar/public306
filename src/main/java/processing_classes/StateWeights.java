package processing_classes;

//This class represents the state, along with the weight of the state determined by the f(s) function
public class StateWeights implements Comparable<StateWeights> {
	public Path state;
	public Double pathWeight;
	
	//Constructor
	public StateWeights(Path state, Double pathWeight){
		this.state = state;
		this.pathWeight = pathWeight;
	}
	
	public Path getState(){
		return state;
	}
	
	public int compareTo(StateWeights second){
		return second.pathWeight.compareTo(pathWeight);
	}
}