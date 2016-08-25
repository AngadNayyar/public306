package a_star_implementation;

import java.util.ArrayList;

import processing_classes.TaskNode;

//Path contains an arraylist of nodes, thus keeping track of the assignment of nodes on the schedule.
public class Path {

	private ArrayList<TaskNode> path;
	private TaskNode currentNode;
	//currentNode is kept track of for quickly getting current node information from the Path.

	//constructor
	public Path(TaskNode new_state){
		path = new ArrayList<TaskNode>();
		path.add(new_state);
		currentNode = new_state;
	}

	//constructor
	public Path(Path existing, TaskNode next_state){
		path = new ArrayList<TaskNode>(existing.getPath());
		path.add(next_state);
		currentNode = next_state;
	}

	//constructor
	public Path(ArrayList<TaskNode> path2) {
		this.path = path2;
	}

	//getter for path
	public ArrayList<TaskNode> getPath(){
		return path;
	}

	//getter for currentNode
	public TaskNode getCurrent(){
		return currentNode;
	}

	//check if paths are equal
	public boolean equals(Object o){
		return path.equals(o);
	}

}
