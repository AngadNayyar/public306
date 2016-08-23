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
        TaskIDGroup taskGroup = parallelSearch();//####[57]####
        try {//####[59]####
            System.out.println("Waiting on threads....");//####[60]####
            taskGroup.waitTillFinished();//####[61]####
        } catch (Exception e) {//####[62]####
            e.printStackTrace();//####[63]####
        }//####[64]####
        Thread.sleep(3000);//####[66]####
        Path optimalPath = getSmallestPathFromList();//####[68]####
        setScheduleOnGraph(optimalPath);//####[69]####
        System.out.println("got to here");//####[70]####
    }//####[71]####
//####[73]####
    private static volatile Method __pt__parallelSearch__method = null;//####[73]####
    private synchronized static void __pt__parallelSearch__ensureMethodVarSet() {//####[73]####
        if (__pt__parallelSearch__method == null) {//####[73]####
            try {//####[73]####
                __pt__parallelSearch__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__parallelSearch", new Class[] {//####[73]####
                    //####[73]####
                });//####[73]####
            } catch (Exception e) {//####[73]####
                e.printStackTrace();//####[73]####
            }//####[73]####
        }//####[73]####
    }//####[73]####
    TaskIDGroup<Void> parallelSearch() {//####[73]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[73]####
        return parallelSearch(new TaskInfo());//####[73]####
    }//####[73]####
    TaskIDGroup<Void> parallelSearch(TaskInfo taskinfo) {//####[73]####
        // ensure Method variable is set//####[73]####
        if (__pt__parallelSearch__method == null) {//####[73]####
            __pt__parallelSearch__ensureMethodVarSet();//####[73]####
        }//####[73]####
        taskinfo.setParameters();//####[73]####
        taskinfo.setMethod(__pt__parallelSearch__method);//####[73]####
        taskinfo.setInstance(this);//####[73]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[73]####
    }//####[73]####
    public void __pt__parallelSearch() {//####[73]####
        System.out.println("=================== If you got here you fixed something ================");//####[74]####
        while (!openQueue.isEmpty()) //####[75]####
        {//####[75]####
            StateWeights stateWeight = openQueue.poll();//####[78]####
            if (isComplete(stateWeight)) //####[79]####
            {//####[79]####
                System.out.println("gets to here or not?");//####[81]####
                threadPathList.add(stateWeight.getState());//####[82]####
                System.out.println("gets to here?");//####[83]####
                break;//####[84]####
            } else {//####[85]####
                expandState(stateWeight, options.getNumProcessors());//####[87]####
            }//####[88]####
            closedQueue.add(stateWeight);//####[89]####
        }//####[90]####
    }//####[91]####
//####[91]####
//####[94]####
    private Path getSmallestPathFromList() {//####[94]####
        int smallestFinPath = Integer.MAX_VALUE;//####[96]####
        int finishTimeOfPath = 0;//####[97]####
        System.out.println("Max Value: " + smallestFinPath);//####[98]####
        System.out.println("Size of the list: " + threadPathList.size());//####[99]####
        Path optimalPath = null;//####[100]####
        for (Path p : threadPathList) //####[102]####
        {//####[102]####
            finishTimeOfPath = 0;//####[103]####
            for (TaskNode n : p.getPath()) //####[105]####
            {//####[105]####
                System.out.println("Node finish time: " + n.finishTime);//####[106]####
                if (n.finishTime > finishTimeOfPath) //####[107]####
                {//####[107]####
                    finishTimeOfPath = n.finishTime;//####[108]####
                }//####[109]####
            }//####[110]####
            if (finishTimeOfPath < smallestFinPath) //####[112]####
            {//####[112]####
                smallestFinPath = finishTimeOfPath;//####[113]####
                optimalPath = p;//####[114]####
            }//####[115]####
        }//####[117]####
        return optimalPath;//####[118]####
    }//####[119]####
//####[122]####
    private void setScheduleOnGraph(Path state) {//####[122]####
        Set<TaskNode> graphNodes = graph.vertexSet();//####[123]####
        for (TaskNode n : state.getPath()) //####[126]####
        {//####[126]####
            for (TaskNode g : graphNodes) //####[127]####
            {//####[127]####
                if (n.name.equals(g.name)) //####[128]####
                {//####[128]####
                    g.setProc(n.allocProc);//####[129]####
                    g.setStart(n.startTime);//####[130]####
                }//####[131]####
            }//####[132]####
        }//####[133]####
    }//####[134]####
//####[139]####
    private void expandState(StateWeights stateWeight, int processors) {//####[139]####
        Path current = stateWeight.state;//####[140]####
        ArrayList<TaskNode> freeNodes = freeNodes(stateWeight);//####[142]####
        for (TaskNode n : freeNodes) //####[144]####
        {//####[144]####
            for (int i = 1; i <= processors; i++) //####[145]####
            {//####[145]####
                TaskNode newNode = new TaskNode(n);//####[147]####
                newNode.setProc(i);//####[148]####
                setNodeTimes(current, newNode, i);//####[149]####
                Path temp = new Path(current, newNode);//####[150]####
                double pathWeight = heuristicCost(temp, stateWeight);//####[151]####
                if (!openQueue.contains(pathWeight) && !closedQueue.contains(pathWeight)) //####[152]####
                {//####[152]####
                    openQueue.add(new StateWeights(temp, pathWeight));//####[153]####
                }//####[154]####
            }//####[163]####
        }//####[164]####
    }//####[165]####
//####[168]####
    public void setNodeTimes(Path current, TaskNode newNode, int processor) {//####[168]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[169]####
        TaskNode graphNode = newNode;//####[170]####
        for (TaskNode n : allNodes) //####[171]####
        {//####[171]####
            if (n.name == newNode.name) //####[172]####
            {//####[172]####
                graphNode = n;//####[173]####
            }//####[174]####
        }//####[175]####
        Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(graphNode);//####[177]####
        int processorEndTime = latestEndTimeOnProcessor(current, processor);//####[179]####
        int parentEndTime = 0;//####[180]####
        int parentProcessor = processor;//####[181]####
        int latestAllowedTime;//####[182]####
        int t = 0;//####[183]####
        if (incomingEdges.isEmpty()) //####[186]####
        {//####[186]####
            newNode.setStart(processorEndTime);//####[187]####
        } else for (DefaultEdge e : incomingEdges) //####[189]####
        {//####[189]####
            int communicationTime = (int) graph.getEdgeWeight(e);//####[190]####
            TaskNode parentNode = graph.getEdgeSource(e);//####[194]####
            ArrayList<TaskNode> setOfNodesInPath = current.getPath();//####[195]####
            for (TaskNode n : setOfNodesInPath) //####[198]####
            {//####[198]####
                if (n.name.equals(parentNode.name)) //####[199]####
                {//####[199]####
                    parentEndTime = n.finishTime;//####[200]####
                    parentProcessor = n.allocProc;//####[201]####
                }//####[202]####
            }//####[203]####
            if (parentProcessor != processor) //####[205]####
            {//####[205]####
                latestAllowedTime = parentEndTime + communicationTime;//####[206]####
            } else {//####[207]####
                latestAllowedTime = parentEndTime;//####[208]####
            }//####[209]####
            if (latestAllowedTime > t) //####[212]####
            {//####[212]####
                t = latestAllowedTime;//####[213]####
            }//####[214]####
        }//####[215]####
        if (t > processorEndTime) //####[218]####
        {//####[218]####
            newNode.setStart(t);//####[219]####
        } else {//####[220]####
            newNode.setStart(processorEndTime);//####[221]####
        }//####[222]####
        newNode.setFinish(newNode.weight + newNode.startTime);//####[225]####
    }//####[226]####
//####[229]####
    private static int latestEndTimeOnProcessor(Path current, int processor) {//####[229]####
        ArrayList<TaskNode> path = current.getPath();//####[230]####
        int currentFinishTime = 0;//####[231]####
        for (TaskNode n : path) //####[232]####
        {//####[232]####
            if (n.allocProc == processor) //####[233]####
            {//####[233]####
                if (n.finishTime > currentFinishTime) //####[234]####
                {//####[234]####
                    currentFinishTime = n.finishTime;//####[235]####
                }//####[236]####
            }//####[237]####
        }//####[238]####
        return currentFinishTime;//####[239]####
    }//####[240]####
//####[244]####
    public double heuristicCost(Path state, StateWeights stateWeight) {//####[244]####
        int maxTime = 0;//####[245]####
        int startTime = 0;//####[246]####
        TaskNode maxNode = new TaskNode();//####[247]####
        int bottomLevel = 0;//####[248]####
        double newPathWeight = 0;//####[249]####
        double idleTime = 0;//####[250]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[251]####
        ArrayList<TaskNode> path = state.getPath();//####[252]####
        double previousPathWeight = stateWeight.pathWeight;//####[253]####
        for (TaskNode n : path) //####[255]####
        {//####[255]####
            if (n.finishTime >= maxTime) //####[256]####
            {//####[256]####
                maxTime = n.finishTime;//####[257]####
                maxNode = n;//####[258]####
            }//####[259]####
        }//####[260]####
        TaskNode graphNode = maxNode;//####[262]####
        for (TaskNode n : allNodes) //####[263]####
        {//####[263]####
            if (n.name == maxNode.name) //####[264]####
            {//####[264]####
                graphNode = n;//####[265]####
            }//####[266]####
        }//####[267]####
        bottomLevel = ComputationalBottomLevel(graphNode);//####[269]####
        startTime = maxNode.startTime;//####[272]####
        idleTime = getIdleTime(state, graphNode, stateWeight);//####[275]####
        newPathWeight = (double) startTime + (double) (bottomLevel + idleTime);//####[278]####
        if (newPathWeight > previousPathWeight) //####[281]####
        {//####[281]####
            return newPathWeight;//####[282]####
        } else {//####[283]####
            return previousPathWeight;//####[284]####
        }//####[285]####
    }//####[286]####
//####[289]####
    private int ComputationalBottomLevel(TaskNode node) {//####[289]####
        int bottomLevel = 0;//####[290]####
        Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(node);//####[292]####
        if (outgoingEdges.isEmpty()) //####[294]####
        {//####[294]####
            return node.weight;//####[295]####
        } else for (DefaultEdge e : outgoingEdges) //####[297]####
        {//####[297]####
            TaskNode successor = graph.getEdgeTarget(e);//####[298]####
            int temp = ComputationalBottomLevel(successor);//####[299]####
            if (temp > bottomLevel) //####[301]####
            {//####[301]####
                bottomLevel = temp;//####[302]####
            }//####[303]####
        }//####[304]####
        return (node.weight + bottomLevel);//####[305]####
    }//####[306]####
//####[308]####
    private double getIdleTime(Path state, TaskNode currentNode, StateWeights stateWeight) {//####[308]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[310]####
        ArrayList<TaskNode> parents = new ArrayList<TaskNode>();//####[311]####
        freeNodes = freeNodes(stateWeight);//####[312]####
        double earliestStartTime = Double.MAX_VALUE;//####[313]####
        double criticalParentFinTime = 0;//####[314]####
        ArrayList<Double> idleTime = new ArrayList<Double>();//####[315]####
        double dataReadyTime = 0;//####[316]####
        double nodeIdleTime = 0;//####[317]####
        for (TaskNode f : freeNodes) //####[321]####
        {//####[321]####
            parents.clear();//####[323]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(f);//####[324]####
            for (DefaultEdge incomingEdge : incomingEdges) //####[325]####
            {//####[325]####
                parents.add(graph.getEdgeSource(incomingEdge));//####[326]####
            }//####[327]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[328]####
            {//####[328]####
                for (TaskNode parent : parents) //####[330]####
                {//####[330]####
                    if (parent.allocProc == i) //####[331]####
                    {//####[331]####
                        dataReadyTime = parent.finishTime;//####[332]####
                    } else {//####[333]####
                        DefaultEdge edge = graph.getEdge(parent, f);//####[336]####
                        dataReadyTime = parent.finishTime + graph.getEdgeWeight(edge);//####[338]####
                    }//####[339]####
                    if (dataReadyTime > criticalParentFinTime) //####[340]####
                    {//####[340]####
                        criticalParentFinTime = dataReadyTime;//####[341]####
                    }//####[342]####
                }//####[343]####
                if (criticalParentFinTime < earliestStartTime) //####[344]####
                {//####[344]####
                    earliestStartTime = criticalParentFinTime;//####[345]####
                }//####[346]####
            }//####[347]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[348]####
            {//####[348]####
                nodeIdleTime += earliestStartTime - latestEndTimeOnProcessor(state, i);//####[349]####
            }//####[350]####
            idleTime.add(nodeIdleTime);//####[351]####
        }//####[353]####
        return (Collections.max(idleTime)) / options.getNumProcessors();//####[355]####
    }//####[356]####
//####[361]####
    @SuppressWarnings("unchecked")//####[361]####
    private ArrayList<TaskNode> freeNodes(StateWeights stateWeight) {//####[361]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[363]####
        ArrayList<String> used = new ArrayList<String>();//####[364]####
        ArrayList<String> all = new ArrayList<String>();//####[365]####
        ArrayList<String> unused = new ArrayList<String>();//####[366]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[367]####
        for (TaskNode n : allNodes) //####[369]####
        {//####[369]####
            all.add(n.name);//####[370]####
        }//####[371]####
        for (TaskNode n : usedNodes) //####[373]####
        {//####[373]####
            used.add(n.name);//####[374]####
        }//####[375]####
        all.removeAll(used);//####[377]####
        unused = (ArrayList<String>) all.clone();//####[378]####
        for (TaskNode n : allNodes) //####[381]####
        {//####[381]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(n);//####[382]####
            for (DefaultEdge e : incomingEdges) //####[383]####
            {//####[383]####
                TaskNode edgeNode = graph.getEdgeSource(e);//####[384]####
                if (unused.contains(edgeNode.name)) //####[385]####
                {//####[385]####
                    all.remove(n.name);//####[386]####
                }//####[387]####
            }//####[388]####
        }//####[389]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[391]####
        for (TaskNode n : allNodes) //####[392]####
        {//####[392]####
            if (all.contains(n.name)) //####[393]####
            {//####[393]####
                freeNodes.add(n);//####[394]####
            }//####[395]####
        }//####[396]####
        return freeNodes;//####[398]####
    }//####[399]####
//####[402]####
    public boolean isComplete(StateWeights stateWeight) {//####[402]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[403]####
        ArrayList<String> used = new ArrayList<String>();//####[404]####
        ArrayList<String> all = new ArrayList<String>();//####[405]####
        for (TaskNode n : usedNodes) //####[407]####
        {//####[407]####
            used.add(n.name);//####[408]####
        }//####[409]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[410]####
        for (TaskNode n : allNodes) //####[412]####
        {//####[412]####
            all.add(n.name);//####[413]####
        }//####[414]####
        all.removeAll(used);//####[416]####
        if (all.isEmpty()) //####[417]####
        {//####[417]####
            return true;//####[432]####
        } else {//####[433]####
            return false;//####[434]####
        }//####[435]####
    }//####[436]####
}//####[436]####
