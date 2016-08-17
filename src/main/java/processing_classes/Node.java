package processing_classes;

/* 
 * Object for each node of the graph. Keeps track of the vital details for each node also.
 */
public class Node {
	public String name;
	public int weight;
	public int startTime;
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
	
	public Node(Node other){
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
	
}