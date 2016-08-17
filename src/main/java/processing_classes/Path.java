package processing_classes;

import java.util.ArrayList;

//Path contains an arraylist of nodes, thus keeping track of the assignment of nodes on the schedule.
public class Path {
	
	private ArrayList<Node> path;
	private Node currentNode;
	//currentNode is kept track of for quickly getting current node information from the Path.

	public Path(Node new_state){
		path = new ArrayList<Node>();
		path.add(new_state);
		currentNode = new_state;
	}
	
	public Path(Path existing, Node next_state){
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
