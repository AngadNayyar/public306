package a_star_implementation;

import java.util.ArrayList;

import processing_classes.TaskNode;

//Path contains an arraylist of nodes, thus keeping track of the assignment of nodes on the schedule.
public class Path {
	
	private ArrayList<TaskNode> path;
	private TaskNode currentNode;
	private double bottomLevel;
	private double idleTime;
	//currentNode is kept track of for quickly getting current node information from the Path.

	public Path(TaskNode new_state){
		path = new ArrayList<TaskNode>();
		path.add(new_state);
		currentNode = new_state;
	}
	
	public Path(Path existing, TaskNode next_state){
		path = new ArrayList<TaskNode>(existing.getPath());
		path.add(next_state);
		currentNode = next_state;
	}

	Path(ArrayList<TaskNode> existing, TaskNode next_state){
		path = existing;
		path.add(next_state);
		currentNode = next_state;
	}
	
	public Path(ArrayList<TaskNode> path2) {
		this.path = path2;
	}

	public ArrayList<TaskNode> getPath(){
		return path;
	}
	
	public TaskNode getCurrent(){
		return currentNode;
	}
	
	public boolean equals(Object o){
		return path.equals(o);
	}
	
	
}
