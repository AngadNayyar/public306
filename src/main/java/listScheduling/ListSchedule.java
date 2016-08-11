package listScheduling;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import java.util.ArrayList;
import java.util.Set;

import processing_classes.MainReadFile;
import processing_classes.Node;


/**
 *  This class creates the list scheduling algorithm.  
 *  The list scheduling algorithm creates a list of the task nodes of the graph, in topological order, so every node is only added to the list after all of it's parents. 
 *  Then for each task item in the list try to add it to each of the processors, and calculate the earliest time that the task could complete based on earlier tasks, 
 *  it's parents and communication times. The allocated processor is selected as the option where the task would have the earliest finish time.  
 *
 **/
public class ListSchedule {

	// nodeList is the array list that will contain the nodes of the graph after the topological sort 
	// tempGraph is a copy of the graph - this is created so nodes can be deleted without deleting from the original 
	// procList is the array list of processors that store all of the processors to be scheduled onto 
	public static ArrayList<Node> nodeList = new ArrayList<Node>(); 
	private static DefaultDirectedWeightedGraph <Node, DefaultEdge> tempGraph = new DefaultDirectedWeightedGraph <Node, DefaultEdge>(DefaultWeightedEdge.class); 
	private static ArrayList<Processor> procList = new ArrayList<Processor>(); 

	
	// This method runs the list schedule algorithm - called from MainReadFile 
	public static void runListSchedule(DefaultDirectedWeightedGraph <Node, DefaultEdge> graph){
		
		// make the priority list 
		makepriorityList(graph); 
		// Call the schedule list function to take the array list created and schedule each task 
		scheduleList(); 
		
	}
	
	
	
	//Method to create the temporary graph object for use in the topological sort 
	public static void createTempGraph(DefaultDirectedWeightedGraph <Node, DefaultEdge> graph){
		// get the original graphs vertices, loop through all the vertices and add a copy to the temp graph  
		Set<Node> nodes = graph.vertexSet();
		for (Node node: nodes){
			tempGraph.addVertex(node); 	
		}

		// get the original graphs edges, loop through all the edges and add a copy to the temp graph  
		Set<DefaultEdge> edges = MainReadFile.graph.edgeSet() ;	
		for (DefaultEdge edge : edges){
			tempGraph.addEdge(MainReadFile.graph.getEdgeSource(edge), MainReadFile.graph.getEdgeTarget(edge)); 
		}	
	}


	// This method creates the list of the task nodes, ordering topologically, where all of the nodes are only added after their parents.  
	public static void makepriorityList(DefaultDirectedWeightedGraph <Node, DefaultEdge> graph){

		// Create the temporary graph 
		createTempGraph(graph); 

		// create an array list to keep track of the deleted nodes from the task graph 
		ArrayList<Node>deletedNodes = new ArrayList<Node>(); 

		// While there are still nodes in the graph, add them to the array list in order of precedence, and then delete them from the graph.  
		// This is done by adding vertices with no incoming edges (i.e. source) and then deleting the source nodes, and repeating 
		while (tempGraph.vertexSet().size() > 0){
			Set<Node> nodes = tempGraph.vertexSet();
			ArrayList<Node>tempNodeList = new ArrayList<Node>(nodes); 
			int length = nodes.size(); 
			for (int i=0; i<length; i++){
				// If it is a source node then add it to the priority array list.
				// Then delete this node from the graph to find the children by making them sources.
				if (false == deletedNodes.contains(tempNodeList.get(i))){
					if (tempGraph.inDegreeOf(tempNodeList.get(i)) == 0){
						nodeList.add(tempNodeList.get(i));
						tempGraph.removeVertex(tempNodeList.get(i));
						deletedNodes.add(tempNodeList.get(i));
					}
				}
			}
		}
	}

	// This method loops through each of the nodes in the priority list of task nodes, and allocates each one to a processor 
	public static void scheduleList(){

		// Create the processor objects - the number of which determined from command prompt input  
		int numProc = MainReadFile.options.getNumProcessors(); 
		for (int i=1; i<numProc+1; i++){
			Processor proc = new Processor(i); 
			procList.add(proc); 
		}


		// For each node in the priority node list, schedule it to a processor 
		for (Node node: nodeList){
			// If it is a source it can be simply scheduled to any processor - the processor with the earliest finish time 
			if (node.findParents(MainReadFile.graph).size() == 0){
				// call the function to find the processor to allocate the source 
				Processor allocatedProc = getMinFinishTimeSource(procList); 
				// Add the node to the processor, to keep track of the schedule on the processor and the node information
				allocatedProc.addTask(node, node.weight); 
				node.updateAllocation(allocatedProc.finTime, allocatedProc.number);
			} else {
				// If it is not a source, find its parents and the call the allocate processor function
				ArrayList<Node> parents = node.findParents(MainReadFile.graph); 
				allocateProcessor(node, parents); 
			}
		}
	}

	// This method allocates a node to a processor - where the task will finish at the earliest possible time (critical path).  
	public static void allocateProcessor (Node node, ArrayList<Node>parents){
		// Initialize the processor and minimum finish time 
		Processor allocProc = procList.get(0);
		int minFinTime = Integer.MAX_VALUE;
		int criticalPathTime = -1;

		// For each processor try to place the task node onto that processor and calculate the finish time. 
		// After looping through each processor,the processor with the earliest finish time of that task is 
		// selected as the allocated processor and the node and processor information is updated
		for (Processor proc : procList){

			// Look at all of the node's parents to see if they are on the current processor. 
			// If the parents are on this processor then there is no need to add communication, or any idle time and 
			// simply the node can be added to the end of the processor to calculate the start and finish time of the task. 
			Boolean allParentsOnProc = true; 
			for (Node parent: parents){
				if (parent.allocProc != proc.number) {
					allParentsOnProc = false; 
				}
			} 			
			if (allParentsOnProc){
				minFinTime = proc.finTime + node.weight;
				allocProc = proc;
			}

			// If the node's parents are on different nodes, then calculate the max length of the parent tasks  
			// along with the communication times for each of the processor
			else {
				criticalPathTime = -1;
				for (Node parent: parents){
					//if one of the parents is on the same processor then the earliest time that the 
					//task node can start is as soon as the processor becomes available
					if (parent.allocProc == proc.number) {
						int currentCriticalPathTime = proc.finTime + node.weight;
						if (criticalPathTime < currentCriticalPathTime) {
							criticalPathTime = currentCriticalPathTime;
						}
					} else {
						// for each parent that is on a different processor find the finish time of that parent 
						// and then the communication cost from that parent
						DefaultEdge edge = MainReadFile.graph.getEdge(parent, node);
						int communicationTime = (int) MainReadFile.graph.getEdgeWeight(edge);
						// set the critical path time to be the max of the parent + communication time or the current end time of the processor 
						int currentCriticalPathTime = Math.max((parent.finishTime + communicationTime), proc.finTime) + node.weight ;
						if (criticalPathTime < currentCriticalPathTime) {
							criticalPathTime = currentCriticalPathTime;
						}
					}
				}
			}

			// update the minFinTime and allocated processor to be the case where the processor allows for the earliest complet
			if ((criticalPathTime < minFinTime) &&  (criticalPathTime > -1)){
				minFinTime = criticalPathTime;
				allocProc = proc;
			}

		}

		// the processor that is selected as the allocated processor is the processor that when the task is ran on this 
		// processor it has the earliest finish time - update the objects accordingly 
		allocProc.addTask(node, minFinTime);
		node.updateAllocation(minFinTime, allocProc.number);
	}


	// Find the processor to allocate a source node by looking at only the earliest finish times of the processor 
	public static Processor getMinFinishTimeSource(ArrayList<Processor> procList){
		int minTime = procList.get(0).finTime; 
		Processor minProc = procList.get(0); 

		for (Processor proc : procList){
			if (proc.finTime < minTime){
				minTime = proc.finTime; 
				minProc = proc; 
			}
		}

		return minProc; 
	}


}
