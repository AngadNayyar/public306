package a_star_implementation;//####[1]####
//####[1]####
import java.util.ArrayList;//####[3]####
import java.util.Collections;//####[4]####
import java.util.Set;//####[5]####
import java.util.concurrent.CopyOnWriteArrayList;//####[6]####
import java.util.concurrent.PriorityBlockingQueue;//####[7]####
import org.jgrapht.graph.DefaultDirectedWeightedGraph;//####[9]####
import org.jgrapht.graph.DefaultEdge;//####[10]####
import org.jgrapht.graph.DefaultWeightedEdge;//####[11]####
import processing_classes.Options;//####[19]####
import processing_classes.TaskNode;//####[20]####
import pt.runtime.CurrentTask;//####[21]####
import pt.runtime.TaskID;//####[22]####
import pt.runtime.TaskIDGroup;//####[23]####
//####[23]####
//-- ParaTask related imports//####[23]####
import pt.runtime.*;//####[23]####
import java.util.concurrent.ExecutionException;//####[23]####
import java.util.concurrent.locks.*;//####[23]####
import java.lang.reflect.*;//####[23]####
import pt.runtime.GuiThread;//####[23]####
import java.util.concurrent.BlockingQueue;//####[23]####
import java.util.ArrayList;//####[23]####
import java.util.List;//####[23]####
//####[23]####
public class AStarParr {//####[25]####
    static{ParaTask.init();}//####[25]####
    /*  ParaTask helper method to access private/protected slots *///####[25]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[25]####
        if (m.getParameterTypes().length == 0)//####[25]####
            m.invoke(instance);//####[25]####
        else if ((m.getParameterTypes().length == 1))//####[25]####
            m.invoke(instance, arg);//####[25]####
        else //####[25]####
            m.invoke(instance, arg, interResult);//####[25]####
    }//####[25]####
//####[27]####
    private PriorityBlockingQueue<StateWeights> openQueue = new PriorityBlockingQueue<StateWeights>();//####[27]####
//####[28]####
    private PriorityBlockingQueue<StateWeights> closedQueue = new PriorityBlockingQueue<StateWeights>();//####[28]####
//####[29]####
    private int numProc;//####[29]####
//####[30]####
    private DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph = new DefaultDirectedWeightedGraph<TaskNode, DefaultEdge>(DefaultWeightedEdge.class);//####[30]####
//####[30]####
    ;//####[30]####
//####[31]####
    private Options options;//####[31]####
//####[32]####
    private CopyOnWriteArrayList<Path> threadPathList = new CopyOnWriteArrayList<Path>();//####[32]####
//####[35]####
    public AStarParr(DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph, Options options) {//####[35]####
        this.graph = graph;//####[36]####
        this.options = options;//####[37]####
    }//####[38]####
//####[40]####
    public AStarParr(DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph) {//####[40]####
        this.graph = graph;//####[41]####
    }//####[42]####
//####[44]####
    public void solveAstar() throws InterruptedException {//####[44]####
        TaskNode initialNode = new TaskNode();//####[47]####
        Path initialPath = new Path(initialNode);//####[48]####
        StateWeights initialSW = new StateWeights(initialPath, 0.0);//####[49]####
        openQueue.add(initialSW);//####[50]####
        options.setNumThreads(4);//####[52]####
        TaskIDGroup taskGroup = new TaskIDGroup(1);//####[53]####
        for (int i = 0; i < options.getNumThreads(); i++) //####[54]####
        {//####[54]####
            TaskID id = parallelSearch();//####[55]####
            taskGroup.add(id);//####[56]####
        }//####[57]####
        try {//####[59]####
            taskGroup.waitTillFinished();//####[60]####
        } catch (Exception e) {//####[61]####
            e.printStackTrace();//####[62]####
        }//####[63]####
        Path optimalPath = getSmallestPathFromList();//####[65]####
        setScheduleOnGraph(optimalPath);//####[66]####
    }//####[67]####
//####[69]####
    private static volatile Method __pt__parallelSearch__method = null;//####[69]####
    private synchronized static void __pt__parallelSearch__ensureMethodVarSet() {//####[69]####
        if (__pt__parallelSearch__method == null) {//####[69]####
            try {//####[69]####
                __pt__parallelSearch__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__parallelSearch", new Class[] {//####[69]####
                    //####[69]####
                });//####[69]####
            } catch (Exception e) {//####[69]####
                e.printStackTrace();//####[69]####
            }//####[69]####
        }//####[69]####
    }//####[69]####
    TaskIDGroup<Void> parallelSearch() {//####[69]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[69]####
        return parallelSearch(new TaskInfo());//####[69]####
    }//####[69]####
    TaskIDGroup<Void> parallelSearch(TaskInfo taskinfo) {//####[69]####
        // ensure Method variable is set//####[69]####
        if (__pt__parallelSearch__method == null) {//####[69]####
            __pt__parallelSearch__ensureMethodVarSet();//####[69]####
        }//####[69]####
        taskinfo.setParameters();//####[69]####
        taskinfo.setMethod(__pt__parallelSearch__method);//####[69]####
        taskinfo.setInstance(this);//####[69]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[69]####
    }//####[69]####
    public void __pt__parallelSearch() {//####[69]####
        while (!openQueue.isEmpty()) //####[70]####
        {//####[70]####
            StateWeights stateWeight = openQueue.poll();//####[73]####
            if (stateWeight == null) //####[74]####
            {//####[74]####
                TaskNode initialNode = new TaskNode();//####[75]####
                Path initialPath = new Path(initialNode);//####[76]####
                stateWeight = new StateWeights(initialPath, 0.0);//####[77]####
            }//####[78]####
            if (isComplete(stateWeight)) //####[79]####
            {//####[79]####
                threadPathList.add(stateWeight.getState());//####[81]####
                break;//####[82]####
            } else {//####[83]####
                expandState(stateWeight, options.getNumProcessors());//####[85]####
            }//####[86]####
            closedQueue.add(stateWeight);//####[87]####
        }//####[88]####
    }//####[89]####
//####[89]####
//####[92]####
    private Path getSmallestPathFromList() {//####[92]####
        int smallestFinPath = Integer.MAX_VALUE;//####[94]####
        int finishTimeOfPath = 0;//####[95]####
        Path optimalPath = null;//####[96]####
        for (Path p : threadPathList) //####[98]####
        {//####[98]####
            finishTimeOfPath = 0;//####[99]####
            for (TaskNode n : p.getPath()) //####[101]####
            {//####[101]####
                if (n.finishTime > finishTimeOfPath) //####[102]####
                {//####[102]####
                    finishTimeOfPath = n.finishTime;//####[103]####
                }//####[104]####
            }//####[105]####
            if (finishTimeOfPath < smallestFinPath) //####[107]####
            {//####[107]####
                smallestFinPath = finishTimeOfPath;//####[108]####
                optimalPath = p;//####[109]####
            }//####[110]####
        }//####[112]####
        return optimalPath;//####[113]####
    }//####[114]####
//####[117]####
    private void setScheduleOnGraph(Path state) {//####[117]####
        Set<TaskNode> graphNodes = graph.vertexSet();//####[118]####
        for (TaskNode n : state.getPath()) //####[121]####
        {//####[121]####
            for (TaskNode g : graphNodes) //####[122]####
            {//####[122]####
                if (n.name.equals(g.name)) //####[123]####
                {//####[123]####
                    g.setProc(n.allocProc);//####[124]####
                    g.setStart(n.startTime);//####[125]####
                }//####[126]####
            }//####[127]####
        }//####[128]####
    }//####[129]####
//####[134]####
    private void expandState(StateWeights stateWeight, int processors) {//####[134]####
        Path current = stateWeight.state;//####[135]####
        ArrayList<TaskNode> freeNodes = freeNodes(stateWeight);//####[137]####
        for (TaskNode n : freeNodes) //####[139]####
        {//####[139]####
            for (int i = 1; i <= processors; i++) //####[140]####
            {//####[140]####
                TaskNode newNode = new TaskNode(n);//####[142]####
                newNode.setProc(i);//####[143]####
                setNodeTimes(current, newNode, i);//####[144]####
                Path temp = new Path(current, newNode);//####[145]####
                double pathWeight = heuristicCost(temp, stateWeight);//####[146]####
                if (!openQueue.contains(pathWeight) && !closedQueue.contains(pathWeight)) //####[147]####
                {//####[147]####
                    openQueue.add(new StateWeights(temp, pathWeight));//####[148]####
                }//####[149]####
            }//####[150]####
        }//####[151]####
    }//####[152]####
//####[155]####
    public void setNodeTimes(Path current, TaskNode newNode, int processor) {//####[155]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[156]####
        TaskNode graphNode = newNode;//####[157]####
        for (TaskNode n : allNodes) //####[158]####
        {//####[158]####
            if (n.name == newNode.name) //####[159]####
            {//####[159]####
                graphNode = n;//####[160]####
            }//####[161]####
        }//####[162]####
        Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(graphNode);//####[164]####
        int processorEndTime = latestEndTimeOnProcessor(current, processor);//####[166]####
        int parentEndTime = 0;//####[167]####
        int parentProcessor = processor;//####[168]####
        int latestAllowedTime;//####[169]####
        int t = 0;//####[170]####
        if (incomingEdges.isEmpty()) //####[173]####
        {//####[173]####
            newNode.setStart(processorEndTime);//####[174]####
        } else for (DefaultEdge e : incomingEdges) //####[176]####
        {//####[176]####
            int communicationTime = (int) graph.getEdgeWeight(e);//####[177]####
            TaskNode parentNode = graph.getEdgeSource(e);//####[181]####
            ArrayList<TaskNode> setOfNodesInPath = current.getPath();//####[182]####
            for (TaskNode n : setOfNodesInPath) //####[185]####
            {//####[185]####
                if (n.name.equals(parentNode.name)) //####[186]####
                {//####[186]####
                    parentEndTime = n.finishTime;//####[187]####
                    parentProcessor = n.allocProc;//####[188]####
                }//####[189]####
            }//####[190]####
            if (parentProcessor != processor) //####[192]####
            {//####[192]####
                latestAllowedTime = parentEndTime + communicationTime;//####[193]####
            } else {//####[194]####
                latestAllowedTime = parentEndTime;//####[195]####
            }//####[196]####
            if (latestAllowedTime > t) //####[199]####
            {//####[199]####
                t = latestAllowedTime;//####[200]####
            }//####[201]####
        }//####[202]####
        if (t > processorEndTime) //####[205]####
        {//####[205]####
            newNode.setStart(t);//####[206]####
        } else {//####[207]####
            newNode.setStart(processorEndTime);//####[208]####
        }//####[209]####
        newNode.setFinish(newNode.weight + newNode.startTime);//####[212]####
    }//####[213]####
//####[216]####
    private static int latestEndTimeOnProcessor(Path current, int processor) {//####[216]####
        ArrayList<TaskNode> path = current.getPath();//####[217]####
        int currentFinishTime = 0;//####[218]####
        for (TaskNode n : path) //####[219]####
        {//####[219]####
            if (n.allocProc == processor) //####[220]####
            {//####[220]####
                if (n.finishTime > currentFinishTime) //####[221]####
                {//####[221]####
                    currentFinishTime = n.finishTime;//####[222]####
                }//####[223]####
            }//####[224]####
        }//####[225]####
        return currentFinishTime;//####[226]####
    }//####[227]####
//####[231]####
    public double heuristicCost(Path state, StateWeights stateWeight) {//####[231]####
        int maxTime = 0;//####[232]####
        int startTime = 0;//####[233]####
        TaskNode maxNode = new TaskNode();//####[234]####
        int bottomLevel = 0;//####[235]####
        double newPathWeight = 0;//####[236]####
        double idleTime = 0;//####[237]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[238]####
        ArrayList<TaskNode> path = state.getPath();//####[239]####
        double previousPathWeight = stateWeight.pathWeight;//####[240]####
        for (TaskNode n : path) //####[242]####
        {//####[242]####
            if (n.finishTime >= maxTime) //####[243]####
            {//####[243]####
                maxTime = n.finishTime;//####[244]####
                maxNode = n;//####[245]####
            }//####[246]####
        }//####[247]####
        TaskNode graphNode = maxNode;//####[249]####
        for (TaskNode n : allNodes) //####[250]####
        {//####[250]####
            if (n.name == maxNode.name) //####[251]####
            {//####[251]####
                graphNode = n;//####[252]####
            }//####[253]####
        }//####[254]####
        bottomLevel = ComputationalBottomLevel(graphNode);//####[256]####
        startTime = maxNode.startTime;//####[259]####
        idleTime = getIdleTime(state, graphNode, stateWeight);//####[262]####
        newPathWeight = (double) startTime + (double) (bottomLevel + idleTime);//####[266]####
        if (newPathWeight > previousPathWeight) //####[270]####
        {//####[270]####
            return newPathWeight;//####[271]####
        } else {//####[272]####
            return previousPathWeight;//####[273]####
        }//####[274]####
    }//####[275]####
//####[279]####
    private int ComputationalBottomLevel(TaskNode node) {//####[279]####
        int bottomLevel = 0;//####[280]####
        Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(node);//####[282]####
        if (outgoingEdges.isEmpty()) //####[284]####
        {//####[284]####
            return node.weight;//####[285]####
        } else for (DefaultEdge e : outgoingEdges) //####[289]####
        {//####[289]####
            TaskNode successor = graph.getEdgeTarget(e);//####[290]####
            int temp = ComputationalBottomLevel(successor);//####[291]####
            if (temp > bottomLevel) //####[293]####
            {//####[293]####
                bottomLevel = temp;//####[294]####
            }//####[295]####
        }//####[296]####
        return (node.weight + bottomLevel);//####[297]####
    }//####[298]####
//####[300]####
    private double getIdleTime(Path state, TaskNode currentNode, StateWeights stateWeight) {//####[300]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[302]####
        ArrayList<TaskNode> parents = new ArrayList<TaskNode>();//####[303]####
        freeNodes = freeNodes(stateWeight);//####[304]####
        double earliestStartTime = Double.MAX_VALUE;//####[305]####
        double criticalParentFinTime = 0;//####[306]####
        ArrayList<Double> idleTime = new ArrayList<Double>();//####[307]####
        double dataReadyTime = 0;//####[308]####
        double nodeIdleTime = 0;//####[309]####
        for (TaskNode f : freeNodes) //####[314]####
        {//####[314]####
            parents.clear();//####[315]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(f);//####[316]####
            for (DefaultEdge incomingEdge : incomingEdges) //####[317]####
            {//####[317]####
                parents.add(graph.getEdgeSource(incomingEdge));//####[318]####
            }//####[319]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[320]####
            {//####[320]####
                for (TaskNode parent : parents) //####[322]####
                {//####[322]####
                    if (parent.allocProc == i) //####[323]####
                    {//####[323]####
                        dataReadyTime = latestEndTimeOnProcessor(state, i);//####[324]####
                    } else {//####[325]####
                        DefaultEdge edge = graph.getEdge(parent, f);//####[326]####
                        dataReadyTime = Math.max((parent.finishTime + graph.getEdgeWeight(edge)), latestEndTimeOnProcessor(state, i));//####[327]####
                    }//####[329]####
                    if (dataReadyTime > criticalParentFinTime) //####[330]####
                    {//####[330]####
                        criticalParentFinTime = dataReadyTime;//####[331]####
                    }//####[332]####
                }//####[333]####
                if (criticalParentFinTime < earliestStartTime) //####[334]####
                {//####[334]####
                    earliestStartTime = criticalParentFinTime;//####[335]####
                }//####[336]####
            }//####[337]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[338]####
            {//####[338]####
                double temp = earliestStartTime - latestEndTimeOnProcessor(state, i);//####[339]####
                if (temp > 0) //####[340]####
                {//####[340]####
                    nodeIdleTime += temp;//####[341]####
                }//####[342]####
            }//####[343]####
            idleTime.add(nodeIdleTime);//####[344]####
        }//####[346]####
        return (Collections.min(idleTime)) / options.getNumProcessors();//####[347]####
    }//####[348]####
//####[352]####
    @SuppressWarnings("unchecked")//####[352]####
    private ArrayList<TaskNode> freeNodes(StateWeights stateWeight) {//####[352]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[354]####
        ArrayList<String> used = new ArrayList<String>();//####[355]####
        ArrayList<String> all = new ArrayList<String>();//####[356]####
        ArrayList<String> unused = new ArrayList<String>();//####[357]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[358]####
        for (TaskNode n : allNodes) //####[360]####
        {//####[360]####
            all.add(n.name);//####[361]####
        }//####[362]####
        for (TaskNode n : usedNodes) //####[364]####
        {//####[364]####
            used.add(n.name);//####[365]####
        }//####[366]####
        all.removeAll(used);//####[368]####
        unused = (ArrayList<String>) all.clone();//####[369]####
        for (TaskNode n : allNodes) //####[372]####
        {//####[372]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(n);//####[373]####
            for (DefaultEdge e : incomingEdges) //####[374]####
            {//####[374]####
                TaskNode edgeNode = graph.getEdgeSource(e);//####[375]####
                if (unused.contains(edgeNode.name)) //####[376]####
                {//####[376]####
                    all.remove(n.name);//####[377]####
                }//####[378]####
            }//####[379]####
        }//####[380]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[382]####
        for (TaskNode n : allNodes) //####[383]####
        {//####[383]####
            if (all.contains(n.name)) //####[384]####
            {//####[384]####
                freeNodes.add(n);//####[385]####
            }//####[386]####
        }//####[387]####
        return freeNodes;//####[389]####
    }//####[390]####
//####[393]####
    public boolean isComplete(StateWeights stateWeight) {//####[393]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[394]####
        ArrayList<String> used = new ArrayList<String>();//####[395]####
        ArrayList<String> all = new ArrayList<String>();//####[396]####
        for (TaskNode n : usedNodes) //####[398]####
        {//####[398]####
            used.add(n.name);//####[399]####
        }//####[400]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[401]####
        for (TaskNode n : allNodes) //####[403]####
        {//####[403]####
            all.add(n.name);//####[404]####
        }//####[405]####
        all.removeAll(used);//####[407]####
        if (all.isEmpty()) //####[408]####
        {//####[408]####
            return true;//####[409]####
        } else {//####[410]####
            return false;//####[411]####
        }//####[412]####
    }//####[413]####
}//####[413]####
