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
        while (!openQueue.isEmpty()) //####[76]####
        {//####[76]####
            StateWeights stateWeight = openQueue.poll();//####[79]####
            if (isComplete(stateWeight)) //####[80]####
            {//####[80]####
                threadPathList.add(stateWeight.getState());//####[82]####
                System.out.println("cheeky");//####[83]####
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
                if (n.finishTime > finishTimeOfPath) //####[106]####
                {//####[106]####
                    finishTimeOfPath = n.finishTime;//####[107]####
                }//####[108]####
            }//####[109]####
            if (finishTimeOfPath < smallestFinPath) //####[111]####
            {//####[111]####
                smallestFinPath = finishTimeOfPath;//####[112]####
                optimalPath = p;//####[113]####
            }//####[114]####
        }//####[116]####
        return optimalPath;//####[117]####
    }//####[118]####
//####[121]####
    private void setScheduleOnGraph(Path state) {//####[121]####
        Set<TaskNode> graphNodes = graph.vertexSet();//####[122]####
        for (TaskNode n : state.getPath()) //####[125]####
        {//####[125]####
            for (TaskNode g : graphNodes) //####[126]####
            {//####[126]####
                if (n.name.equals(g.name)) //####[127]####
                {//####[127]####
                    g.setProc(n.allocProc);//####[128]####
                    g.setStart(n.startTime);//####[129]####
                }//####[130]####
            }//####[131]####
        }//####[132]####
    }//####[133]####
//####[138]####
    private void expandState(StateWeights stateWeight, int processors) {//####[138]####
        Path current = stateWeight.state;//####[139]####
        ArrayList<TaskNode> freeNodes = freeNodes(stateWeight);//####[141]####
        for (TaskNode n : freeNodes) //####[143]####
        {//####[143]####
            for (int i = 1; i <= processors; i++) //####[144]####
            {//####[144]####
                TaskNode newNode = new TaskNode(n);//####[146]####
                newNode.setProc(i);//####[147]####
                setNodeTimes(current, newNode, i);//####[148]####
                Path temp = new Path(current, newNode);//####[149]####
                double pathWeight = heuristicCost(temp, stateWeight);//####[150]####
                if (!openQueue.contains(pathWeight) && !closedQueue.contains(pathWeight)) //####[151]####
                {//####[151]####
                    openQueue.add(new StateWeights(temp, pathWeight));//####[152]####
                }//####[153]####
            }//####[162]####
        }//####[163]####
    }//####[164]####
//####[167]####
    public void setNodeTimes(Path current, TaskNode newNode, int processor) {//####[167]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[168]####
        TaskNode graphNode = newNode;//####[169]####
        for (TaskNode n : allNodes) //####[170]####
        {//####[170]####
            if (n.name == newNode.name) //####[171]####
            {//####[171]####
                graphNode = n;//####[172]####
            }//####[173]####
        }//####[174]####
        Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(graphNode);//####[176]####
        int processorEndTime = latestEndTimeOnProcessor(current, processor);//####[178]####
        int parentEndTime = 0;//####[179]####
        int parentProcessor = processor;//####[180]####
        int latestAllowedTime;//####[181]####
        int t = 0;//####[182]####
        if (incomingEdges.isEmpty()) //####[185]####
        {//####[185]####
            newNode.setStart(processorEndTime);//####[186]####
        } else for (DefaultEdge e : incomingEdges) //####[188]####
        {//####[188]####
            int communicationTime = (int) graph.getEdgeWeight(e);//####[189]####
            TaskNode parentNode = graph.getEdgeSource(e);//####[193]####
            ArrayList<TaskNode> setOfNodesInPath = current.getPath();//####[194]####
            for (TaskNode n : setOfNodesInPath) //####[197]####
            {//####[197]####
                if (n.name.equals(parentNode.name)) //####[198]####
                {//####[198]####
                    parentEndTime = n.finishTime;//####[199]####
                    parentProcessor = n.allocProc;//####[200]####
                }//####[201]####
            }//####[202]####
            if (parentProcessor != processor) //####[204]####
            {//####[204]####
                latestAllowedTime = parentEndTime + communicationTime;//####[205]####
            } else {//####[206]####
                latestAllowedTime = parentEndTime;//####[207]####
            }//####[208]####
            if (latestAllowedTime > t) //####[211]####
            {//####[211]####
                t = latestAllowedTime;//####[212]####
            }//####[213]####
        }//####[214]####
        if (t > processorEndTime) //####[217]####
        {//####[217]####
            newNode.setStart(t);//####[218]####
        } else {//####[219]####
            newNode.setStart(processorEndTime);//####[220]####
        }//####[221]####
        newNode.setFinish(newNode.weight + newNode.startTime);//####[224]####
    }//####[225]####
//####[228]####
    private static int latestEndTimeOnProcessor(Path current, int processor) {//####[228]####
        ArrayList<TaskNode> path = current.getPath();//####[229]####
        int currentFinishTime = 0;//####[230]####
        for (TaskNode n : path) //####[231]####
        {//####[231]####
            if (n.allocProc == processor) //####[232]####
            {//####[232]####
                if (n.finishTime > currentFinishTime) //####[233]####
                {//####[233]####
                    currentFinishTime = n.finishTime;//####[234]####
                }//####[235]####
            }//####[236]####
        }//####[237]####
        return currentFinishTime;//####[238]####
    }//####[239]####
//####[243]####
    public double heuristicCost(Path state, StateWeights stateWeight) {//####[243]####
        int maxTime = 0;//####[244]####
        int startTime = 0;//####[245]####
        TaskNode maxNode = new TaskNode();//####[246]####
        int bottomLevel = 0;//####[247]####
        double newPathWeight = 0;//####[248]####
        double idleTime = 0;//####[249]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[250]####
        ArrayList<TaskNode> path = state.getPath();//####[251]####
        double previousPathWeight = stateWeight.pathWeight;//####[252]####
        for (TaskNode n : path) //####[254]####
        {//####[254]####
            if (n.finishTime >= maxTime) //####[255]####
            {//####[255]####
                maxTime = n.finishTime;//####[256]####
                maxNode = n;//####[257]####
            }//####[258]####
        }//####[259]####
        TaskNode graphNode = maxNode;//####[261]####
        for (TaskNode n : allNodes) //####[262]####
        {//####[262]####
            if (n.name == maxNode.name) //####[263]####
            {//####[263]####
                graphNode = n;//####[264]####
            }//####[265]####
        }//####[266]####
        bottomLevel = ComputationalBottomLevel(graphNode);//####[268]####
        startTime = maxNode.startTime;//####[271]####
        idleTime = getIdleTime(state, graphNode, stateWeight);//####[274]####
        newPathWeight = (double) startTime + (double) (bottomLevel + idleTime);//####[277]####
        if (newPathWeight > previousPathWeight) //####[280]####
        {//####[280]####
            return newPathWeight;//####[281]####
        } else {//####[282]####
            return previousPathWeight;//####[283]####
        }//####[284]####
    }//####[285]####
//####[288]####
    private int ComputationalBottomLevel(TaskNode node) {//####[288]####
        int bottomLevel = 0;//####[289]####
        Set<DefaultEdge> outgoingEdges = graph.outgoingEdgesOf(node);//####[291]####
        if (outgoingEdges.isEmpty()) //####[293]####
        {//####[293]####
            return node.weight;//####[294]####
        } else for (DefaultEdge e : outgoingEdges) //####[296]####
        {//####[296]####
            TaskNode successor = graph.getEdgeTarget(e);//####[297]####
            int temp = ComputationalBottomLevel(successor);//####[298]####
            if (temp > bottomLevel) //####[300]####
            {//####[300]####
                bottomLevel = temp;//####[301]####
            }//####[302]####
        }//####[303]####
        return (node.weight + bottomLevel);//####[304]####
    }//####[305]####
//####[307]####
    private double getIdleTime(Path state, TaskNode currentNode, StateWeights stateWeight) {//####[307]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[309]####
        ArrayList<TaskNode> parents = new ArrayList<TaskNode>();//####[310]####
        freeNodes = freeNodes(stateWeight);//####[311]####
        double earliestStartTime = Double.MAX_VALUE;//####[312]####
        double criticalParentFinTime = 0;//####[313]####
        ArrayList<Double> idleTime = new ArrayList<Double>();//####[314]####
        double dataReadyTime = 0;//####[315]####
        double nodeIdleTime = 0;//####[316]####
        for (TaskNode f : freeNodes) //####[320]####
        {//####[320]####
            parents.clear();//####[322]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(f);//####[323]####
            for (DefaultEdge incomingEdge : incomingEdges) //####[324]####
            {//####[324]####
                parents.add(graph.getEdgeSource(incomingEdge));//####[325]####
            }//####[326]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[327]####
            {//####[327]####
                for (TaskNode parent : parents) //####[329]####
                {//####[329]####
                    if (parent.allocProc == i) //####[330]####
                    {//####[330]####
                        dataReadyTime = parent.finishTime;//####[331]####
                    } else {//####[332]####
                        DefaultEdge edge = graph.getEdge(parent, f);//####[335]####
                        dataReadyTime = parent.finishTime + graph.getEdgeWeight(edge);//####[337]####
                    }//####[338]####
                    if (dataReadyTime > criticalParentFinTime) //####[339]####
                    {//####[339]####
                        criticalParentFinTime = dataReadyTime;//####[340]####
                    }//####[341]####
                }//####[342]####
                if (criticalParentFinTime < earliestStartTime) //####[343]####
                {//####[343]####
                    earliestStartTime = criticalParentFinTime;//####[344]####
                }//####[345]####
            }//####[346]####
            for (int i = 0; i < options.getNumProcessors(); i++) //####[347]####
            {//####[347]####
                nodeIdleTime += earliestStartTime - latestEndTimeOnProcessor(state, i);//####[348]####
            }//####[349]####
            idleTime.add(nodeIdleTime);//####[350]####
        }//####[352]####
        return (Collections.max(idleTime)) / options.getNumProcessors();//####[354]####
    }//####[355]####
//####[360]####
    @SuppressWarnings("unchecked")//####[360]####
    private ArrayList<TaskNode> freeNodes(StateWeights stateWeight) {//####[360]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[362]####
        ArrayList<String> used = new ArrayList<String>();//####[363]####
        ArrayList<String> all = new ArrayList<String>();//####[364]####
        ArrayList<String> unused = new ArrayList<String>();//####[365]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[366]####
        for (TaskNode n : allNodes) //####[368]####
        {//####[368]####
            all.add(n.name);//####[369]####
        }//####[370]####
        for (TaskNode n : usedNodes) //####[372]####
        {//####[372]####
            used.add(n.name);//####[373]####
        }//####[374]####
        all.removeAll(used);//####[376]####
        unused = (ArrayList<String>) all.clone();//####[377]####
        for (TaskNode n : allNodes) //####[380]####
        {//####[380]####
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(n);//####[381]####
            for (DefaultEdge e : incomingEdges) //####[382]####
            {//####[382]####
                TaskNode edgeNode = graph.getEdgeSource(e);//####[383]####
                if (unused.contains(edgeNode.name)) //####[384]####
                {//####[384]####
                    all.remove(n.name);//####[385]####
                }//####[386]####
            }//####[387]####
        }//####[388]####
        ArrayList<TaskNode> freeNodes = new ArrayList<TaskNode>();//####[390]####
        for (TaskNode n : allNodes) //####[391]####
        {//####[391]####
            if (all.contains(n.name)) //####[392]####
            {//####[392]####
                freeNodes.add(n);//####[393]####
            }//####[394]####
        }//####[395]####
        return freeNodes;//####[397]####
    }//####[398]####
//####[401]####
    public boolean isComplete(StateWeights stateWeight) {//####[401]####
        ArrayList<TaskNode> usedNodes = stateWeight.state.getPath();//####[402]####
        ArrayList<String> used = new ArrayList<String>();//####[403]####
        ArrayList<String> all = new ArrayList<String>();//####[404]####
        for (TaskNode n : usedNodes) //####[406]####
        {//####[406]####
            used.add(n.name);//####[407]####
        }//####[408]####
        Set<TaskNode> allNodes = graph.vertexSet();//####[409]####
        for (TaskNode n : allNodes) //####[411]####
        {//####[411]####
            all.add(n.name);//####[412]####
        }//####[413]####
        all.removeAll(used);//####[415]####
        if (all.isEmpty()) //####[416]####
        {//####[416]####
            return true;//####[431]####
        } else {//####[432]####
            return false;//####[433]####
        }//####[434]####
    }//####[435]####
}//####[435]####
