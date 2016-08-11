package processing_classes;

/* 
 * Object for each node of the graph.
 */
public class Node {
	public String name;
	public int weight;
	public int startTime; //TODO should we change these to double
	public int allocProc;
	public int finishTime;
	
	//Constructor of Node class
	public Node(String n, int w, int s, int a, int f){
		name = n;
		weight = w;
		startTime = s;
		allocProc = a;
		finishTime = f;
	}
	
	public Node(){
		name = "$";
		weight = 0;
		startTime = 0;
		allocProc = 0;
		finishTime = 0;	
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
	
}