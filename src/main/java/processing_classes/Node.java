package processing_classes;

/* 
 * Object for each node of the graph.
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
	
	@Override 
	public String toString(){
		return name; 
	}
	
}