package file_processing;

/* 
 * Object for each node of the graph.
 */
public class Node {
	public String name;
	public int weight;
	public int startTime;
	public int allocProc;
	
	//Constructor of Node class
	public Node(String n, int w, int s, int a){
		name = n;
		weight = w;
		startTime = s;
		allocProc = a;
	}
	
	@Override 
	public String toString(){
		return name; 
	}
	
}