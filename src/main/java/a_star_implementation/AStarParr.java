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
                boolean add = checkIfPathExists(temp, pathWeight);//####[147]####
                if (add) //####[149]####
                {//####[149]####
                    openQueue.add(new StateWeights(temp, pathWeight));//####[150]####
                }//####[151]####
            }//####[152]####
            newStates.clear();//####[153]####
        }//####[154]####
    }//####[155]####
//####[159]####
    public boolean checkIfPathExists(Path temp, double pathWeight) {//####[159]####
        if (openQueue.isEmpty()) //####[161]####
        {//####[161]####
            return true;//####[162]####
        }//####[163]####
        ArrayList<StateWeights> similarSchedules = new ArrayList<StateWeights>();//####[165]####
        Set<Set> tempProcSet = new HashSet<Set>();//####[166]####
        boolean remove = true;//####[167]####
        for (int j = 0; j < options.getNumProcessors(); j++) //####[170]####
        {//####[170]####
            Set<TaskNode> nodes = new HashSet<TaskNode>();//####[171]####
            Path tempPath = new Path(temp.getPath());//####[172]####
            for (TaskNode tempNode : tempPath.getPath()) //####[173]####
            {//####[173]####
                if (tempNode.allocProc == j) //####[174]####
                {//####[174]####
                    TaskNode copyNode = new TaskNode(tempNode);//####[175]####
                    copyNode.setProc(1);//####[176]####
                    nodes.add(copyNode);//####[177]####
                }//####[178]####
            }//####[179]####
            tempProcSet.add(nodes);//####[180]####
        }//####[181]####
        Iterator<StateWeights> itr = openQueue.iterator();//####[183]####
        while (itr.hasNext()) //####[184]####
        {//####[184]####
            StateWeights schedule = (StateWeights) itr.next();//####[185]####
            if (schedule.pathWeight == pathWeight && (schedule.state.getPath().size() == temp.getPath().size())) //####[186]####
            {//####[188]####
                similarSchedules.add(schedule);//####[189]####
            }//####[190]####
        }//####[191]####
        for (int k = 0; k < similarSchedules.size(); k++) //####[193]####
        {//####[193]####
            Set<Set> currentSet = new HashSet<Set>();//####[194]####
            for (int j = 0; j < options.getNumProcessors(); j++) //####[195]####
            {//####[195]####
                Set<TaskNode> nodes = new HashSet<TaskNode>();//####[196]####
                Path tempPath = new Path(similarSchedules.get(k).state.getPath());//####[197]####
                for (TaskNode tempNode : tempPath.getPath()) //####[199]####
                {//####[199]####
                    if (tempNode.allocProc == j) //####[200]####
                    {//####[200]####
                        TaskNode copyNode = new TaskNode(tempNode);//####[201]####
                        copyNode.setProc(1);//####[202]####
                        nodes.add(copyNode);//####[203]####
                    }//####[204]####
                }//####[205]####
                currentSet.add(nodes);//####[206]####
            }//####[207]####
            if (tempProcSet.containsAll(currentSet) && currentSet.containsAll(tempProcSet)) //####[242]####
            {//####[243]####
                return false;//####[244]####
            }//####[245]####
        }//####[246]####
        return true;//####[248]####
    }//####[249]####
//####[251]####
    public boolean removePathDuplicates(StateWeights newState) {//####[251]####
        Iterator<StateWeights> itrO = openQueue.iterator();//####[252]####
        Iterator<StateWeights> itrC = closedQueue.iterator();//####[253]####
        ArrayList<TaskNode> newPath = newState.state.getPath();//####[254]####
        while (itrO.hasNext()) //####[256]####
        {//####[256]####
            StateWeights temp = itrO.next();//####[257]####
            ArrayList<TaskNode> path = temp.state.getPath();//####[258]####
            if (path.containsAll(newPath)) //####[259]####
            {//####[259]####
                return false;//####[260]####
            }//####[261]####
        }//####[262]####
        return true;//####[272]####
    }//####[274]####
//####[276]####
    public boolean removeCurrentNodeDuplicates(StateWeights newState) {//####[276]####
        Iterator<StateWeights> itr = newStates.iterator();//####[277]####
        TaskNode newNode = newState.state.getCurrent();//####[278]####
        while (itr.hasNext()) //####[280]####
        {//####[280]####
            StateWeights temp = itr.next();//####[281]####
            TaskNode tempNode = temp.state.getCurrent();//####[282]####
            if (newNode.startTime == tempNode.startTime) //####[283]####
            {//####[283]####
                if (newNode.name == tempNode.name) //####[284]####
                {//####[284]####
                    return false;//####[285]####
                }//####[286]####
            }//####[287]####
        }//####[288]####
        newStates.add(newState);//####[290]####
        return true;//####[291]####
    }//####[293]####
//####[296]####
    public void setNodeTimes(Path current, TaskNode newNode, int processor) {//####[296]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[297]####
        TaskNode graphNode = newNode;//####[298]####
        for (TaskNode n : allNodes) //####[299]####
        {//####[299]####
            if (n.name == newNode.name) //####[300]####
            {//####[300]####
                graphNode = n;//####[301]####
            }//####[302]####
        }//####[303]####
        Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(graphNode);//####[305]####
        int processorEndTime = latestEndTimeOnProcessor(current, processor);//####[307]####
        int parentEndTime = 0;//####[308]####
        int parentProcessor = processor;//####[309]####
        int latestAllowedTime;//####[310]####
        int t = 0;//####[311]####
        if (incomingEdges.isEmpty()) //####[314]####
        {//####[314]####
            newNode.setStart(processorEndTime);//####[315]####
        } else for (DefaultEdge e : incomingEdges) //####[317]####
        {//####[317]####
            int communicationTime = (int) graph.getEdgeWeight(e);//####[318]####
            TaskNode parentNode = graph.getEdgeSource(e);//####[322]####
            ArrayList<TaskNode> setOfNodesInPath = current.getPath();//####[323]####
            for (TaskNode n : setOfNodesInPath) //####[326]####
            {//####[326]####
                if (n.name.equals(parentNode.name)) //####[327]####
                {//####[327]####
                    parentEndTime = n.finishTime;//####[328]####
                    parentProcessor = n.allocProc;//####[329]####
                }//####[330]####
            }//####[331]####
            if (parentProcessor != processor) //####[333]####
            {//####[333]####
                latestAllowedTime = parentEndTime + communicationTime;//####[334]####
            } else {//####[335]####
                latestAllowedTime = parentEndTime;//####[336]####
            }//####[337]####
            if (latestAllowedTime > t) //####[340]####
            {//####[340]####
                t = latestAllowedTime;//####[341]####
            }//####[342]####
        }//####[343]####
        if (t > processorEndTime) //####[346]####
        {//####[346]####
            newNode.setStart(t);//####[347]####
        } else {//####[348]####
            newNode.setStart(processorEndTime);//####[349]####
        }//####[350]####
        newNode.setFinish(newNode.weight + newNode.startTime);//####[353]####
    }//####[354]####
//####[357]####
    private static int latestEndTimeOnProcessor(Path current, int processor) {//####[357]####
        ArrayList<TaskNode> path = current.getPath();//####[358]####
        int currentFinishTime = 0;//####[359]####
        for (TaskNode n : path) //####[360]####
        {//####[360]####
            if (n.allocProc == processor) //####[361]####
            {//####[361]####
                if (n.finishTime > currentFinishTime) //####[362]####
                {//####[362]####
                    currentFinishTime = n.finishTime;//####[363]####
                }//####[364]####
            }//####[365]####
        }//####[366]####
        return currentFinishTime;//####[367]####
    }//####[368]####
//####[372]####
    public double heuristicCost(Path state, StateWeights stateWeight) {//####[372]####
        int maxTime = 0;//####[373]####
        int startTime = 0;//####[374]####
        TaskNode maxNode = new TaskNode();//####[375]####
        int bottomLevel = 0;//####[376]####
        double newPathWeight = 0;//####[377]####
        double idleTime = 0;//####[378]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[379]####
        ArrayList<TaskNode> path = state.getPath();//####[380]####
        double previousPathWeight = stateWeight.pathWeight;//####[381]####
        for (TaskNode n : path) //####[383]####
        {//####[383]####
            if (n.finishTime >= maxTime) //####[384]####
            {//####[384]####
                maxTime = n.finishTime;//####[385]####
                maxNode = n;//####[386]####
            }//####[387]####
        }//####[388]####
        TaskNode graphNode = maxNode;//####[390]####
        for (TaskNode n : allNodes) //####[391]####
        {//####[391]####
            if (n.name == maxNode.name) //####[392]####
            {//####[392]####
                graphNode = n;//####[393]####
            }//####[394]####
        }//####[395]####
        bottomLevel = ComputationalBottomLevel(graphNode);//####[397]####
        startTime = maxNode.startTime;//####[399]####
        idleTime = getIdleTime(state, graphNode, stateWeight);//####[401]####
        newPathWeight = (double) startTime + (double) (bottomLevel + idleTime);//####[404]####
        if (newPathWeight > previousPathWeight) //####[407]####
        {//####[407]####
            return newPathWeight;//####[408]####
        } else {//####[409]####
            return previousPathWeight;//####[410]####
        }//####[411]####
    }//####[412]####
//####[415]####
    private int ComputationalBottomLevel(TaskNode node) {//####[415]####
        int bottomLevel = 0;//####[416]####
        Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(node);//####[418]####
        if (outgoingEdges.isEmpty()) //####[420]####
        {//####[420]####
            return node.weight;//####[421]####
        } else for (DefaultEdge e : outgoingEdges) //####[425]####
        {//####[425]####
            TaskNode successor = graph.getEdgeTarget(e);//####[426]####
            int temp = ComputationalBottomLevel(successor);//####[427]####
            if (temp > bottomLevel) //####[429]####
            {//####[429]####
                bottomLevel = temp;//####[430]####
            }//####[431]####
        }//####[432]####
        return (node.weight + bottomLevel);//####[433]####
    }//####[434]####
//####[437]####
    private double addToIdleTime(Path state, TaskNode nodeAdded) {//####[437]####
        int lastTimeOnProc = latestEndTimeOnProcessor(state, nodeAdded.allocProc);//####[438]####
        int idleTimeToAdd = nodeAdded.startTime - lastTimeOnProc;//####[439]####
        return idleTimeToAdd;//####[440]####
    }//####[441]####
//####[443]####
    private double getIdleTime(Path state, TaskNode currentNode, StateWeights stateWeight) {//####[443]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[445]####
        ArrayList<TaskNode> parents = new ArrayList<TaskNode>();//####[446]####
        freeNodes = freeNodes(stateWeight);//####[447]####
        double earliestStartTime = Double.MAX_VALUE;//####[448]####
        double criticalParentFinTime = 0;//####[449]####
        ArrayList<Double> idleTime = new ArrayList<Double>();//####[450]####
        double dataReadyTime = 0;//####[451]####
        double nodeIdleTime = 0;//####[452]####
        for (TaskNode f : freeNodes) //####[456]####
        {//####[456]####
            parents.clear();//####[457]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(f);//####[458]####
            for (DefaultEdge incomingEdge : incomingEdges) //####[459]####
            {//####[459]####
                parents.add(graph.getEdgeSource(incomingEdge));//####[460]####
            }//####[461]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[462]####
            {//####[462]####
                for (TaskNode parent : parents) //####[463]####
                {//####[463]####
                    if (parent.allocProc == i) //####[464]####
                    {//####[464]####
                        dataReadyTime = latestEndTimeOnProcessor(state, i);//####[465]####
                    } else {//####[466]####
                        DefaultEdge edge = graph.getEdge(parent, f);//####[467]####
                        dataReadyTime = Math.max((parent.finishTime + graph.getEdgeWeight(edge)), latestEndTimeOnProcessor(state, i));//####[468]####
                    }//####[470]####
                    if (dataReadyTime > criticalParentFinTime) //####[471]####
                    {//####[471]####
                        criticalParentFinTime = dataReadyTime;//####[472]####
                    }//####[473]####
                }//####[474]####
                if (criticalParentFinTime < earliestStartTime) //####[475]####
                {//####[475]####
                    earliestStartTime = criticalParentFinTime;//####[476]####
                }//####[477]####
            }//####[478]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[479]####
            {//####[479]####
                double temp = earliestStartTime - latestEndTimeOnProcessor(state, i);//####[480]####
                if (temp > 0) //####[481]####
                {//####[481]####
                    nodeIdleTime += temp;//####[482]####
                }//####[483]####
            }//####[484]####
            idleTime.add(nodeIdleTime);//####[485]####
        }//####[486]####
        return (Collections.min(idleTime)) / options.getNumProcessors();//####[487]####
    }//####[488]####
//####[492]####
    @SuppressWarnings("unchecked")//####[492]####
    private ArrayList<TaskNode> freeNodes(StateWeights stateWeight) {//####[492]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[494]####
        ArrayList<String> used = new ArrayList<String>();//####[495]####
        ArrayList<String> all = new ArrayList<String>();//####[496]####
        ArrayList<String> unused = new ArrayList<String>();//####[497]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[498]####
        for (TaskNode n : allNodes) //####[500]####
        {//####[500]####
            all.add(n.name);//####[501]####
        }//####[502]####
        for (TaskNode n : usedNodes) //####[504]####
        {//####[504]####
            used.add(n.name);//####[505]####
        }//####[506]####
        all.removeAll(used);//####[508]####
        unused = (ArrayList<String>) all.clone();//####[509]####
        for (TaskNode n : allNodes) //####[512]####
        {//####[512]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(n);//####[513]####
            for (DefaultEdge e : incomingEdges) //####[514]####
            {//####[514]####
                TaskNode edgeNode = graph.getEdgeSource(e);//####[515]####
                if (unused.contains(edgeNode.name)) //####[516]####
                {//####[516]####
                    all.remove(n.name);//####[517]####
                }//####[518]####
            }//####[519]####
        }//####[520]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[522]####
        for (TaskNode n : allNodes) //####[523]####
        {//####[523]####
            if (all.contains(n.name)) //####[524]####
            {//####[524]####
                freeNodes.add(n);//####[525]####
            }//####[526]####
        }//####[527]####
        return freeNodes;//####[529]####
    }//####[530]####
//####[533]####
    public boolean isComplete(StateWeights stateWeight) {//####[533]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[534]####
        ArrayList<String> used = new ArrayList<String>();//####[535]####
        ArrayList<String> all = new ArrayList<String>();//####[536]####
        for (TaskNode n : usedNodes) //####[538]####
        {//####[538]####
            used.add(n.name);//####[539]####
        }//####[540]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[541]####
        for (TaskNode n : allNodes) //####[543]####
        {//####[543]####
            all.add(n.name);//####[544]####
        }//####[545]####
        all.removeAll(used);//####[547]####
        if (all.isEmpty()) //####[548]####
        {//####[548]####
            return true;//####[563]####
        } else {//####[564]####
            return false;//####[565]####
        }//####[566]####
    }//####[567]####
}//####[567]####
