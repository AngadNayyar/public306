package a_star_implementation;//####[1]####
//####[1]####
import java.util.ArrayList;//####[3]####
import java.util.Collections;//####[4]####
import java.util.Set;//####[5]####
import java.util.concurrent.PriorityBlockingQueue;//####[6]####
import org.jgrapht.graph.DefaultDirectedWeightedGraph;//####[8]####
import org.jgrapht.graph.DefaultEdge;//####[9]####
import org.jgrapht.graph.DefaultWeightedEdge;//####[10]####
import processing_classes.Options;//####[13]####
import processing_classes.TaskNode;//####[14]####
import pt.runtime.TaskID;//####[16]####
import pt.runtime.TaskIDGroup;//####[17]####
//####[17]####
//-- ParaTask related imports//####[17]####
import pt.runtime.*;//####[17]####
import java.util.concurrent.ExecutionException;//####[17]####
import java.util.concurrent.locks.*;//####[17]####
import java.lang.reflect.*;//####[17]####
import pt.runtime.GuiThread;//####[17]####
import java.util.concurrent.BlockingQueue;//####[17]####
import java.util.ArrayList;//####[17]####
import java.util.List;//####[17]####
//####[17]####
public class AstarParr implements AstarInterface {//####[19]####
    static{ParaTask.init();}//####[19]####
    /*  ParaTask helper method to access private/protected slots *///####[19]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[19]####
        if (m.getParameterTypes().length == 0)//####[19]####
            m.invoke(instance);//####[19]####
        else if ((m.getParameterTypes().length == 1))//####[19]####
            m.invoke(instance, arg);//####[19]####
        else //####[19]####
            m.invoke(instance, arg, interResult);//####[19]####
    }//####[19]####
//####[21]####
    private PriorityBlockingQueue<StateWeights> openQueue = new PriorityBlockingQueue<StateWeights>();//####[21]####
//####[22]####
    private PriorityBlockingQueue<StateWeights> closedQueue = new PriorityBlockingQueue<StateWeights>();//####[22]####
//####[23]####
    private int numProc;//####[23]####
//####[24]####
    private DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph = new DefaultDirectedWeightedGraph<TaskNode, DefaultEdge>(DefaultWeightedEdge.class);//####[24]####
//####[24]####
    ;//####[24]####
//####[25]####
    private Options options = new Options();//####[25]####
//####[27]####
    public AstarParr(DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph, Options options) {//####[27]####
        this.graph = graph;//####[28]####
        this.options = options;//####[29]####
    }//####[30]####
//####[32]####
    public AstarParr(DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph) {//####[32]####
        this.graph = graph;//####[33]####
    }//####[34]####
//####[36]####
    public Path solveAstar() throws InterruptedException {//####[36]####
        TaskNode initialNode = new TaskNode();//####[39]####
        Path initialPath = new Path(initialNode);//####[40]####
        StateWeights initialSW = new StateWeights(initialPath, 0.0);//####[41]####
        openQueue.add(initialSW);//####[42]####
        TaskIDGroup<Void> taskGroup = new TaskIDGroup(options.getNumThreads());//####[44]####
        for (int i : options.getNumThreads()) //####[45]####
        {//####[45]####
            TaskID id = parallelSearch();//####[46]####
            taskGroup.add(id);//####[47]####
        }//####[48]####
        try {//####[50]####
            taskGroup.waitTillFinished();//####[51]####
        } catch (Exception e) {//####[52]####
            e.printStackTrace();//####[53]####
        }//####[54]####
    }//####[56]####
//####[58]####
    private static volatile Method __pt__parallelSearch__method = null;//####[58]####
    private synchronized static void __pt__parallelSearch__ensureMethodVarSet() {//####[58]####
        if (__pt__parallelSearch__method == null) {//####[58]####
            try {//####[58]####
                __pt__parallelSearch__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__parallelSearch", new Class[] {//####[58]####
                    //####[58]####
                });//####[58]####
            } catch (Exception e) {//####[58]####
                e.printStackTrace();//####[58]####
            }//####[58]####
        }//####[58]####
    }//####[58]####
    TaskIDGroup<Void> parallelSearch() {//####[58]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[58]####
        return parallelSearch(new TaskInfo());//####[58]####
    }//####[58]####
    TaskIDGroup<Void> parallelSearch(TaskInfo taskinfo) {//####[58]####
        // ensure Method variable is set//####[58]####
        if (__pt__parallelSearch__method == null) {//####[58]####
            __pt__parallelSearch__ensureMethodVarSet();//####[58]####
        }//####[58]####
        taskinfo.setParameters();//####[58]####
        taskinfo.setMethod(__pt__parallelSearch__method);//####[58]####
        taskinfo.setInstance(this);//####[58]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[58]####
    }//####[58]####
    public void __pt__parallelSearch() {//####[58]####
        while (!openQueue.isEmpty()) //####[59]####
        {//####[59]####
            StateWeights stateWeight = openQueue.poll();//####[62]####
            if (isComplete(stateWeight)) //####[63]####
            {//####[63]####
                setScheduleOnGraph(stateWeight.getState());//####[65]####
                return stateWeight.getState();//####[66]####
            } else {//####[67]####
                expandState(stateWeight, options.getNumProcessors());//####[69]####
            }//####[70]####
            closedQueue.add(stateWeight);//####[71]####
        }//####[72]####
    }//####[73]####
//####[73]####
//####[76]####
    private void setScheduleOnGraph(Path state) {//####[76]####
        Set<TaskNode> graphNodes = graph.vertexSet();//####[77]####
        for (TaskNode n : state.getPath()) //####[80]####
        {//####[80]####
            for (TaskNode g : graphNodes) //####[81]####
            {//####[81]####
                if (n.name.equals(g.name)) //####[82]####
                {//####[82]####
                    g.setProc(n.allocProc);//####[83]####
                    g.setStart(n.startTime);//####[84]####
                }//####[85]####
            }//####[86]####
        }//####[87]####
    }//####[88]####
//####[93]####
    private void expandState(StateWeights stateWeight, int processors) {//####[93]####
        Path current = stateWeight.state;//####[94]####
        ArrayList<TaskNode> freeNodes = freeNodes(stateWeight);//####[96]####
        for (TaskNode n : freeNodes) //####[98]####
        {//####[98]####
            for (int i = 1; i <= processors; i++) //####[99]####
            {//####[99]####
                TaskNode newNode = new TaskNode(n);//####[101]####
                newNode.setProc(i);//####[102]####
                setNodeTimes(current, newNode, i);//####[103]####
                Path temp = new Path(current, newNode);//####[104]####
                double pathWeight = heuristicCost(temp, stateWeight);//####[105]####
                if (!openQueue.contains(pathWeight) && !closedQueue.contains(pathWeight)) //####[106]####
                {//####[106]####
                    openQueue.add(new StateWeights(temp, pathWeight));//####[107]####
                }//####[108]####
            }//####[117]####
        }//####[118]####
    }//####[119]####
//####[122]####
    public void setNodeTimes(Path current, TaskNode newNode, int processor) {//####[122]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[123]####
        TaskNode graphNode = newNode;//####[124]####
        for (TaskNode n : allNodes) //####[125]####
        {//####[125]####
            if (n.name == newNode.name) //####[126]####
            {//####[126]####
                graphNode = n;//####[127]####
            }//####[128]####
        }//####[129]####
        Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(graphNode);//####[131]####
        int processorEndTime = latestEndTimeOnProcessor(current, processor);//####[133]####
        int parentEndTime = 0;//####[134]####
        int parentProcessor = processor;//####[135]####
        int latestAllowedTime;//####[136]####
        int t = 0;//####[137]####
        if (incomingEdges.isEmpty()) //####[140]####
        {//####[140]####
            newNode.setStart(processorEndTime);//####[141]####
        } else for (DefaultEdge e : incomingEdges) //####[143]####
        {//####[143]####
            int communicationTime = (int) graph.getEdgeWeight(e);//####[144]####
            TaskNode parentNode = graph.getEdgeSource(e);//####[148]####
            ArrayList<TaskNode> setOfNodesInPath = current.getPath();//####[149]####
            for (TaskNode n : setOfNodesInPath) //####[152]####
            {//####[152]####
                if (n.name.equals(parentNode.name)) //####[153]####
                {//####[153]####
                    parentEndTime = n.finishTime;//####[154]####
                    parentProcessor = n.allocProc;//####[155]####
                }//####[156]####
            }//####[157]####
            if (parentProcessor != processor) //####[159]####
            {//####[159]####
                latestAllowedTime = parentEndTime + communicationTime;//####[160]####
            } else {//####[161]####
                latestAllowedTime = parentEndTime;//####[162]####
            }//####[163]####
            if (latestAllowedTime > t) //####[166]####
            {//####[166]####
                t = latestAllowedTime;//####[167]####
            }//####[168]####
        }//####[169]####
        if (t > processorEndTime) //####[172]####
        {//####[172]####
            newNode.setStart(t);//####[173]####
        } else {//####[174]####
            newNode.setStart(processorEndTime);//####[175]####
        }//####[176]####
        newNode.setFinish(newNode.weight + newNode.startTime);//####[179]####
    }//####[180]####
//####[183]####
    private static int latestEndTimeOnProcessor(Path current, int processor) {//####[183]####
        ArrayList<TaskNode> path = current.getPath();//####[184]####
        int currentFinishTime = 0;//####[185]####
        for (TaskNode n : path) //####[186]####
        {//####[186]####
            if (n.allocProc == processor) //####[187]####
            {//####[187]####
                if (n.finishTime > currentFinishTime) //####[188]####
                {//####[188]####
                    currentFinishTime = n.finishTime;//####[189]####
                }//####[190]####
            }//####[191]####
        }//####[192]####
        return currentFinishTime;//####[193]####
    }//####[194]####
//####[198]####
    public double heuristicCost(Path state, StateWeights stateWeight) {//####[198]####
        int maxTime = 0;//####[199]####
        int startTime = 0;//####[200]####
        TaskNode maxNode = new TaskNode();//####[201]####
        int bottomLevel = 0;//####[202]####
        double newPathWeight = 0;//####[203]####
        double idleTime = 0;//####[204]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[205]####
        ArrayList<TaskNode> path = state.getPath();//####[206]####
        double previousPathWeight = stateWeight.pathWeight;//####[207]####
        for (TaskNode n : path) //####[209]####
        {//####[209]####
            if (n.finishTime >= maxTime) //####[210]####
            {//####[210]####
                maxTime = n.finishTime;//####[211]####
                maxNode = n;//####[212]####
            }//####[213]####
        }//####[214]####
        TaskNode graphNode = maxNode;//####[216]####
        for (TaskNode n : allNodes) //####[217]####
        {//####[217]####
            if (n.name == maxNode.name) //####[218]####
            {//####[218]####
                graphNode = n;//####[219]####
            }//####[220]####
        }//####[221]####
        bottomLevel = ComputationalBottomLevel(graphNode);//####[223]####
        startTime = maxNode.startTime;//####[226]####
        idleTime = getIdleTime(state, graphNode, stateWeight);//####[229]####
        newPathWeight = (double) startTime + (double) (bottomLevel + idleTime);//####[232]####
        if (newPathWeight > previousPathWeight) //####[235]####
        {//####[235]####
            return newPathWeight;//####[236]####
        } else {//####[237]####
            return previousPathWeight;//####[238]####
        }//####[239]####
    }//####[240]####
//####[243]####
    private int ComputationalBottomLevel(TaskNode node) {//####[243]####
        int bottomLevel = 0;//####[244]####
        Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(node);//####[246]####
        if (outgoingEdges.isEmpty()) //####[248]####
        {//####[248]####
            return node.weight;//####[249]####
        } else for (DefaultEdge e : outgoingEdges) //####[251]####
        {//####[251]####
            TaskNode successor = graph.getEdgeTarget(e);//####[252]####
            int temp = ComputationalBottomLevel(successor);//####[253]####
            if (temp > bottomLevel) //####[255]####
            {//####[255]####
                bottomLevel = temp;//####[256]####
            }//####[257]####
        }//####[258]####
        return (node.weight + bottomLevel);//####[259]####
    }//####[260]####
//####[262]####
    private double getIdleTime(Path state, TaskNode currentNode, StateWeights stateWeight) {//####[262]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[264]####
        ArrayList<TaskNode> parents = new ArrayList<TaskNode>();//####[265]####
        freeNodes = freeNodes(stateWeight);//####[266]####
        double earliestStartTime = Double.MAX_VALUE;//####[267]####
        double criticalParentFinTime = 0;//####[268]####
        ArrayList<Double> idleTime = new ArrayList<Double>();//####[269]####
        double dataReadyTime = 0;//####[270]####
        double nodeIdleTime = 0;//####[271]####
        for (TaskNode f : freeNodes) //####[275]####
        {//####[275]####
            parents.clear();//####[277]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(f);//####[278]####
            for (DefaultEdge incomingEdge : incomingEdges) //####[279]####
            {//####[279]####
                parents.add(graph.getEdgeSource(incomingEdge));//####[280]####
            }//####[281]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[282]####
            {//####[282]####
                for (TaskNode parent : parents) //####[284]####
                {//####[284]####
                    if (parent.allocProc == i) //####[285]####
                    {//####[285]####
                        dataReadyTime = parent.finishTime;//####[286]####
                    } else {//####[287]####
                        DefaultEdge edge = graph.getEdge(parent, f);//####[290]####
                        dataReadyTime = parent.finishTime + graph.getEdgeWeight(edge);//####[292]####
                    }//####[293]####
                    if (dataReadyTime > criticalParentFinTime) //####[294]####
                    {//####[294]####
                        criticalParentFinTime = dataReadyTime;//####[295]####
                    }//####[296]####
                }//####[297]####
                if (criticalParentFinTime < earliestStartTime) //####[298]####
                {//####[298]####
                    earliestStartTime = criticalParentFinTime;//####[299]####
                }//####[300]####
            }//####[301]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[302]####
            {//####[302]####
                nodeIdleTime += earliestStartTime - latestEndTimeOnProcessor(state, i);//####[303]####
            }//####[304]####
            idleTime.add(nodeIdleTime);//####[305]####
        }//####[307]####
        return (Collections.max(idleTime)) / options.getNumProcessors();//####[309]####
    }//####[310]####
//####[315]####
    @SuppressWarnings("unchecked")//####[315]####
    private ArrayList<TaskNode> freeNodes(StateWeights stateWeight) {//####[315]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[317]####
        ArrayList<String> used = new ArrayList<String>();//####[318]####
        ArrayList<String> all = new ArrayList<String>();//####[319]####
        ArrayList<String> unused = new ArrayList<String>();//####[320]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[321]####
        for (TaskNode n : allNodes) //####[323]####
        {//####[323]####
            all.add(n.name);//####[324]####
        }//####[325]####
        for (TaskNode n : usedNodes) //####[327]####
        {//####[327]####
            used.add(n.name);//####[328]####
        }//####[329]####
        all.removeAll(used);//####[331]####
        unused = (ArrayList<String>) all.clone();//####[332]####
        for (TaskNode n : allNodes) //####[335]####
        {//####[335]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(n);//####[336]####
            for (DefaultEdge e : incomingEdges) //####[337]####
            {//####[337]####
                TaskNode edgeNode = graph.getEdgeSource(e);//####[338]####
                if (unused.contains(edgeNode.name)) //####[339]####
                {//####[339]####
                    all.remove(n.name);//####[340]####
                }//####[341]####
            }//####[342]####
        }//####[343]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[345]####
        for (TaskNode n : allNodes) //####[346]####
        {//####[346]####
            if (all.contains(n.name)) //####[347]####
            {//####[347]####
                freeNodes.add(n);//####[348]####
            }//####[349]####
        }//####[350]####
        return freeNodes;//####[352]####
    }//####[353]####
//####[356]####
    public boolean isComplete(StateWeights stateWeight) {//####[356]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[357]####
        ArrayList<String> used = new ArrayList<String>();//####[358]####
        ArrayList<String> all = new ArrayList<String>();//####[359]####
        for (TaskNode n : usedNodes) //####[361]####
        {//####[361]####
            used.add(n.name);//####[362]####
        }//####[363]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[364]####
        for (TaskNode n : allNodes) //####[366]####
        {//####[366]####
            all.add(n.name);//####[367]####
        }//####[368]####
        all.removeAll(used);//####[370]####
        if (all.isEmpty()) //####[371]####
        {//####[371]####
            return true;//####[386]####
        } else {//####[387]####
            return false;//####[388]####
        }//####[389]####
    }//####[390]####
}//####[390]####
