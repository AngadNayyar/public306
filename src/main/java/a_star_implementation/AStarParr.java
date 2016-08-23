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
        TaskIDGroup taskGroup = new TaskIDGroup(options.getNumThreads());//####[53]####
        System.out.println(options.getNumThreads());//####[54]####
        for (int i = 0; i < options.getNumThreads(); i++) //####[55]####
        {//####[55]####
            TaskID id = parallelSearch();//####[56]####
            taskGroup.add(id);//####[57]####
        }//####[58]####
        try {//####[60]####
            taskGroup.waitTillFinished();//####[61]####
        } catch (Exception e) {//####[62]####
            e.printStackTrace();//####[63]####
        }//####[64]####
        Path optimalPath = getSmallestPathFromList();//####[66]####
        setScheduleOnGraph(optimalPath);//####[67]####
    }//####[68]####
//####[70]####
    private static volatile Method __pt__parallelSearch__method = null;//####[70]####
    private synchronized static void __pt__parallelSearch__ensureMethodVarSet() {//####[70]####
        if (__pt__parallelSearch__method == null) {//####[70]####
            try {//####[70]####
                __pt__parallelSearch__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__parallelSearch", new Class[] {//####[70]####
                    //####[70]####
                });//####[70]####
            } catch (Exception e) {//####[70]####
                e.printStackTrace();//####[70]####
            }//####[70]####
        }//####[70]####
    }//####[70]####
    TaskIDGroup<Void> parallelSearch() {//####[70]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[70]####
        return parallelSearch(new TaskInfo());//####[70]####
    }//####[70]####
    TaskIDGroup<Void> parallelSearch(TaskInfo taskinfo) {//####[70]####
        // ensure Method variable is set//####[70]####
        if (__pt__parallelSearch__method == null) {//####[70]####
            __pt__parallelSearch__ensureMethodVarSet();//####[70]####
        }//####[70]####
        taskinfo.setParameters();//####[70]####
        taskinfo.setMethod(__pt__parallelSearch__method);//####[70]####
        taskinfo.setInstance(this);//####[70]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[70]####
    }//####[70]####
    public void __pt__parallelSearch() {//####[70]####
        while (!openQueue.isEmpty()) //####[71]####
        {//####[71]####
            StateWeights stateWeight = openQueue.poll();//####[74]####
            if (stateWeight == null) //####[75]####
            {//####[75]####
                TaskNode initialNode = new TaskNode();//####[76]####
                Path initialPath = new Path(initialNode);//####[77]####
                stateWeight = new StateWeights(initialPath, 0.0);//####[78]####
            }//####[79]####
            if (isComplete(stateWeight)) //####[80]####
            {//####[80]####
                threadPathList.add(stateWeight.getState());//####[82]####
                break;//####[83]####
            } else {//####[84]####
                expandState(stateWeight, options.getNumProcessors());//####[86]####
            }//####[87]####
            closedQueue.add(stateWeight);//####[88]####
        }//####[89]####
    }//####[90]####
//####[90]####
//####[93]####
    private Path getSmallestPathFromList() {//####[93]####
        int smallestFinPath = Integer.MAX_VALUE;//####[95]####
        int finishTimeOfPath = 0;//####[96]####
        Path optimalPath = null;//####[97]####
        for (Path p : threadPathList) //####[99]####
        {//####[99]####
            finishTimeOfPath = 0;//####[100]####
            for (TaskNode n : p.getPath()) //####[102]####
            {//####[102]####
                if (n.finishTime > finishTimeOfPath) //####[103]####
                {//####[103]####
                    finishTimeOfPath = n.finishTime;//####[104]####
                }//####[105]####
            }//####[106]####
            if (finishTimeOfPath < smallestFinPath) //####[108]####
            {//####[108]####
                smallestFinPath = finishTimeOfPath;//####[109]####
                optimalPath = p;//####[110]####
            }//####[111]####
        }//####[113]####
        return optimalPath;//####[114]####
    }//####[115]####
//####[118]####
    private void setScheduleOnGraph(Path state) {//####[118]####
        Set<TaskNode> graphNodes = graph.vertexSet();//####[119]####
        for (TaskNode n : state.getPath()) //####[122]####
        {//####[122]####
            for (TaskNode g : graphNodes) //####[123]####
            {//####[123]####
                if (n.name.equals(g.name)) //####[124]####
                {//####[124]####
                    g.setProc(n.allocProc);//####[125]####
                    g.setStart(n.startTime);//####[126]####
                }//####[127]####
            }//####[128]####
        }//####[129]####
    }//####[130]####
//####[135]####
    private void expandState(StateWeights stateWeight, int processors) {//####[135]####
        Path current = stateWeight.state;//####[136]####
        ArrayList<TaskNode> freeNodes = freeNodes(stateWeight);//####[138]####
        for (TaskNode n : freeNodes) //####[140]####
        {//####[140]####
            for (int i = 1; i <= processors; i++) //####[141]####
            {//####[141]####
                TaskNode newNode = new TaskNode(n);//####[143]####
                newNode.setProc(i);//####[144]####
                setNodeTimes(current, newNode, i);//####[145]####
                Path temp = new Path(current, newNode);//####[146]####
                double pathWeight = heuristicCost(temp, stateWeight);//####[147]####
                if (!openQueue.contains(pathWeight) && !closedQueue.contains(pathWeight)) //####[148]####
                {//####[148]####
                    openQueue.add(new StateWeights(temp, pathWeight));//####[149]####
                }//####[150]####
            }//####[151]####
        }//####[152]####
    }//####[153]####
//####[156]####
    public void setNodeTimes(Path current, TaskNode newNode, int processor) {//####[156]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[157]####
        TaskNode graphNode = newNode;//####[158]####
        for (TaskNode n : allNodes) //####[159]####
        {//####[159]####
            if (n.name == newNode.name) //####[160]####
            {//####[160]####
                graphNode = n;//####[161]####
            }//####[162]####
        }//####[163]####
        Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(graphNode);//####[165]####
        int processorEndTime = latestEndTimeOnProcessor(current, processor);//####[167]####
        int parentEndTime = 0;//####[168]####
        int parentProcessor = processor;//####[169]####
        int latestAllowedTime;//####[170]####
        int t = 0;//####[171]####
        if (incomingEdges.isEmpty()) //####[174]####
        {//####[174]####
            newNode.setStart(processorEndTime);//####[175]####
        } else for (DefaultEdge e : incomingEdges) //####[177]####
        {//####[177]####
            int communicationTime = (int) graph.getEdgeWeight(e);//####[178]####
            TaskNode parentNode = graph.getEdgeSource(e);//####[182]####
            ArrayList<TaskNode> setOfNodesInPath = current.getPath();//####[183]####
            for (TaskNode n : setOfNodesInPath) //####[186]####
            {//####[186]####
                if (n.name.equals(parentNode.name)) //####[187]####
                {//####[187]####
                    parentEndTime = n.finishTime;//####[188]####
                    parentProcessor = n.allocProc;//####[189]####
                }//####[190]####
            }//####[191]####
            if (parentProcessor != processor) //####[193]####
            {//####[193]####
                latestAllowedTime = parentEndTime + communicationTime;//####[194]####
            } else {//####[195]####
                latestAllowedTime = parentEndTime;//####[196]####
            }//####[197]####
            if (latestAllowedTime > t) //####[200]####
            {//####[200]####
                t = latestAllowedTime;//####[201]####
            }//####[202]####
        }//####[203]####
        if (t > processorEndTime) //####[206]####
        {//####[206]####
            newNode.setStart(t);//####[207]####
        } else {//####[208]####
            newNode.setStart(processorEndTime);//####[209]####
        }//####[210]####
        newNode.setFinish(newNode.weight + newNode.startTime);//####[213]####
    }//####[214]####
//####[217]####
    private static int latestEndTimeOnProcessor(Path current, int processor) {//####[217]####
        ArrayList<TaskNode> path = current.getPath();//####[218]####
        int currentFinishTime = 0;//####[219]####
        for (TaskNode n : path) //####[220]####
        {//####[220]####
            if (n.allocProc == processor) //####[221]####
            {//####[221]####
                if (n.finishTime > currentFinishTime) //####[222]####
                {//####[222]####
                    currentFinishTime = n.finishTime;//####[223]####
                }//####[224]####
            }//####[225]####
        }//####[226]####
        return currentFinishTime;//####[227]####
    }//####[228]####
//####[232]####
    public double heuristicCost(Path state, StateWeights stateWeight) {//####[232]####
        int maxTime = 0;//####[233]####
        int startTime = 0;//####[234]####
        TaskNode maxNode = new TaskNode();//####[235]####
        int bottomLevel = 0;//####[236]####
        double newPathWeight = 0;//####[237]####
        double idleTime = 0;//####[238]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[239]####
        ArrayList<TaskNode> path = state.getPath();//####[240]####
        double previousPathWeight = stateWeight.pathWeight;//####[241]####
        for (TaskNode n : path) //####[243]####
        {//####[243]####
            if (n.finishTime >= maxTime) //####[244]####
            {//####[244]####
                maxTime = n.finishTime;//####[245]####
                maxNode = n;//####[246]####
            }//####[247]####
        }//####[248]####
        TaskNode graphNode = maxNode;//####[250]####
        for (TaskNode n : allNodes) //####[251]####
        {//####[251]####
            if (n.name == maxNode.name) //####[252]####
            {//####[252]####
                graphNode = n;//####[253]####
            }//####[254]####
        }//####[255]####
        bottomLevel = ComputationalBottomLevel(graphNode);//####[257]####
        startTime = maxNode.startTime;//####[260]####
        idleTime = getIdleTime(state, graphNode, stateWeight);//####[263]####
        newPathWeight = (double) startTime + (double) (bottomLevel + idleTime);//####[267]####
        if (newPathWeight > previousPathWeight) //####[271]####
        {//####[271]####
            return newPathWeight;//####[272]####
        } else {//####[273]####
            return previousPathWeight;//####[274]####
        }//####[275]####
    }//####[276]####
//####[280]####
    private int ComputationalBottomLevel(TaskNode node) {//####[280]####
        int bottomLevel = 0;//####[281]####
        Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(node);//####[283]####
        if (outgoingEdges.isEmpty()) //####[285]####
        {//####[285]####
            return node.weight;//####[286]####
        } else for (DefaultEdge e : outgoingEdges) //####[290]####
        {//####[290]####
            TaskNode successor = graph.getEdgeTarget(e);//####[291]####
            int temp = ComputationalBottomLevel(successor);//####[292]####
            if (temp > bottomLevel) //####[294]####
            {//####[294]####
                bottomLevel = temp;//####[295]####
            }//####[296]####
        }//####[297]####
        return (node.weight + bottomLevel);//####[298]####
    }//####[299]####
//####[301]####
    private double getIdleTime(Path state, TaskNode currentNode, StateWeights stateWeight) {//####[301]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[303]####
        ArrayList<TaskNode> parents = new ArrayList<TaskNode>();//####[304]####
        freeNodes = freeNodes(stateWeight);//####[305]####
        double earliestStartTime = Double.MAX_VALUE;//####[306]####
        double criticalParentFinTime = 0;//####[307]####
        ArrayList<Double> idleTime = new ArrayList<Double>();//####[308]####
        double dataReadyTime = 0;//####[309]####
        double nodeIdleTime = 0;//####[310]####
        for (TaskNode f : freeNodes) //####[315]####
        {//####[315]####
            parents.clear();//####[316]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(f);//####[317]####
            for (DefaultEdge incomingEdge : incomingEdges) //####[318]####
            {//####[318]####
                parents.add(graph.getEdgeSource(incomingEdge));//####[319]####
            }//####[320]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[321]####
            {//####[321]####
                for (TaskNode parent : parents) //####[323]####
                {//####[323]####
                    if (parent.allocProc == i) //####[324]####
                    {//####[324]####
                        dataReadyTime = latestEndTimeOnProcessor(state, i);//####[325]####
                    } else {//####[326]####
                        DefaultEdge edge = graph.getEdge(parent, f);//####[327]####
                        dataReadyTime = Math.max((parent.finishTime + graph.getEdgeWeight(edge)), latestEndTimeOnProcessor(state, i));//####[328]####
                    }//####[330]####
                    if (dataReadyTime > criticalParentFinTime) //####[331]####
                    {//####[331]####
                        criticalParentFinTime = dataReadyTime;//####[332]####
                    }//####[333]####
                }//####[334]####
                if (criticalParentFinTime < earliestStartTime) //####[335]####
                {//####[335]####
                    earliestStartTime = criticalParentFinTime;//####[336]####
                }//####[337]####
            }//####[338]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[339]####
            {//####[339]####
                double temp = earliestStartTime - latestEndTimeOnProcessor(state, i);//####[340]####
                if (temp > 0) //####[341]####
                {//####[341]####
                    nodeIdleTime += temp;//####[342]####
                }//####[343]####
            }//####[344]####
            idleTime.add(nodeIdleTime);//####[345]####
        }//####[347]####
        return (Collections.min(idleTime)) / options.getNumProcessors();//####[348]####
    }//####[349]####
//####[353]####
    @SuppressWarnings("unchecked")//####[353]####
    private ArrayList<TaskNode> freeNodes(StateWeights stateWeight) {//####[353]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[355]####
        ArrayList<String> used = new ArrayList<String>();//####[356]####
        ArrayList<String> all = new ArrayList<String>();//####[357]####
        ArrayList<String> unused = new ArrayList<String>();//####[358]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[359]####
        for (TaskNode n : allNodes) //####[361]####
        {//####[361]####
            all.add(n.name);//####[362]####
        }//####[363]####
        for (TaskNode n : usedNodes) //####[365]####
        {//####[365]####
            used.add(n.name);//####[366]####
        }//####[367]####
        all.removeAll(used);//####[369]####
        unused = (ArrayList<String>) all.clone();//####[370]####
        for (TaskNode n : allNodes) //####[373]####
        {//####[373]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(n);//####[374]####
            for (DefaultEdge e : incomingEdges) //####[375]####
            {//####[375]####
                TaskNode edgeNode = graph.getEdgeSource(e);//####[376]####
                if (unused.contains(edgeNode.name)) //####[377]####
                {//####[377]####
                    all.remove(n.name);//####[378]####
                }//####[379]####
            }//####[380]####
        }//####[381]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[383]####
        for (TaskNode n : allNodes) //####[384]####
        {//####[384]####
            if (all.contains(n.name)) //####[385]####
            {//####[385]####
                freeNodes.add(n);//####[386]####
            }//####[387]####
        }//####[388]####
        return freeNodes;//####[390]####
    }//####[391]####
//####[394]####
    public boolean isComplete(StateWeights stateWeight) {//####[394]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[395]####
        ArrayList<String> used = new ArrayList<String>();//####[396]####
        ArrayList<String> all = new ArrayList<String>();//####[397]####
        for (TaskNode n : usedNodes) //####[399]####
        {//####[399]####
            used.add(n.name);//####[400]####
        }//####[401]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[402]####
        for (TaskNode n : allNodes) //####[404]####
        {//####[404]####
            all.add(n.name);//####[405]####
        }//####[406]####
        all.removeAll(used);//####[408]####
        if (all.isEmpty()) //####[409]####
        {//####[409]####
            return true;//####[410]####
        } else {//####[411]####
            return false;//####[412]####
        }//####[413]####
    }//####[414]####
}//####[414]####
