package processing_classes;

import java.util.ArrayList;

public class Processor {
	
	public int number; 
	public ArrayList<Node>schedule = new ArrayList<Node>(); 
	public int finTime = 0; 
	
	public Processor(int num){
		this.number = num; 
	}
	
	public void addTask(Node node, int minTime){
		this.schedule.add(node); 
		this.finTime += minTime;
	}

}
