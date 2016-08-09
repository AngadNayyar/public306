package processing_classes;

import java.util.ArrayList;

public class Path {
	
	private ArrayList<Node> path;
	Node currentNode;

	Path(Node new_state){
		path = new ArrayList<Node>();
		path.add(new_state);
		currentNode = new_state;
	}
	
	Path(Path existing, Node next_state){
		path = new ArrayList<Node>(existing.getPath());
		path.add(next_state);
		currentNode = next_state;
	}

	Path(ArrayList<Node> existing, Node next_state){
		path = existing;
		path.add(next_state);
		currentNode = next_state;
	}
	
	public ArrayList<Node> getPath(){
		return path;
	}
	
	public Node getCurrent(){
		return currentNode;
	}
	
	public boolean equals(Object o){
		return path.equals(o);
	}
	
	
}
