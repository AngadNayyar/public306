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
import processing_classes.Options;//####[18]####
import processing_classes.TaskNode;//####[19]####
import pt.runtime.TaskID;//####[20]####
import pt.runtime.TaskIDGroup;//####[21]####
//####[21]####
//-- ParaTask related imports//####[21]####
import pt.runtime.*;//####[21]####
import java.util.concurrent.ExecutionException;//####[21]####
import java.util.concurrent.locks.*;//####[21]####
import java.lang.reflect.*;//####[21]####
import pt.runtime.GuiThread;//####[21]####
import java.util.concurrent.BlockingQueue;//####[21]####
import java.util.ArrayList;//####[21]####
import java.util.List;//####[21]####
//####[21]####
public class AStarParr {//####[23]####
    static{ParaTask.init();}//####[23]####
    /*  ParaTask helper method to access private/protected slots *///####[23]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[23]####
        if (m.getParameterTypes().length == 0)//####[23]####
            m.invoke(instance);//####[23]####
        else if ((m.getParameterTypes().length == 1))//####[23]####
            m.invoke(instance, arg);//####[23]####
        else //####[23]####
            m.invoke(instance, arg, interResult);//####[23]####
    }//####[23]####
//####[25]####
    private PriorityBlockingQueue<StateWeights> openQueue = new PriorityBlockingQueue<StateWeights>();//####[25]####
//####[26]####
    private PriorityBlockingQueue<StateWeights> closedQueue = new PriorityBlockingQueue<StateWeights>();//####[26]####
//####[27]####
    private int numProc;//####[27]####
//####[28]####
    private DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph = new DefaultDirectedWeightedGraph<TaskNode, DefaultEdge>(DefaultWeightedEdge.class);//####[28]####
//####[28]####
    ;//####[28]####
//####[29]####
    private Options options;//####[29]####
//####[30]####
    private CopyOnWriteArrayList<Path> threadPathList = new CopyOnWriteArrayList<Path>();//####[30]####
//####[33]####
    public AStarParr(DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph, Options options) {//####[33]####
        this.graph = graph;//####[34]####
        this.options = options;//####[35]####
    }//####[36]####
//####[38]####
    public AStarParr(DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph) {//####[38]####
        this.graph = graph;//####[39]####
    }//####[40]####
//####[42]####
    public void solveAstar() throws InterruptedException {//####[42]####
        TaskNode initialNode = new TaskNode();//####[45]####
        Path initialPath = new Path(initialNode);//####[46]####
        StateWeights initialSW = new StateWeights(initialPath, 0.0);//####[47]####
        openQueue.add(initialSW);//####[48]####
        options.setNumThreads(4);//####[50]####
        TaskIDGroup taskGroup = new TaskIDGroup(options.getNumThreads());//####[51]####
        System.out.println(options.getNumThreads());//####[52]####
        for (int i = 0; i < options.getNumThreads(); i++) //####[53]####
        {//####[53]####
            TaskID id = parallelSearch();//####[54]####
            taskGroup.add(id);//####[55]####
        }//####[56]####
        try {//####[60]####
            System.out.println("Waiting on threads....");//####[61]####
            taskGroup.waitTillFinished();//####[62]####
        } catch (Exception e) {//####[63]####
            e.printStackTrace();//####[64]####
        }//####[65]####
        System.out.println("NothingPastHereM8y");//####[67]####
        Path optimalPath = getSmallestPathFromList();//####[70]####
        setScheduleOnGraph(optimalPath);//####[71]####
        System.out.println("got to here");//####[72]####
    }//####[73]####
//####[75]####
    private static volatile Method __pt__parallelSearch__method = null;//####[75]####
    private synchronized static void __pt__parallelSearch__ensureMethodVarSet() {//####[75]####
        if (__pt__parallelSearch__method == null) {//####[75]####
            try {//####[75]####
                __pt__parallelSearch__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__parallelSearch", new Class[] {//####[75]####
                    //####[75]####
                });//####[75]####
            } catch (Exception e) {//####[75]####
                e.printStackTrace();//####[75]####
            }//####[75]####
        }//####[75]####
    }//####[75]####
    TaskIDGroup<Void> parallelSearch() {//####[75]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[75]####
        return parallelSearch(new TaskInfo());//####[75]####
    }//####[75]####
    TaskIDGroup<Void> parallelSearch(TaskInfo taskinfo) {//####[75]####
        // ensure Method variable is set//####[75]####
        if (__pt__parallelSearch__method == null) {//####[75]####
            __pt__parallelSearch__ensureMethodVarSet();//####[75]####
        }//####[75]####
        taskinfo.setParameters();//####[75]####
        taskinfo.setMethod(__pt__parallelSearch__method);//####[75]####
        taskinfo.setInstance(this);//####[75]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[75]####
    }//####[75]####
    public void __pt__parallelSearch() {//####[75]####
        System.out.println("hello");//####[76]####
        while (!openQueue.isEmpty()) //####[77]####
        {//####[77]####
            StateWeights stateWeight = openQueue.poll();//####[80]####
            if (stateWeight == null) //####[81]####
            {//####[81]####
                TaskNode initialNode = new TaskNode();//####[82]####
                Path initialPath = new Path(initialNode);//####[83]####
                stateWeight = new StateWeights(initialPath, 0.0);//####[84]####
            }//####[85]####
            if (isComplete(stateWeight)) //####[86]####
            {//####[86]####
                threadPathList.add(stateWeight.getState());//####[88]####
                System.out.println("cheeky");//####[89]####
                break;//####[90]####
            } else {//####[91]####
                expandState(stateWeight, options.getNumProcessors());//####[93]####
            }//####[94]####
            closedQueue.add(stateWeight);//####[95]####
        }//####[96]####
    }//####[97]####
//####[97]####
//####[100]####
    private Path getSmallestPathFromList() {//####[100]####
        int smallestFinPath = Integer.MAX_VALUE;//####[102]####
        int finishTimeOfPath = 0;//####[103]####
        System.out.println("Max Value: " + smallestFinPath);//####[104]####
        System.out.println("Size of the list: " + threadPathList.size());//####[105]####
        Path optimalPath = null;//####[106]####
        for (Path p : threadPathList) //####[108]####
        {//####[108]####
            finishTimeOfPath = 0;//####[109]####
            for (TaskNode n : p.getPath()) //####[111]####
            {//####[111]####
                if (n.finishTime > finishTimeOfPath) //####[112]####
                {//####[112]####
                    finishTimeOfPath = n.finishTime;//####[113]####
                }//####[114]####
            }//####[115]####
            if (finishTimeOfPath < smallestFinPath) //####[117]####
            {//####[117]####
                smallestFinPath = finishTimeOfPath;//####[118]####
                optimalPath = p;//####[119]####
            }//####[120]####
        }//####[122]####
        return optimalPath;//####[123]####
    }//####[124]####
//####[127]####
    private void setScheduleOnGraph(Path state) {//####[127]####
        Set<TaskNode> graphNodes = graph.vertexSet();//####[128]####
        for (TaskNode n : state.getPath()) //####[131]####
        {//####[131]####
            for (TaskNode g : graphNodes) //####[132]####
            {//####[132]####
                if (n.name.equals(g.name)) //####[133]####
                {//####[133]####
                    g.setProc(n.allocProc);//####[134]####
                    g.setStart(n.startTime);//####[135]####
                }//####[136]####
            }//####[137]####
        }//####[138]####
    }//####[139]####
//####[144]####
    private void expandState(StateWeights stateWeight, int processors) {//####[144]####
        Path current = stateWeight.state;//####[145]####
        ArrayList<TaskNode> freeNodes = freeNodes(stateWeight);//####[147]####
        for (TaskNode n : freeNodes) //####[149]####
        {//####[149]####
            for (int i = 1; i <= processors; i++) //####[150]####
            {//####[150]####
                TaskNode newNode = new TaskNode(n);//####[152]####
                newNode.setProc(i);//####[153]####
                setNodeTimes(current, newNode, i);//####[154]####
                Path temp = new Path(current, newNode);//####[155]####
                double pathWeight = heuristicCost(temp, stateWeight);//####[156]####
                if (!openQueue.contains(pathWeight) && !closedQueue.contains(pathWeight)) //####[157]####
                {//####[157]####
                    openQueue.add(new StateWeights(temp, pathWeight));//####[158]####
                }//####[159]####
            }//####[168]####
        }//####[169]####
    }//####[170]####
//####[173]####
    public void setNodeTimes(Path current, TaskNode newNode, int processor) {//####[173]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[174]####
        TaskNode graphNode = newNode;//####[175]####
        for (TaskNode n : allNodes) //####[176]####
        {//####[176]####
            if (n.name == newNode.name) //####[177]####
            {//####[177]####
                graphNode = n;//####[178]####
            }//####[179]####
        }//####[180]####
        Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(graphNode);//####[182]####
        int processorEndTime = latestEndTimeOnProcessor(current, processor);//####[184]####
        int parentEndTime = 0;//####[185]####
        int parentProcessor = processor;//####[186]####
        int latestAllowedTime;//####[187]####
        int t = 0;//####[188]####
        if (incomingEdges.isEmpty()) //####[191]####
        {//####[191]####
            newNode.setStart(processorEndTime);//####[192]####
        } else for (DefaultEdge e : incomingEdges) //####[194]####
        {//####[194]####
            int communicationTime = (int) graph.getEdgeWeight(e);//####[195]####
            TaskNode parentNode = graph.getEdgeSource(e);//####[199]####
            ArrayList<TaskNode> setOfNodesInPath = current.getPath();//####[200]####
            for (TaskNode n : setOfNodesInPath) //####[203]####
            {//####[203]####
                if (n.name.equals(parentNode.name)) //####[204]####
                {//####[204]####
                    parentEndTime = n.finishTime;//####[205]####
                    parentProcessor = n.allocProc;//####[206]####
                }//####[207]####
            }//####[208]####
            if (parentProcessor != processor) //####[210]####
            {//####[210]####
                latestAllowedTime = parentEndTime + communicationTime;//####[211]####
            } else {//####[212]####
                latestAllowedTime = parentEndTime;//####[213]####
            }//####[214]####
            if (latestAllowedTime > t) //####[217]####
            {//####[217]####
                t = latestAllowedTime;//####[218]####
            }//####[219]####
        }//####[220]####
        if (t > processorEndTime) //####[223]####
        {//####[223]####
            newNode.setStart(t);//####[224]####
        } else {//####[225]####
            newNode.setStart(processorEndTime);//####[226]####
        }//####[227]####
        newNode.setFinish(newNode.weight + newNode.startTime);//####[230]####
    }//####[231]####
//####[234]####
    private static int latestEndTimeOnProcessor(Path current, int processor) {//####[234]####
        ArrayList<TaskNode> path = current.getPath();//####[235]####
        int currentFinishTime = 0;//####[236]####
        for (TaskNode n : path) //####[237]####
        {//####[237]####
            if (n.allocProc == processor) //####[238]####
            {//####[238]####
                if (n.finishTime > currentFinishTime) //####[239]####
                {//####[239]####
                    currentFinishTime = n.finishTime;//####[240]####
                }//####[241]####
            }//####[242]####
        }//####[243]####
        return currentFinishTime;//####[244]####
    }//####[245]####
//####[249]####
    public double heuristicCost(Path state, StateWeights stateWeight) {//####[249]####
        int maxTime = 0;//####[250]####
        int startTime = 0;//####[251]####
        TaskNode maxNode = new TaskNode();//####[252]####
        int bottomLevel = 0;//####[253]####
        double newPathWeight = 0;//####[254]####
        double idleTime = 0;//####[255]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[256]####
        ArrayList<TaskNode> path = state.getPath();//####[257]####
        double previousPathWeight = stateWeight.pathWeight;//####[258]####
        for (TaskNode n : path) //####[260]####
        {//####[260]####
            if (n.finishTime >= maxTime) //####[261]####
            {//####[261]####
                maxTime = n.finishTime;//####[262]####
                maxNode = n;//####[263]####
            }//####[264]####
        }//####[265]####
        TaskNode graphNode = maxNode;//####[267]####
        for (TaskNode n : allNodes) //####[268]####
        {//####[268]####
            if (n.name == maxNode.name) //####[269]####
            {//####[269]####
                graphNode = n;//####[270]####
            }//####[271]####
        }//####[272]####
        bottomLevel = ComputationalBottomLevel(graphNode);//####[274]####
        startTime = maxNode.startTime;//####[277]####
        idleTime = getIdleTime(state, graphNode, stateWeight);//####[280]####
        newPathWeight = (double) startTime + (double) (bottomLevel + idleTime);//####[284]####
        if (newPathWeight > previousPathWeight) //####[288]####
        {//####[288]####
            return newPathWeight;//####[289]####
        } else {//####[290]####
            return previousPathWeight;//####[291]####
        }//####[292]####
    }//####[293]####
//####[297]####
    private int ComputationalBottomLevel(TaskNode node) {//####[297]####
        int bottomLevel = 0;//####[298]####
        Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(node);//####[300]####
        if (outgoingEdges.isEmpty()) //####[302]####
        {//####[302]####
            return node.weight;//####[303]####
        } else for (DefaultEdge e : outgoingEdges) //####[307]####
        {//####[307]####
            TaskNode successor = graph.getEdgeTarget(e);//####[308]####
            int temp = ComputationalBottomLevel(successor);//####[309]####
            if (temp > bottomLevel) //####[311]####
            {//####[311]####
                bottomLevel = temp;//####[312]####
            }//####[313]####
        }//####[314]####
        return (node.weight + bottomLevel);//####[315]####
    }//####[316]####
//####[318]####
    private double getIdleTime(Path state, TaskNode currentNode, StateWeights stateWeight) {//####[318]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[320]####
        ArrayList<TaskNode> parents = new ArrayList<TaskNode>();//####[321]####
        freeNodes = freeNodes(stateWeight);//####[322]####
        double earliestStartTime = Double.MAX_VALUE;//####[323]####
        double criticalParentFinTime = 0;//####[324]####
        ArrayList<Double> idleTime = new ArrayList<Double>();//####[325]####
        double dataReadyTime = 0;//####[326]####
        double nodeIdleTime = 0;//####[327]####
        for (TaskNode f : freeNodes) //####[332]####
        {//####[332]####
            parents.clear();//####[334]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(f);//####[335]####
            for (DefaultEdge incomingEdge : incomingEdges) //####[336]####
            {//####[336]####
                parents.add(graph.getEdgeSource(incomingEdge));//####[337]####
            }//####[338]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[339]####
            {//####[339]####
                for (TaskNode parent : parents) //####[341]####
                {//####[341]####
                    if (parent.allocProc == i) //####[342]####
                    {//####[342]####
                        dataReadyTime = latestEndTimeOnProcessor(state, i);//####[343]####
                    } else {//####[344]####
                        DefaultEdge edge = graph.getEdge(parent, f);//####[347]####
                        dataReadyTime = Math.max((parent.finishTime + graph.getEdgeWeight(edge)), latestEndTimeOnProcessor(state, i));//####[349]####
                    }//####[351]####
                    if (dataReadyTime > criticalParentFinTime) //####[352]####
                    {//####[352]####
                        criticalParentFinTime = dataReadyTime;//####[353]####
                    }//####[354]####
                }//####[355]####
                if (criticalParentFinTime < earliestStartTime) //####[356]####
                {//####[356]####
                    earliestStartTime = criticalParentFinTime;//####[357]####
                }//####[358]####
            }//####[359]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[360]####
            {//####[360]####
                double temp = earliestStartTime - latestEndTimeOnProcessor(state, i);//####[361]####
                if (temp > 0) //####[362]####
                {//####[362]####
                    nodeIdleTime += temp;//####[363]####
                }//####[364]####
            }//####[365]####
            idleTime.add(nodeIdleTime);//####[366]####
        }//####[368]####
        return (Collections.min(idleTime)) / options.getNumProcessors();//####[370]####
    }//####[371]####
//####[375]####
    @SuppressWarnings("unchecked")//####[375]####
    private ArrayList<TaskNode> freeNodes(StateWeights stateWeight) {//####[375]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[377]####
        ArrayList<String> used = new ArrayList<String>();//####[378]####
        ArrayList<String> all = new ArrayList<String>();//####[379]####
        ArrayList<String> unused = new ArrayList<String>();//####[380]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[381]####
        for (TaskNode n : allNodes) //####[383]####
        {//####[383]####
            all.add(n.name);//####[384]####
        }//####[385]####
        for (TaskNode n : usedNodes) //####[387]####
        {//####[387]####
            used.add(n.name);//####[388]####
        }//####[389]####
        all.removeAll(used);//####[391]####
        unused = (ArrayList<String>) all.clone();//####[392]####
        for (TaskNode n : allNodes) //####[395]####
        {//####[395]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(n);//####[396]####
            for (DefaultEdge e : incomingEdges) //####[397]####
            {//####[397]####
                TaskNode edgeNode = graph.getEdgeSource(e);//####[398]####
                if (unused.contains(edgeNode.name)) //####[399]####
                {//####[399]####
                    all.remove(n.name);//####[400]####
                }//####[401]####
            }//####[402]####
        }//####[403]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[405]####
        for (TaskNode n : allNodes) //####[406]####
        {//####[406]####
            if (all.contains(n.name)) //####[407]####
            {//####[407]####
                freeNodes.add(n);//####[408]####
            }//####[409]####
        }//####[410]####
        return freeNodes;//####[412]####
    }//####[413]####
//####[416]####
    public boolean isComplete(StateWeights stateWeight) {//####[416]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[417]####
        ArrayList<String> used = new ArrayList<String>();//####[418]####
        ArrayList<String> all = new ArrayList<String>();//####[419]####
        for (TaskNode n : usedNodes) //####[421]####
        {//####[421]####
            used.add(n.name);//####[422]####
        }//####[423]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[424]####
        for (TaskNode n : allNodes) //####[426]####
        {//####[426]####
            all.add(n.name);//####[427]####
        }//####[428]####
        all.removeAll(used);//####[430]####
        if (all.isEmpty()) //####[431]####
        {//####[431]####
            return true;//####[446]####
        } else {//####[447]####
            return false;//####[448]####
        }//####[449]####
    }//####[450]####
}//####[450]####
