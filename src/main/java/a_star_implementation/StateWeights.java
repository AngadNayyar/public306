package a_star_implementation;


//This class represents the state, along with the weight of the state determined by the f(s) function
public class StateWeights implements Comparable<StateWeights> {
	public Path state;
	public Double pathWeight;
	public Double bottomLevel;
	public Double idleTime;

	//Constructor
	public StateWeights(Path state, Double pathWeight){
		this.state = state;
		this.pathWeight = pathWeight;
		this.bottomLevel = 0.0;
		this.idleTime = 0.0;
	}

	//getter for state
	public Path getState(){
		return state;
	}

	//compare states using pathWeights
	public int compareTo(StateWeights second){
		return pathWeight.compareTo(second.pathWeight);
	}
}