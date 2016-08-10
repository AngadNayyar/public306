package processing_classes;

import java.util.ArrayList;

public class Processor {
	
	public int number; 
	public ArrayList<Node>schedule = new ArrayList<Node>(); 
	public int FinTime = 0; 
	
	public Processor(int num){
		this.number = num; 
	}
	
	public void addTask(Node node){
		this.schedule.add(node); 
	}

}
