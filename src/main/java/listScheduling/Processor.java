package listScheduling;

import java.util.ArrayList;

import processing_classes.Node;

public class Processor {
	
	public int number; 
	public ArrayList<Node>schedule = new ArrayList<Node>(); 
	public int finTime = 0; 
	
	public Processor(int num){
		this.number = num; 
	}
	
	public void addTask(Node node, int minTime){
		this.schedule.add(node); 
		this.finTime = minTime;
	}

	public void setFinTime(int time){
		this.finTime = time; 
	}
	
	public int getFinTime(){
		return this.finTime; 
	}

}
