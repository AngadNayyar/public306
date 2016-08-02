package processing_classes;

// Object for each node of the graph.
public class Node {
	public String name;
	public int weight;
	public int startTime;
	public int allocProc;
	
	public Node(String n, int w, int s, int a){
		name = n;
		weight = w;
		startTime = s;
		allocProc = a;
	}
	
}