package processing_classes;

/* 
 * Object for each node of the graph. Keeps track of the vital details for each node also.
 */
public class TaskNode {
	public String name;
	public int weight;
	public int startTime;
	public int allocProc;
	public int finishTime;
	
	//Constructor of Node class
	public TaskNode(String n, int w, int s, int a, int f){
		name = n;
		weight = w;
		startTime = s;
		allocProc = a;
		finishTime = f;
	}
	
	public TaskNode(){
		name = "$";
		weight = 0;
		startTime = 0;
		allocProc = 0;
		finishTime = 0;	
	}
	
	public TaskNode(TaskNode other){
		name = other.name;
		weight = other.weight;
		startTime = other.startTime;
		allocProc = other.allocProc;
		finishTime = other.finishTime;
	}
	
	public void setFinish(int f){
		this.finishTime = f;
	}
	
	public void setStart(int s){
		this.startTime = s;
	}
	
	public void setProc(int p){
		this.allocProc = p;
	}
	
	@Override 
	public String toString(){
		return name; 
	}
	
	@Override
	public boolean equals(Object obj){
		TaskNode node = (TaskNode) obj; 
//		return true; 
//		if ((this.name.equals(node.name)) && this.startTime == node.startTime && this.allocProc == node.allocProc && this.finishTime == node.finishTime && this.weight == node.weight){
		if (this.name.equals(node.name)){	
			return true; 
		} else {
		return false;
		}
	}
	
	@Override
	public int hashCode(){
		return 1; 
	}
	
}