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

	//constructor
	public TaskNode(){
		name = "$";
		weight = 0;
		startTime = 0;
		allocProc = 0;
		finishTime = 0;	
	}

	//constructor
	public TaskNode(TaskNode other){
		name = other.name;
		weight = other.weight;
		startTime = other.startTime;
		allocProc = other.allocProc;
		finishTime = other.finishTime;
	}

	//setter for finish time
	public void setFinish(int f){
		this.finishTime = f;
	}

	//setter for start time
	public void setStart(int s){
		this.startTime = s;
	}

	//setter for processor number that the node is allocated to
	public void setProc(int p){
		this.allocProc = p;
	}

	//overriding toString function to return the name of the node
	@Override 
	public String toString(){
		return name; 
	}

	//override equals function to allow for correct comparison of nodes by name
	@Override
	public boolean equals(Object obj){
		TaskNode node = (TaskNode) obj; 
		if (this.name.equals(node.name)){	
			return true; 
		} else {
			return false;
		}
	}

	//override hashCode function from Set class to allow for correct comparison of sets
	//using our nodes
	@Override
	public int hashCode(){
		return 1; 
	}

}