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
public class AStarParr extends AStarParent {//####[25]####
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
    public AStarParr(DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph, Options options) {//####[27]####
        this.graph = graph;//####[28]####
        this.options = options;//####[29]####
    }//####[30]####
//####[32]####
    public AStarParr(DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph) {//####[32]####
        this.graph = graph;//####[33]####
    }//####[34]####
//####[36]####
    public void solveAstar() throws InterruptedException {//####[36]####
        TaskNode initialNode = new TaskNode();//####[39]####
        Path initialPath = new Path(initialNode);//####[40]####
        StateWeights initialSW = new StateWeights(initialPath, 0.0);//####[41]####
        openQueue.add(initialSW);//####[42]####
        TaskIDGroup taskGroup = new TaskIDGroup(options.getNumThreads());//####[45]####
        for (int i = 0; i < options.getNumThreads(); i++) //####[46]####
        {//####[46]####
            TaskID id = parallelSearch();//####[47]####
            taskGroup.add(id);//####[48]####
        }//####[49]####
        try {//####[51]####
            taskGroup.waitTillFinished();//####[52]####
        } catch (Exception e) {//####[53]####
            e.printStackTrace();//####[54]####
        }//####[55]####
        Path optimalPath = getSmallestPathFromList();//####[57]####
        setScheduleOnGraph(optimalPath);//####[58]####
    }//####[59]####
//####[61]####
    private static volatile Method __pt__parallelSearch__method = null;//####[61]####
    private synchronized static void __pt__parallelSearch__ensureMethodVarSet() {//####[61]####
        if (__pt__parallelSearch__method == null) {//####[61]####
            try {//####[61]####
                __pt__parallelSearch__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__parallelSearch", new Class[] {//####[61]####
                    //####[61]####
                });//####[61]####
            } catch (Exception e) {//####[61]####
                e.printStackTrace();//####[61]####
            }//####[61]####
        }//####[61]####
    }//####[61]####
    TaskIDGroup<Void> parallelSearch() {//####[61]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[61]####
        return parallelSearch(new TaskInfo());//####[61]####
    }//####[61]####
    TaskIDGroup<Void> parallelSearch(TaskInfo taskinfo) {//####[61]####
        // ensure Method variable is set//####[61]####
        if (__pt__parallelSearch__method == null) {//####[61]####
            __pt__parallelSearch__ensureMethodVarSet();//####[61]####
        }//####[61]####
        taskinfo.setParameters();//####[61]####
        taskinfo.setMethod(__pt__parallelSearch__method);//####[61]####
        taskinfo.setInstance(this);//####[61]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[61]####
    }//####[61]####
    public void __pt__parallelSearch() {//####[61]####
        while (!openQueue.isEmpty()) //####[62]####
        {//####[62]####
            StateWeights stateWeight = openQueue.poll();//####[65]####
            if (stateWeight == null) //####[66]####
            {//####[66]####
                TaskNode initialNode = new TaskNode();//####[67]####
                Path initialPath = new Path(initialNode);//####[68]####
                stateWeight = new StateWeights(initialPath, 0.0);//####[69]####
            }//####[70]####
            if (isComplete(stateWeight)) //####[71]####
            {//####[71]####
                threadPathList.add(stateWeight.getState());//####[73]####
                break;//####[74]####
            } else {//####[75]####
                expandState(stateWeight, options.getNumProcessors());//####[77]####
            }//####[78]####
            closedQueue.add(stateWeight);//####[79]####
        }//####[80]####
    }//####[81]####
//####[81]####
//####[84]####
    private Path getSmallestPathFromList() {//####[84]####
        int smallestFinPath = Integer.MAX_VALUE;//####[86]####
        int finishTimeOfPath = 0;//####[87]####
        Path optimalPath = null;//####[88]####
        for (Path p : threadPathList) //####[90]####
        {//####[90]####
            finishTimeOfPath = 0;//####[91]####
            for (TaskNode n : p.getPath()) //####[93]####
            {//####[93]####
                if (n.finishTime > finishTimeOfPath) //####[94]####
                {//####[94]####
                    finishTimeOfPath = n.finishTime;//####[95]####
                }//####[96]####
            }//####[97]####
            if (finishTimeOfPath < smallestFinPath) //####[99]####
            {//####[99]####
                smallestFinPath = finishTimeOfPath;//####[100]####
                optimalPath = p;//####[101]####
            }//####[102]####
        }//####[103]####
        return optimalPath;//####[104]####
    }//####[105]####
}//####[105]####
