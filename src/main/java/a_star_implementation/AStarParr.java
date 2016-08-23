package a_star_implementation;//####[1]####
//####[1]####
import java.nio.file.Path;//####[3]####
import java.util.ArrayList;//####[4]####
import java.util.Collections;//####[5]####
import java.util.Set;//####[6]####
import java.util.concurrent.CopyOnWriteArrayList;//####[7]####
import java.util.concurrent.PriorityBlockingQueue;//####[8]####
import org.jgrapht.graph.DefaultDirectedWeightedGraph;//####[9]####
import org.jgrapht.graph.DefaultEdge;//####[10]####
import org.jgrapht.graph.DefaultWeightedEdge;//####[11]####
import processing_classes.Options;//####[16]####
import processing_classes.TaskNode;//####[17]####
import pt.runtime.TaskID;//####[18]####
import pt.runtime.TaskIDGroup;//####[19]####
//####[19]####
//-- ParaTask related imports//####[19]####
import pt.runtime.*;//####[19]####
import java.util.concurrent.ExecutionException;//####[19]####
import java.util.concurrent.locks.*;//####[19]####
import java.lang.reflect.*;//####[19]####
import pt.runtime.GuiThread;//####[19]####
import java.util.concurrent.BlockingQueue;//####[19]####
import java.util.ArrayList;//####[19]####
import java.util.List;//####[19]####
//####[19]####
public class AstarParr {//####[21]####
    static{ParaTask.init();}//####[21]####
    /*  ParaTask helper method to access private/protected slots *///####[21]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[21]####
        if (m.getParameterTypes().length == 0)//####[21]####
            m.invoke(instance);//####[21]####
        else if ((m.getParameterTypes().length == 1))//####[21]####
            m.invoke(instance, arg);//####[21]####
        else //####[21]####
            m.invoke(instance, arg, interResult);//####[21]####
    }//####[21]####
//####[23]####
    private PriorityBlockingQueue<StateWeights> openQueue = new PriorityBlockingQueue<StateWeights>();//####[23]####
//####[24]####
    private PriorityBlockingQueue<StateWeights> closedQueue = new PriorityBlockingQueue<StateWeights>();//####[24]####
//####[25]####
    private int numProc;//####[25]####
//####[26]####
    private DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph = new DefaultDirectedWeightedGraph<TaskNode, DefaultEdge>(DefaultWeightedEdge.class);//####[26]####
//####[26]####
    ;//####[26]####
//####[27]####
    private Options options;//####[27]####
//####[28]####
    private CopyOnWriteArrayList<Path> threadPathList = new CompyOnWriteArrayList<Path>();//####[28]####
//####[31]####
    public AstarParr(DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph, Options options) {//####[31]####
        this.graph = graph;//####[32]####
        this.options = options;//####[33]####
    }//####[34]####
//####[36]####
    public AstarParr(DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph) {//####[36]####
        this.graph = graph;//####[37]####
    }//####[38]####
//####[40]####
    public void solveAstar() throws InterruptedException {//####[40]####
        TaskNode initialNode = new TaskNode();//####[43]####
        Path initialPath = new Path(initialNode);//####[44]####
        StateWeights initialSW = new StateWeights(initialPath, 0.0);//####[45]####
        openQueue.add(initialSW);//####[46]####
        TaskIDGroup<Void> taskGroup = new TaskIDGroup(options.getNumThreads());//####[48]####
        for (int i = 0; i < options.getNumThreads(); i++) //####[49]####
        {//####[49]####
            TaskID id = parallelSearch();//####[50]####
            taskGroup.add(id);//####[51]####
        }//####[52]####
        try {//####[54]####
            taskGroup.waitTillFinished();//####[55]####
        } catch (Exception e) {//####[56]####
            e.printStackTrace();//####[57]####
        }//####[58]####
        int smallestFinPath = Integer.MAX_VALUE;//####[61]####
        Path optimalPath;//####[62]####
        for (Path p : threadPathList) //####[63]####
        {//####[63]####
            for (TaskNode n : p) //####[64]####
            {//####[64]####
                if (n.finishTime < smallestFinNode) //####[65]####
                {//####[65]####
                    smallestFinPath = n.finishTime;//####[66]####
                    optimalPath = p;//####[67]####
                }//####[68]####
            }//####[69]####
        }//####[70]####
        setScheduleOnGraph(optimalPath);//####[72]####
    }//####[74]####
//####[76]####
    private static volatile Method __pt__parallelSearch__method = null;//####[76]####
    private synchronized static void __pt__parallelSearch__ensureMethodVarSet() {//####[76]####
        if (__pt__parallelSearch__method == null) {//####[76]####
            try {//####[76]####
                __pt__parallelSearch__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__parallelSearch", new Class[] {//####[76]####
                    //####[76]####
                });//####[76]####
            } catch (Exception e) {//####[76]####
                e.printStackTrace();//####[76]####
            }//####[76]####
        }//####[76]####
    }//####[76]####
    TaskIDGroup<Void> parallelSearch() {//####[76]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[76]####
        return parallelSearch(new TaskInfo());//####[76]####
    }//####[76]####
    TaskIDGroup<Void> parallelSearch(TaskInfo taskinfo) {//####[76]####
        // ensure Method variable is set//####[76]####
        if (__pt__parallelSearch__method == null) {//####[76]####
            __pt__parallelSearch__ensureMethodVarSet();//####[76]####
        }//####[76]####
        taskinfo.setParameters();//####[76]####
        taskinfo.setMethod(__pt__parallelSearch__method);//####[76]####
        taskinfo.setInstance(this);//####[76]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[76]####
    }//####[76]####
    public void __pt__parallelSearch() {//####[76]####
        while (!openQueue.isEmpty()) //####[77]####
        {//####[77]####
            StateWeights stateWeight = openQueue.poll();//####[80]####
            if (isComplete(stateWeight)) //####[81]####
            {//####[81]####
                threadPathList.add(stateWeight.getState());//####[83]####
                break;//####[84]####
            } else {//####[85]####
                expandState(stateWeight, options.getNumProcessors());//####[87]####
            }//####[88]####
            closedQueue.add(stateWeight);//####[89]####
        }//####[90]####
    }//####[91]####
//####[91]####
//####[94]####
    private void setScheduleOnGraph(Path state) {//####[94]####
        Set<TaskNode> graphNodes = graph.vertexSet();//####[95]####
        for (TaskNode n : state.getPath()) //####[98]####
        {//####[98]####
            for (TaskNode g : graphNodes) //####[99]####
            {//####[99]####
                if (n.name.equals(g.name)) //####[100]####
                {//####[100]####
                    g.setProc(n.allocProc);//####[101]####
                    g.setStart(n.startTime);//####[102]####
                }//####[103]####
            }//####[104]####
        }//####[105]####
    }//####[106]####
//####[111]####
    private void expandState(StateWeights stateWeight, int processors) {//####[111]####
        Path current = stateWeight.state;//####[112]####
        ArrayList<TaskNode> freeNodes = freeNodes(stateWeight);//####[114]####
        for (TaskNode n : freeNodes) //####[116]####
        {//####[116]####
            for (int i = 1; i <= processors; i++) //####[117]####
            {//####[117]####
                TaskNode newNode = new TaskNode(n);//####[119]####
                newNode.setProc(i);//####[120]####
                setNodeTimes(current, newNode, i);//####[121]####
                Path temp = new Path(current, newNode);//####[122]####
                double pathWeight = heuristicCost(temp, stateWeight);//####[123]####
                if (!openQueue.contains(pathWeight) && !closedQueue.contains(pathWeight)) //####[124]####
                {//####[124]####
                    openQueue.add(new StateWeights(temp, pathWeight));//####[125]####
                }//####[126]####
            }//####[135]####
        }//####[136]####
    }//####[137]####
//####[140]####
    public void setNodeTimes(Path current, TaskNode newNode, int processor) {//####[140]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[141]####
        TaskNode graphNode = newNode;//####[142]####
        for (TaskNode n : allNodes) //####[143]####
        {//####[143]####
            if (n.name == newNode.name) //####[144]####
            {//####[144]####
                graphNode = n;//####[145]####
            }//####[146]####
        }//####[147]####
        Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(graphNode);//####[149]####
        int processorEndTime = latestEndTimeOnProcessor(current, processor);//####[151]####
        int parentEndTime = 0;//####[152]####
        int parentProcessor = processor;//####[153]####
        int latestAllowedTime;//####[154]####
        int t = 0;//####[155]####
        if (incomingEdges.isEmpty()) //####[158]####
        {//####[158]####
            newNode.setStart(processorEndTime);//####[159]####
        } else for (DefaultEdge e : incomingEdges) //####[161]####
        {//####[161]####
            int communicationTime = (int) graph.getEdgeWeight(e);//####[162]####
            TaskNode parentNode = graph.getEdgeSource(e);//####[166]####
            ArrayList<TaskNode> setOfNodesInPath = current.getPath();//####[167]####
            for (TaskNode n : setOfNodesInPath) //####[170]####
            {//####[170]####
                if (n.name.equals(parentNode.name)) //####[171]####
                {//####[171]####
                    parentEndTime = n.finishTime;//####[172]####
                    parentProcessor = n.allocProc;//####[173]####
                }//####[174]####
            }//####[175]####
            if (parentProcessor != processor) //####[177]####
            {//####[177]####
                latestAllowedTime = parentEndTime + communicationTime;//####[178]####
            } else {//####[179]####
                latestAllowedTime = parentEndTime;//####[180]####
            }//####[181]####
            if (latestAllowedTime > t) //####[184]####
            {//####[184]####
                t = latestAllowedTime;//####[185]####
            }//####[186]####
        }//####[187]####
        if (t > processorEndTime) //####[190]####
        {//####[190]####
            newNode.setStart(t);//####[191]####
        } else {//####[192]####
            newNode.setStart(processorEndTime);//####[193]####
        }//####[194]####
        newNode.setFinish(newNode.weight + newNode.startTime);//####[197]####
    }//####[198]####
//####[201]####
    private static int latestEndTimeOnProcessor(Path current, int processor) {//####[201]####
        ArrayList<TaskNode> path = current.getPath();//####[202]####
        int currentFinishTime = 0;//####[203]####
        for (TaskNode n : path) //####[204]####
        {//####[204]####
            if (n.allocProc == processor) //####[205]####
            {//####[205]####
                if (n.finishTime > currentFinishTime) //####[206]####
                {//####[206]####
                    currentFinishTime = n.finishTime;//####[207]####
                }//####[208]####
            }//####[209]####
        }//####[210]####
        return currentFinishTime;//####[211]####
    }//####[212]####
//####[216]####
    public double heuristicCost(Path state, StateWeights stateWeight) {//####[216]####
        int maxTime = 0;//####[217]####
        int startTime = 0;//####[218]####
        TaskNode maxNode = new TaskNode();//####[219]####
        int bottomLevel = 0;//####[220]####
        double newPathWeight = 0;//####[221]####
        double idleTime = 0;//####[222]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[223]####
        ArrayList<TaskNode> path = state.getPath();//####[224]####
        double previousPathWeight = stateWeight.pathWeight;//####[225]####
        for (TaskNode n : path) //####[227]####
        {//####[227]####
            if (n.finishTime >= maxTime) //####[228]####
            {//####[228]####
                maxTime = n.finishTime;//####[229]####
                maxNode = n;//####[230]####
            }//####[231]####
        }//####[232]####
        TaskNode graphNode = maxNode;//####[234]####
        for (TaskNode n : allNodes) //####[235]####
        {//####[235]####
            if (n.name == maxNode.name) //####[236]####
            {//####[236]####
                graphNode = n;//####[237]####
            }//####[238]####
        }//####[239]####
        bottomLevel = ComputationalBottomLevel(graphNode);//####[241]####
        startTime = maxNode.startTime;//####[244]####
        idleTime = getIdleTime(state, graphNode, stateWeight);//####[247]####
        newPathWeight = (double) startTime + (double) (bottomLevel + idleTime);//####[250]####
        if (newPathWeight > previousPathWeight) //####[253]####
        {//####[253]####
            return newPathWeight;//####[254]####
        } else {//####[255]####
            return previousPathWeight;//####[256]####
        }//####[257]####
    }//####[258]####
//####[261]####
    private int ComputationalBottomLevel(TaskNode node) {//####[261]####
        int bottomLevel = 0;//####[262]####
        Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(node);//####[264]####
        if (outgoingEdges.isEmpty()) //####[266]####
        {//####[266]####
            return node.weight;//####[267]####
        } else for (DefaultEdge e : outgoingEdges) //####[269]####
        {//####[269]####
            TaskNode successor = graph.getEdgeTarget(e);//####[270]####
            int temp = ComputationalBottomLevel(successor);//####[271]####
            if (temp > bottomLevel) //####[273]####
            {//####[273]####
                bottomLevel = temp;//####[274]####
            }//####[275]####
        }//####[276]####
        return (node.weight + bottomLevel);//####[277]####
    }//####[278]####
//####[280]####
    private double getIdleTime(Path state, TaskNode currentNode, StateWeights stateWeight) {//####[280]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[282]####
        ArrayList<TaskNode> parents = new ArrayList<TaskNode>();//####[283]####
        freeNodes = freeNodes(stateWeight);//####[284]####
        double earliestStartTime = Double.MAX_VALUE;//####[285]####
        double criticalParentFinTime = 0;//####[286]####
        ArrayList<Double> idleTime = new ArrayList<Double>();//####[287]####
        double dataReadyTime = 0;//####[288]####
        double nodeIdleTime = 0;//####[289]####
        for (TaskNode f : freeNodes) //####[293]####
        {//####[293]####
            parents.clear();//####[295]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(f);//####[296]####
            for (DefaultEdge incomingEdge : incomingEdges) //####[297]####
            {//####[297]####
                parents.add(graph.getEdgeSource(incomingEdge));//####[298]####
            }//####[299]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[300]####
            {//####[300]####
                for (TaskNode parent : parents) //####[302]####
                {//####[302]####
                    if (parent.allocProc == i) //####[303]####
                    {//####[303]####
                        dataReadyTime = parent.finishTime;//####[304]####
                    } else {//####[305]####
                        DefaultEdge edge = graph.getEdge(parent, f);//####[308]####
                        dataReadyTime = parent.finishTime + graph.getEdgeWeight(edge);//####[310]####
                    }//####[311]####
                    if (dataReadyTime > criticalParentFinTime) //####[312]####
                    {//####[312]####
                        criticalParentFinTime = dataReadyTime;//####[313]####
                    }//####[314]####
                }//####[315]####
                if (criticalParentFinTime < earliestStartTime) //####[316]####
                {//####[316]####
                    earliestStartTime = criticalParentFinTime;//####[317]####
                }//####[318]####
            }//####[319]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[320]####
            {//####[320]####
                nodeIdleTime += earliestStartTime - latestEndTimeOnProcessor(state, i);//####[321]####
            }//####[322]####
            idleTime.add(nodeIdleTime);//####[323]####
        }//####[325]####
        return (Collections.max(idleTime)) / options.getNumProcessors();//####[327]####
    }//####[328]####
//####[333]####
    @SuppressWarnings("unchecked")//####[333]####
    private ArrayList<TaskNode> freeNodes(StateWeights stateWeight) {//####[333]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[335]####
        ArrayList<String> used = new ArrayList<String>();//####[336]####
        ArrayList<String> all = new ArrayList<String>();//####[337]####
        ArrayList<String> unused = new ArrayList<String>();//####[338]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[339]####
        for (TaskNode n : allNodes) //####[341]####
        {//####[341]####
            all.add(n.name);//####[342]####
        }//####[343]####
        for (TaskNode n : usedNodes) //####[345]####
        {//####[345]####
            used.add(n.name);//####[346]####
        }//####[347]####
        all.removeAll(used);//####[349]####
        unused = (ArrayList<String>) all.clone();//####[350]####
        for (TaskNode n : allNodes) //####[353]####
        {//####[353]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(n);//####[354]####
            for (DefaultEdge e : incomingEdges) //####[355]####
            {//####[355]####
                TaskNode edgeNode = graph.getEdgeSource(e);//####[356]####
                if (unused.contains(edgeNode.name)) //####[357]####
                {//####[357]####
                    all.remove(n.name);//####[358]####
                }//####[359]####
            }//####[360]####
        }//####[361]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[363]####
        for (TaskNode n : allNodes) //####[364]####
        {//####[364]####
            if (all.contains(n.name)) //####[365]####
            {//####[365]####
                freeNodes.add(n);//####[366]####
            }//####[367]####
        }//####[368]####
        return freeNodes;//####[370]####
    }//####[371]####
//####[374]####
    public boolean isComplete(StateWeights stateWeight) {//####[374]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[375]####
        ArrayList<String> used = new ArrayList<String>();//####[376]####
        ArrayList<String> all = new ArrayList<String>();//####[377]####
        for (TaskNode n : usedNodes) //####[379]####
        {//####[379]####
            used.add(n.name);//####[380]####
        }//####[381]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[382]####
        for (TaskNode n : allNodes) //####[384]####
        {//####[384]####
            all.add(n.name);//####[385]####
        }//####[386]####
        all.removeAll(used);//####[388]####
        if (all.isEmpty()) //####[389]####
        {//####[389]####
            return true;//####[404]####
        } else {//####[405]####
            return false;//####[406]####
        }//####[407]####
    }//####[408]####
}//####[408]####
