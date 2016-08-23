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
import processing_classes.Options;//####[14]####
import processing_classes.TaskNode;//####[15]####
import processing_classes.VisualisationGraph;//####[16]####
import pt.runtime.CurrentTask;//####[17]####
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
public class AStarParrVis {//####[21]####
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
    private CopyOnWriteArrayList<Path> threadPathList = new CopyOnWriteArrayList<Path>();//####[28]####
//####[29]####
    private VisualisationGraph visualGraphObj = new VisualisationGraph();//####[29]####
//####[32]####
    public AStarParrVis(DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph, Options options, VisualisationGraph visualGraphObj) {//####[32]####
        this.graph = graph;//####[33]####
        this.options = options;//####[34]####
        this.visualGraphObj = visualGraphObj;//####[35]####
    }//####[36]####
//####[38]####
    public AStarParrVis(DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph) {//####[38]####
        this.graph = graph;//####[39]####
    }//####[40]####
//####[42]####
    public void solveAstar() throws InterruptedException {//####[42]####
        long startTime = System.currentTimeMillis();//####[44]####
        long counter = 500;//####[45]####
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");//####[48]####
        TaskNode initialNode = new TaskNode();//####[51]####
        Path initialPath = new Path(initialNode);//####[52]####
        StateWeights initialSW = new StateWeights(initialPath, 0.0);//####[53]####
        openQueue.add(initialSW);//####[54]####
        TaskIDGroup taskGroup = new TaskIDGroup(options.getNumThreads());//####[57]####
        for (int i = 0; i < options.getNumThreads(); i++) //####[58]####
        {//####[58]####
            TaskID id = parallelSearch();//####[59]####
            taskGroup.add(id);//####[60]####
        }//####[61]####
        try {//####[63]####
            taskGroup.waitTillFinished();//####[64]####
        } catch (Exception e) {//####[65]####
            e.printStackTrace();//####[66]####
        }//####[67]####
        Path optimalPath = getSmallestPathFromList();//####[69]####
        setScheduleOnGraph(optimalPath);//####[70]####
        Thread.sleep(Math.max(counter, 0));//####[71]####
        counter -= 10;//####[72]####
        StateWeights o = new StateWeights(optimalPath, 0.0);//####[73]####
        visualGraphObj.update(o, options);//####[74]####
    }//####[75]####
//####[77]####
    private static volatile Method __pt__parallelSearch__method = null;//####[77]####
    private synchronized static void __pt__parallelSearch__ensureMethodVarSet() {//####[77]####
        if (__pt__parallelSearch__method == null) {//####[77]####
            try {//####[77]####
                __pt__parallelSearch__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__parallelSearch", new Class[] {//####[77]####
                    //####[77]####
                });//####[77]####
            } catch (Exception e) {//####[77]####
                e.printStackTrace();//####[77]####
            }//####[77]####
        }//####[77]####
    }//####[77]####
    TaskIDGroup<Void> parallelSearch() throws InterruptedException {//####[77]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[77]####
        return parallelSearch(new TaskInfo());//####[77]####
    }//####[77]####
    TaskIDGroup<Void> parallelSearch(TaskInfo taskinfo) throws InterruptedException {//####[77]####
        // ensure Method variable is set//####[77]####
        if (__pt__parallelSearch__method == null) {//####[77]####
            __pt__parallelSearch__ensureMethodVarSet();//####[77]####
        }//####[77]####
        taskinfo.setParameters();//####[77]####
        taskinfo.setMethod(__pt__parallelSearch__method);//####[77]####
        taskinfo.setInstance(this);//####[77]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[77]####
    }//####[77]####
    public void __pt__parallelSearch() throws InterruptedException {//####[77]####
        long startTime = System.currentTimeMillis();//####[78]####
        long counter = 500;//####[79]####
        while (!openQueue.isEmpty()) //####[80]####
        {//####[80]####
            StateWeights stateWeight = openQueue.poll();//####[83]####
            if (stateWeight == null) //####[84]####
            {//####[84]####
                TaskNode initialNode = new TaskNode();//####[85]####
                Path initialPath = new Path(initialNode);//####[86]####
                stateWeight = new StateWeights(initialPath, 0.0);//####[87]####
            }//####[88]####
            if (isComplete(stateWeight)) //####[89]####
            {//####[89]####
                threadPathList.add(stateWeight.getState());//####[91]####
                break;//####[92]####
            } else {//####[93]####
                visualGraphObj.updateNode(stateWeight.state.getCurrent());//####[95]####
                expandState(stateWeight, options.getNumProcessors());//####[96]####
                Thread.sleep(Math.max(counter, 0));//####[97]####
                counter -= 10;//####[98]####
                visualGraphObj.update(stateWeight, options);//####[99]####
            }//####[100]####
            closedQueue.add(stateWeight);//####[101]####
        }//####[102]####
    }//####[103]####
//####[103]####
//####[106]####
    private Path getSmallestPathFromList() {//####[106]####
        int smallestFinPath = Integer.MAX_VALUE;//####[108]####
        int finishTimeOfPath = 0;//####[109]####
        Path optimalPath = null;//####[110]####
        for (Path p : threadPathList) //####[112]####
        {//####[112]####
            finishTimeOfPath = 0;//####[113]####
            for (TaskNode n : p.getPath()) //####[115]####
            {//####[115]####
                if (n.finishTime > finishTimeOfPath) //####[116]####
                {//####[116]####
                    finishTimeOfPath = n.finishTime;//####[117]####
                }//####[118]####
            }//####[119]####
            if (finishTimeOfPath < smallestFinPath) //####[121]####
            {//####[121]####
                smallestFinPath = finishTimeOfPath;//####[122]####
                optimalPath = p;//####[123]####
            }//####[124]####
        }//####[126]####
        return optimalPath;//####[127]####
    }//####[128]####
//####[131]####
    private void setScheduleOnGraph(Path state) {//####[131]####
        Set<TaskNode> graphNodes = graph.vertexSet();//####[132]####
        for (TaskNode n : state.getPath()) //####[135]####
        {//####[135]####
            for (TaskNode g : graphNodes) //####[136]####
            {//####[136]####
                if (n.name.equals(g.name)) //####[137]####
                {//####[137]####
                    g.setProc(n.allocProc);//####[138]####
                    g.setStart(n.startTime);//####[139]####
                }//####[140]####
            }//####[141]####
        }//####[142]####
    }//####[143]####
//####[148]####
    private void expandState(StateWeights stateWeight, int processors) {//####[148]####
        Path current = stateWeight.state;//####[149]####
        ArrayList<TaskNode> freeNodes = freeNodes(stateWeight);//####[151]####
        for (TaskNode n : freeNodes) //####[153]####
        {//####[153]####
            for (int i = 1; i <= processors; i++) //####[154]####
            {//####[154]####
                TaskNode newNode = new TaskNode(n);//####[156]####
                newNode.setProc(i);//####[157]####
                setNodeTimes(current, newNode, i);//####[158]####
                Path temp = new Path(current, newNode);//####[159]####
                double pathWeight = heuristicCost(temp, stateWeight);//####[160]####
                if (!openQueue.contains(pathWeight) && !closedQueue.contains(pathWeight)) //####[161]####
                {//####[161]####
                    openQueue.add(new StateWeights(temp, pathWeight));//####[162]####
                }//####[163]####
            }//####[164]####
        }//####[165]####
    }//####[166]####
//####[169]####
    public void setNodeTimes(Path current, TaskNode newNode, int processor) {//####[169]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[170]####
        TaskNode graphNode = newNode;//####[171]####
        for (TaskNode n : allNodes) //####[172]####
        {//####[172]####
            if (n.name == newNode.name) //####[173]####
            {//####[173]####
                graphNode = n;//####[174]####
            }//####[175]####
        }//####[176]####
        Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(graphNode);//####[178]####
        int processorEndTime = latestEndTimeOnProcessor(current, processor);//####[180]####
        int parentEndTime = 0;//####[181]####
        int parentProcessor = processor;//####[182]####
        int latestAllowedTime;//####[183]####
        int t = 0;//####[184]####
        if (incomingEdges.isEmpty()) //####[187]####
        {//####[187]####
            newNode.setStart(processorEndTime);//####[188]####
        } else for (DefaultEdge e : incomingEdges) //####[190]####
        {//####[190]####
            int communicationTime = (int) graph.getEdgeWeight(e);//####[191]####
            TaskNode parentNode = graph.getEdgeSource(e);//####[195]####
            ArrayList<TaskNode> setOfNodesInPath = current.getPath();//####[196]####
            for (TaskNode n : setOfNodesInPath) //####[199]####
            {//####[199]####
                if (n.name.equals(parentNode.name)) //####[200]####
                {//####[200]####
                    parentEndTime = n.finishTime;//####[201]####
                    parentProcessor = n.allocProc;//####[202]####
                }//####[203]####
            }//####[204]####
            if (parentProcessor != processor) //####[206]####
            {//####[206]####
                latestAllowedTime = parentEndTime + communicationTime;//####[207]####
            } else {//####[208]####
                latestAllowedTime = parentEndTime;//####[209]####
            }//####[210]####
            if (latestAllowedTime > t) //####[213]####
            {//####[213]####
                t = latestAllowedTime;//####[214]####
            }//####[215]####
        }//####[216]####
        if (t > processorEndTime) //####[219]####
        {//####[219]####
            newNode.setStart(t);//####[220]####
        } else {//####[221]####
            newNode.setStart(processorEndTime);//####[222]####
        }//####[223]####
        newNode.setFinish(newNode.weight + newNode.startTime);//####[226]####
    }//####[227]####
//####[230]####
    private static int latestEndTimeOnProcessor(Path current, int processor) {//####[230]####
        ArrayList<TaskNode> path = current.getPath();//####[231]####
        int currentFinishTime = 0;//####[232]####
        for (TaskNode n : path) //####[233]####
        {//####[233]####
            if (n.allocProc == processor) //####[234]####
            {//####[234]####
                if (n.finishTime > currentFinishTime) //####[235]####
                {//####[235]####
                    currentFinishTime = n.finishTime;//####[236]####
                }//####[237]####
            }//####[238]####
        }//####[239]####
        return currentFinishTime;//####[240]####
    }//####[241]####
//####[245]####
    public double heuristicCost(Path state, StateWeights stateWeight) {//####[245]####
        int maxTime = 0;//####[246]####
        int startTime = 0;//####[247]####
        TaskNode maxNode = new TaskNode();//####[248]####
        int bottomLevel = 0;//####[249]####
        double newPathWeight = 0;//####[250]####
        double idleTime = 0;//####[251]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[252]####
        ArrayList<TaskNode> path = state.getPath();//####[253]####
        double previousPathWeight = stateWeight.pathWeight;//####[254]####
        for (TaskNode n : path) //####[256]####
        {//####[256]####
            if (n.finishTime >= maxTime) //####[257]####
            {//####[257]####
                maxTime = n.finishTime;//####[258]####
                maxNode = n;//####[259]####
            }//####[260]####
        }//####[261]####
        TaskNode graphNode = maxNode;//####[263]####
        for (TaskNode n : allNodes) //####[264]####
        {//####[264]####
            if (n.name == maxNode.name) //####[265]####
            {//####[265]####
                graphNode = n;//####[266]####
            }//####[267]####
        }//####[268]####
        bottomLevel = ComputationalBottomLevel(graphNode);//####[270]####
        startTime = maxNode.startTime;//####[273]####
        idleTime = getIdleTime(state, graphNode, stateWeight);//####[276]####
        newPathWeight = (double) startTime + (double) (bottomLevel + idleTime);//####[280]####
        if (newPathWeight > previousPathWeight) //####[284]####
        {//####[284]####
            return newPathWeight;//####[285]####
        } else {//####[286]####
            return previousPathWeight;//####[287]####
        }//####[288]####
    }//####[289]####
//####[293]####
    private int ComputationalBottomLevel(TaskNode node) {//####[293]####
        int bottomLevel = 0;//####[294]####
        Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(node);//####[296]####
        if (outgoingEdges.isEmpty()) //####[298]####
        {//####[298]####
            return node.weight;//####[299]####
        } else for (DefaultEdge e : outgoingEdges) //####[303]####
        {//####[303]####
            TaskNode successor = graph.getEdgeTarget(e);//####[304]####
            int temp = ComputationalBottomLevel(successor);//####[305]####
            if (temp > bottomLevel) //####[307]####
            {//####[307]####
                bottomLevel = temp;//####[308]####
            }//####[309]####
        }//####[310]####
        return (node.weight + bottomLevel);//####[311]####
    }//####[312]####
//####[314]####
    private double getIdleTime(Path state, TaskNode currentNode, StateWeights stateWeight) {//####[314]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[316]####
        ArrayList<TaskNode> parents = new ArrayList<TaskNode>();//####[317]####
        freeNodes = freeNodes(stateWeight);//####[318]####
        double earliestStartTime = Double.MAX_VALUE;//####[319]####
        double criticalParentFinTime = 0;//####[320]####
        ArrayList<Double> idleTime = new ArrayList<Double>();//####[321]####
        double dataReadyTime = 0;//####[322]####
        double nodeIdleTime = 0;//####[323]####
        for (TaskNode f : freeNodes) //####[328]####
        {//####[328]####
            parents.clear();//####[329]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(f);//####[330]####
            for (DefaultEdge incomingEdge : incomingEdges) //####[331]####
            {//####[331]####
                parents.add(graph.getEdgeSource(incomingEdge));//####[332]####
            }//####[333]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[334]####
            {//####[334]####
                for (TaskNode parent : parents) //####[336]####
                {//####[336]####
                    if (parent.allocProc == i) //####[337]####
                    {//####[337]####
                        dataReadyTime = latestEndTimeOnProcessor(state, i);//####[338]####
                    } else {//####[339]####
                        DefaultEdge edge = graph.getEdge(parent, f);//####[340]####
                        dataReadyTime = Math.max((parent.finishTime + graph.getEdgeWeight(edge)), latestEndTimeOnProcessor(state, i));//####[341]####
                    }//####[343]####
                    if (dataReadyTime > criticalParentFinTime) //####[344]####
                    {//####[344]####
                        criticalParentFinTime = dataReadyTime;//####[345]####
                    }//####[346]####
                }//####[347]####
                if (criticalParentFinTime < earliestStartTime) //####[348]####
                {//####[348]####
                    earliestStartTime = criticalParentFinTime;//####[349]####
                }//####[350]####
            }//####[351]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[352]####
            {//####[352]####
                double temp = earliestStartTime - latestEndTimeOnProcessor(state, i);//####[353]####
                if (temp > 0) //####[354]####
                {//####[354]####
                    nodeIdleTime += temp;//####[355]####
                }//####[356]####
            }//####[357]####
            idleTime.add(nodeIdleTime);//####[358]####
        }//####[360]####
        return (Collections.min(idleTime)) / options.getNumProcessors();//####[361]####
    }//####[362]####
//####[366]####
    @SuppressWarnings("unchecked")//####[366]####
    private ArrayList<TaskNode> freeNodes(StateWeights stateWeight) {//####[366]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[368]####
        ArrayList<String> used = new ArrayList<String>();//####[369]####
        ArrayList<String> all = new ArrayList<String>();//####[370]####
        ArrayList<String> unused = new ArrayList<String>();//####[371]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[372]####
        for (TaskNode n : allNodes) //####[374]####
        {//####[374]####
            all.add(n.name);//####[375]####
        }//####[376]####
        for (TaskNode n : usedNodes) //####[378]####
        {//####[378]####
            used.add(n.name);//####[379]####
        }//####[380]####
        all.removeAll(used);//####[382]####
        unused = (ArrayList<String>) all.clone();//####[383]####
        for (TaskNode n : allNodes) //####[386]####
        {//####[386]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(n);//####[387]####
            for (DefaultEdge e : incomingEdges) //####[388]####
            {//####[388]####
                TaskNode edgeNode = graph.getEdgeSource(e);//####[389]####
                if (unused.contains(edgeNode.name)) //####[390]####
                {//####[390]####
                    all.remove(n.name);//####[391]####
                }//####[392]####
            }//####[393]####
        }//####[394]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[396]####
        for (TaskNode n : allNodes) //####[397]####
        {//####[397]####
            if (all.contains(n.name)) //####[398]####
            {//####[398]####
                freeNodes.add(n);//####[399]####
            }//####[400]####
        }//####[401]####
        return freeNodes;//####[403]####
    }//####[404]####
//####[407]####
    public boolean isComplete(StateWeights stateWeight) {//####[407]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[408]####
        ArrayList<String> used = new ArrayList<String>();//####[409]####
        ArrayList<String> all = new ArrayList<String>();//####[410]####
        for (TaskNode n : usedNodes) //####[412]####
        {//####[412]####
            used.add(n.name);//####[413]####
        }//####[414]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[415]####
        for (TaskNode n : allNodes) //####[417]####
        {//####[417]####
            all.add(n.name);//####[418]####
        }//####[419]####
        all.removeAll(used);//####[421]####
        if (all.isEmpty()) //####[422]####
        {//####[422]####
            return true;//####[423]####
        } else {//####[424]####
            return false;//####[425]####
        }//####[426]####
    }//####[427]####
}//####[427]####
