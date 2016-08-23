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
        TaskIDGroup<Void> taskGroup = new TaskIDGroup(options.getNumThreads());//####[51]####
        for (int i = 0; i < options.getNumThreads(); i++) //####[52]####
        {//####[52]####
            TaskID id = parallelSearch();//####[53]####
            taskGroup.add(id);//####[54]####
        }//####[55]####
        try {//####[57]####
            System.out.println("Waiting on threads....");//####[58]####
            taskGroup.waitTillFinished();//####[59]####
        } catch (Exception e) {//####[60]####
            e.printStackTrace();//####[61]####
        }//####[62]####
        int smallestFinPath = Integer.MAX_VALUE;//####[65]####
        System.out.println("Max Value: " + smallestFinPath);//####[66]####
        System.out.println("Size of the list: " + threadPathList.size());//####[67]####
        Path optimalPath = null;//####[68]####
        for (Path p : threadPathList) //####[69]####
        {//####[69]####
            for (TaskNode n : p.getPath()) //####[70]####
            {//####[70]####
                System.out.println("Node finish time: " + n.finishTime);//####[71]####
                if (n.finishTime < smallestFinPath) //####[72]####
                {//####[72]####
                    System.out.println("Hello");//####[73]####
                    smallestFinPath = n.finishTime;//####[74]####
                    optimalPath = p;//####[75]####
                }//####[76]####
            }//####[77]####
        }//####[78]####
        setScheduleOnGraph(optimalPath);//####[80]####
        System.out.println("got to here");//####[81]####
    }//####[83]####
//####[85]####
    private static volatile Method __pt__parallelSearch__method = null;//####[85]####
    private synchronized static void __pt__parallelSearch__ensureMethodVarSet() {//####[85]####
        if (__pt__parallelSearch__method == null) {//####[85]####
            try {//####[85]####
                __pt__parallelSearch__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__parallelSearch", new Class[] {//####[85]####
                    //####[85]####
                });//####[85]####
            } catch (Exception e) {//####[85]####
                e.printStackTrace();//####[85]####
            }//####[85]####
        }//####[85]####
    }//####[85]####
    TaskIDGroup<Void> parallelSearch() {//####[85]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[85]####
        return parallelSearch(new TaskInfo());//####[85]####
    }//####[85]####
    TaskIDGroup<Void> parallelSearch(TaskInfo taskinfo) {//####[85]####
        // ensure Method variable is set//####[85]####
        if (__pt__parallelSearch__method == null) {//####[85]####
            __pt__parallelSearch__ensureMethodVarSet();//####[85]####
        }//####[85]####
        taskinfo.setParameters();//####[85]####
        taskinfo.setMethod(__pt__parallelSearch__method);//####[85]####
        taskinfo.setInstance(this);//####[85]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[85]####
    }//####[85]####
    public void __pt__parallelSearch() {//####[85]####
        System.out.println("Harry bro");//####[86]####
        while (!openQueue.isEmpty()) //####[87]####
        {//####[87]####
            StateWeights stateWeight = openQueue.poll();//####[90]####
            if (isComplete(stateWeight)) //####[91]####
            {//####[91]####
                System.out.println("gets to here or not?");//####[93]####
                threadPathList.add(stateWeight.getState());//####[94]####
                System.out.println("gets to here?");//####[95]####
                break;//####[96]####
            } else {//####[97]####
                expandState(stateWeight, options.getNumProcessors());//####[99]####
            }//####[100]####
            closedQueue.add(stateWeight);//####[101]####
        }//####[102]####
    }//####[103]####
//####[103]####
//####[106]####
    private void setScheduleOnGraph(Path state) {//####[106]####
        Set<TaskNode> graphNodes = graph.vertexSet();//####[107]####
        for (TaskNode n : state.getPath()) //####[110]####
        {//####[110]####
            for (TaskNode g : graphNodes) //####[111]####
            {//####[111]####
                if (n.name.equals(g.name)) //####[112]####
                {//####[112]####
                    g.setProc(n.allocProc);//####[113]####
                    g.setStart(n.startTime);//####[114]####
                }//####[115]####
            }//####[116]####
        }//####[117]####
    }//####[118]####
//####[123]####
    private void expandState(StateWeights stateWeight, int processors) {//####[123]####
        Path current = stateWeight.state;//####[124]####
        ArrayList<TaskNode> freeNodes = freeNodes(stateWeight);//####[126]####
        for (TaskNode n : freeNodes) //####[128]####
        {//####[128]####
            for (int i = 1; i <= processors; i++) //####[129]####
            {//####[129]####
                TaskNode newNode = new TaskNode(n);//####[131]####
                newNode.setProc(i);//####[132]####
                setNodeTimes(current, newNode, i);//####[133]####
                Path temp = new Path(current, newNode);//####[134]####
                double pathWeight = heuristicCost(temp, stateWeight);//####[135]####
                if (!openQueue.contains(pathWeight) && !closedQueue.contains(pathWeight)) //####[136]####
                {//####[136]####
                    openQueue.add(new StateWeights(temp, pathWeight));//####[137]####
                }//####[138]####
            }//####[147]####
        }//####[148]####
    }//####[149]####
//####[152]####
    public void setNodeTimes(Path current, TaskNode newNode, int processor) {//####[152]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[153]####
        TaskNode graphNode = newNode;//####[154]####
        for (TaskNode n : allNodes) //####[155]####
        {//####[155]####
            if (n.name == newNode.name) //####[156]####
            {//####[156]####
                graphNode = n;//####[157]####
            }//####[158]####
        }//####[159]####
        Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(graphNode);//####[161]####
        int processorEndTime = latestEndTimeOnProcessor(current, processor);//####[163]####
        int parentEndTime = 0;//####[164]####
        int parentProcessor = processor;//####[165]####
        int latestAllowedTime;//####[166]####
        int t = 0;//####[167]####
        if (incomingEdges.isEmpty()) //####[170]####
        {//####[170]####
            newNode.setStart(processorEndTime);//####[171]####
        } else for (DefaultEdge e : incomingEdges) //####[173]####
        {//####[173]####
            int communicationTime = (int) graph.getEdgeWeight(e);//####[174]####
            TaskNode parentNode = graph.getEdgeSource(e);//####[178]####
            ArrayList<TaskNode> setOfNodesInPath = current.getPath();//####[179]####
            for (TaskNode n : setOfNodesInPath) //####[182]####
            {//####[182]####
                if (n.name.equals(parentNode.name)) //####[183]####
                {//####[183]####
                    parentEndTime = n.finishTime;//####[184]####
                    parentProcessor = n.allocProc;//####[185]####
                }//####[186]####
            }//####[187]####
            if (parentProcessor != processor) //####[189]####
            {//####[189]####
                latestAllowedTime = parentEndTime + communicationTime;//####[190]####
            } else {//####[191]####
                latestAllowedTime = parentEndTime;//####[192]####
            }//####[193]####
            if (latestAllowedTime > t) //####[196]####
            {//####[196]####
                t = latestAllowedTime;//####[197]####
            }//####[198]####
        }//####[199]####
        if (t > processorEndTime) //####[202]####
        {//####[202]####
            newNode.setStart(t);//####[203]####
        } else {//####[204]####
            newNode.setStart(processorEndTime);//####[205]####
        }//####[206]####
        newNode.setFinish(newNode.weight + newNode.startTime);//####[209]####
    }//####[210]####
//####[213]####
    private static int latestEndTimeOnProcessor(Path current, int processor) {//####[213]####
        ArrayList<TaskNode> path = current.getPath();//####[214]####
        int currentFinishTime = 0;//####[215]####
        for (TaskNode n : path) //####[216]####
        {//####[216]####
            if (n.allocProc == processor) //####[217]####
            {//####[217]####
                if (n.finishTime > currentFinishTime) //####[218]####
                {//####[218]####
                    currentFinishTime = n.finishTime;//####[219]####
                }//####[220]####
            }//####[221]####
        }//####[222]####
        return currentFinishTime;//####[223]####
    }//####[224]####
//####[228]####
    public double heuristicCost(Path state, StateWeights stateWeight) {//####[228]####
        int maxTime = 0;//####[229]####
        int startTime = 0;//####[230]####
        TaskNode maxNode = new TaskNode();//####[231]####
        int bottomLevel = 0;//####[232]####
        double newPathWeight = 0;//####[233]####
        double idleTime = 0;//####[234]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[235]####
        ArrayList<TaskNode> path = state.getPath();//####[236]####
        double previousPathWeight = stateWeight.pathWeight;//####[237]####
        for (TaskNode n : path) //####[239]####
        {//####[239]####
            if (n.finishTime >= maxTime) //####[240]####
            {//####[240]####
                maxTime = n.finishTime;//####[241]####
                maxNode = n;//####[242]####
            }//####[243]####
        }//####[244]####
        TaskNode graphNode = maxNode;//####[246]####
        for (TaskNode n : allNodes) //####[247]####
        {//####[247]####
            if (n.name == maxNode.name) //####[248]####
            {//####[248]####
                graphNode = n;//####[249]####
            }//####[250]####
        }//####[251]####
        bottomLevel = ComputationalBottomLevel(graphNode);//####[253]####
        startTime = maxNode.startTime;//####[256]####
        idleTime = getIdleTime(state, graphNode, stateWeight);//####[259]####
        newPathWeight = (double) startTime + (double) (bottomLevel + idleTime);//####[262]####
        if (newPathWeight > previousPathWeight) //####[265]####
        {//####[265]####
            return newPathWeight;//####[266]####
        } else {//####[267]####
            return previousPathWeight;//####[268]####
        }//####[269]####
    }//####[270]####
//####[273]####
    private int ComputationalBottomLevel(TaskNode node) {//####[273]####
        int bottomLevel = 0;//####[274]####
        Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(node);//####[276]####
        if (outgoingEdges.isEmpty()) //####[278]####
        {//####[278]####
            return node.weight;//####[279]####
        } else for (DefaultEdge e : outgoingEdges) //####[281]####
        {//####[281]####
            TaskNode successor = graph.getEdgeTarget(e);//####[282]####
            int temp = ComputationalBottomLevel(successor);//####[283]####
            if (temp > bottomLevel) //####[285]####
            {//####[285]####
                bottomLevel = temp;//####[286]####
            }//####[287]####
        }//####[288]####
        return (node.weight + bottomLevel);//####[289]####
    }//####[290]####
//####[292]####
    private double getIdleTime(Path state, TaskNode currentNode, StateWeights stateWeight) {//####[292]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[294]####
        ArrayList<TaskNode> parents = new ArrayList<TaskNode>();//####[295]####
        freeNodes = freeNodes(stateWeight);//####[296]####
        double earliestStartTime = Double.MAX_VALUE;//####[297]####
        double criticalParentFinTime = 0;//####[298]####
        ArrayList<Double> idleTime = new ArrayList<Double>();//####[299]####
        double dataReadyTime = 0;//####[300]####
        double nodeIdleTime = 0;//####[301]####
        for (TaskNode f : freeNodes) //####[305]####
        {//####[305]####
            parents.clear();//####[307]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(f);//####[308]####
            for (DefaultEdge incomingEdge : incomingEdges) //####[309]####
            {//####[309]####
                parents.add(graph.getEdgeSource(incomingEdge));//####[310]####
            }//####[311]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[312]####
            {//####[312]####
                for (TaskNode parent : parents) //####[314]####
                {//####[314]####
                    if (parent.allocProc == i) //####[315]####
                    {//####[315]####
                        dataReadyTime = parent.finishTime;//####[316]####
                    } else {//####[317]####
                        DefaultEdge edge = graph.getEdge(parent, f);//####[320]####
                        dataReadyTime = parent.finishTime + graph.getEdgeWeight(edge);//####[322]####
                    }//####[323]####
                    if (dataReadyTime > criticalParentFinTime) //####[324]####
                    {//####[324]####
                        criticalParentFinTime = dataReadyTime;//####[325]####
                    }//####[326]####
                }//####[327]####
                if (criticalParentFinTime < earliestStartTime) //####[328]####
                {//####[328]####
                    earliestStartTime = criticalParentFinTime;//####[329]####
                }//####[330]####
            }//####[331]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[332]####
            {//####[332]####
                nodeIdleTime += earliestStartTime - latestEndTimeOnProcessor(state, i);//####[333]####
            }//####[334]####
            idleTime.add(nodeIdleTime);//####[335]####
        }//####[337]####
        return (Collections.max(idleTime)) / options.getNumProcessors();//####[339]####
    }//####[340]####
//####[345]####
    @SuppressWarnings("unchecked")//####[345]####
    private ArrayList<TaskNode> freeNodes(StateWeights stateWeight) {//####[345]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[347]####
        ArrayList<String> used = new ArrayList<String>();//####[348]####
        ArrayList<String> all = new ArrayList<String>();//####[349]####
        ArrayList<String> unused = new ArrayList<String>();//####[350]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[351]####
        for (TaskNode n : allNodes) //####[353]####
        {//####[353]####
            all.add(n.name);//####[354]####
        }//####[355]####
        for (TaskNode n : usedNodes) //####[357]####
        {//####[357]####
            used.add(n.name);//####[358]####
        }//####[359]####
        all.removeAll(used);//####[361]####
        unused = (ArrayList<String>) all.clone();//####[362]####
        for (TaskNode n : allNodes) //####[365]####
        {//####[365]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(n);//####[366]####
            for (DefaultEdge e : incomingEdges) //####[367]####
            {//####[367]####
                TaskNode edgeNode = graph.getEdgeSource(e);//####[368]####
                if (unused.contains(edgeNode.name)) //####[369]####
                {//####[369]####
                    all.remove(n.name);//####[370]####
                }//####[371]####
            }//####[372]####
        }//####[373]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[375]####
        for (TaskNode n : allNodes) //####[376]####
        {//####[376]####
            if (all.contains(n.name)) //####[377]####
            {//####[377]####
                freeNodes.add(n);//####[378]####
            }//####[379]####
        }//####[380]####
        return freeNodes;//####[382]####
    }//####[383]####
//####[386]####
    public boolean isComplete(StateWeights stateWeight) {//####[386]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[387]####
        ArrayList<String> used = new ArrayList<String>();//####[388]####
        ArrayList<String> all = new ArrayList<String>();//####[389]####
        for (TaskNode n : usedNodes) //####[391]####
        {//####[391]####
            used.add(n.name);//####[392]####
        }//####[393]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[394]####
        for (TaskNode n : allNodes) //####[396]####
        {//####[396]####
            all.add(n.name);//####[397]####
        }//####[398]####
        all.removeAll(used);//####[400]####
        if (all.isEmpty()) //####[401]####
        {//####[401]####
            return true;//####[416]####
        } else {//####[417]####
            return false;//####[418]####
        }//####[419]####
    }//####[420]####
}//####[420]####
