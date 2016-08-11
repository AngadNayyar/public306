package listScheduling;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import processing_classes.MainReadFile;
import processing_classes.Node;
import processing_classes.Processor;

public class ListSchedule {

	private static ArrayList<Node> nodeList = new ArrayList<Node>(); 
	private static DefaultDirectedWeightedGraph <Node, DefaultEdge> tempGraph = new DefaultDirectedWeightedGraph <Node, DefaultEdge>(DefaultWeightedEdge.class); 
	private static ArrayList<Processor> procList = new ArrayList<Processor>(); 



	public static void createTempGraph(){
		Set<Node> nodes = MainReadFile.graph.vertexSet();

		for (Node node: nodes){
			tempGraph.addVertex(node); 	
		}

		Set<DefaultEdge> edges = MainReadFile.graph.edgeSet() ;	
		for (DefaultEdge edge : edges){
			tempGraph.addEdge(MainReadFile.graph.getEdgeSource(edge), MainReadFile.graph.getEdgeTarget(edge)); 
		}	

	}


	public static void makepriorityList(){

		createTempGraph(); 

		// Get the set of all the nodes in the graph and convert to an array list 
//		Set<Node> nodes = tempGraph.vertexSet();
//
//		ArrayList<Node>tempNodeList = new ArrayList<Node>(nodes); 
		ArrayList<Node>deletedNodes = new ArrayList<Node>(); 

		// While there are still nodes in the graph, add them to the array list in order of precedence 
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
						//					System.out.println(tempNodeList.get(i).name);
						tempGraph.removeVertex(tempNodeList.get(i));
						deletedNodes.add(tempNodeList.get(i));

					}
				}
			}
			//			System.out.println(tempGraph.vertexSet().size());
			
		}

		for (Node node : nodeList){
			System.out.println(node.toString());
		}

		scheduleList(); 

	}

	public static void scheduleList(){

		int numProc = MainReadFile.options.getNumProcessors(); 


		for (int i=1; i<numProc+1; i++){
			Processor proc = new Processor(i); 
			procList.add(proc); 
		}


		// for each node in the node list schedule it to a processor 
		for (Node node: nodeList){
			// if it is a source it can be simply scheduled to any processor - this would be the processor with the earliest finish time 
			if (node.findParents(MainReadFile.graph).size() == 0){
				Processor allocatedProc = getMinFinishTimeSource(); 
				allocatedProc.addTask(node, node.weight); 
				node.updateAllocation(allocatedProc.finTime, allocatedProc.number);

			} else {
				ArrayList<Node> parents = node.findParents(MainReadFile.graph); 

				// check if all parents are done before the children (although this should be ensured by the ordering of the list 

				// Try to put the task on each processor and keep track of the finish time if this task was put on that processor 
				allocateProcessor(node, parents); 

			}

		}


	}

	public static void allocateProcessor (Node node, ArrayList<Node>parents){
		Processor allocProc = procList.get(0);
		//		int minFinTime = procList.get(0).FinTime ; 
		int minFinTime = Integer.MAX_VALUE;
		int criticalPathTime = -1;

		// for each processor try to place the task node onto that processor and calculate the finish time. 
		// the processor with the earliest finish time of that task, is the processor that the task will be allocated to.  
		for (Processor proc : procList){

			// look at each of the nodes parents and see if they are on this processor. 
			// if the parents are on this processor then no need to add communication, or any idle time 
			// and simply add to the end of the processor to calculate the start and finish time of the task. 
			Boolean allParentsOnProc = true; 
			for (Node parent: parents){
				if (parent.allocProc != proc.number) {
					//					System.out.println(parent.name);
					//					System.out.println(parent.allocProc);
					allParentsOnProc = false; 
				}
			} 			
			if (allParentsOnProc){
				minFinTime = proc.finTime + node.weight;
				allocProc = proc;
				System.out.println(minFinTime);
			}

			// If the nodes parents are on different nodes, then calculate the max length of the parent tasks 
			// all completing, along with the communication times for each of the processor 

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
						int currentCriticalPathTime = Math.max((parent.finishTime + communicationTime), proc.finTime) + node.weight ;
						if (criticalPathTime < currentCriticalPathTime) {
							criticalPathTime = currentCriticalPathTime;
						}
					}
				}
			}

			if ((criticalPathTime < minFinTime) &&  (criticalPathTime > -1)){
				minFinTime = criticalPathTime;
				allocProc = proc;
			}

		}

		// the processor that is selected as the allocated processor is the processor that when the task is ran on this 
		// processor it has the earliest finish time  
		allocProc.addTask(node, minFinTime);
		//		System.out.println(node.name);
		//		System.out.println("2nd minFinTime " + minFinTime);
		//		System.out.println(allocProc.number);
		node.updateAllocation(minFinTime, allocProc.number);
		//		System.out.println("node start " + node.startTime);
		//		System.out.println("proc fin" + allocProc);


	}


	// find the processor to allocate a source node by looking at only the earliest finish times 
	public static Processor getMinFinishTimeSource(){

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
