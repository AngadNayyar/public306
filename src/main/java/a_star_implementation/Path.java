package a_star_implementation;

import java.util.ArrayList;

import processing_classes.Node;

public class Path {
	
	private ArrayList<Node> path;
	Node currentState;

	Path(Node new_state){
		path = new ArrayList<Node>();
		path.add(new_state);
		currentState = new_state;
	}
	
	Path(Path existing, Node next_state){
		path = new ArrayList<Node>(existing.getPath());
		path.add(next_state);
		currentState = next_state;
	}

	Path(ArrayList<Node> existing, Node next_state){
		path = existing;
		path.add(next_state);
		currentState = next_state;
	}
	
	public ArrayList<Node> getPath(){
		return path;
	}
	
	public Node getCurrent(){
		return currentState;
	}
	
	public boolean equals(Object o){
		return path.equals(o);
	}
	
	
}
