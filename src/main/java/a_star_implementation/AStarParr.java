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
import processing_classes.Options;//####[20]####
import processing_classes.TaskNode;//####[21]####
import pt.runtime.CurrentTask;//####[22]####
import pt.runtime.ParaTask;//####[23]####
import pt.runtime.TaskID;//####[24]####
import pt.runtime.TaskIDGroup;//####[25]####
//####[25]####
//-- ParaTask related imports//####[25]####
import pt.runtime.*;//####[25]####
import java.util.concurrent.ExecutionException;//####[25]####
import java.util.concurrent.locks.*;//####[25]####
import java.lang.reflect.*;//####[25]####
import pt.runtime.GuiThread;//####[25]####
import java.util.concurrent.BlockingQueue;//####[25]####
import java.util.ArrayList;//####[25]####
import java.util.List;//####[25]####
//####[25]####
public class AStarParr extends AStarParent {//####[27]####
    static{ParaTask.init();}//####[27]####
    /*  ParaTask helper method to access private/protected slots *///####[27]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[27]####
        if (m.getParameterTypes().length == 0)//####[27]####
            m.invoke(instance);//####[27]####
        else if ((m.getParameterTypes().length == 1))//####[27]####
            m.invoke(instance, arg);//####[27]####
        else //####[27]####
            m.invoke(instance, arg, interResult);//####[27]####
    }//####[27]####
//####[29]####
    public AStarParr(DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph, Options options) {//####[29]####
        this.graph = graph;//####[30]####
        this.options = options;//####[31]####
    }//####[32]####
//####[34]####
    public AStarParr(DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph) {//####[34]####
        this.graph = graph;//####[35]####
    }//####[36]####
//####[38]####
    public void solveAstar() throws InterruptedException {//####[38]####
        TaskNode initialNode = new TaskNode();//####[40]####
        Path initialPath = new Path(initialNode);//####[41]####
        StateWeights initialSW = new StateWeights(initialPath, 0.0);//####[42]####
        openQueue.add(initialSW);//####[43]####
        TaskIDGroup taskGroup = new TaskIDGroup(options.getNumThreads());//####[46]####
        for (int i = 0; i < options.getNumThreads(); i++) //####[47]####
        {//####[47]####
            TaskID id = parallelSearch();//####[48]####
            taskGroup.add(id);//####[49]####
        }//####[50]####
        try {//####[52]####
            taskGroup.waitTillFinished();//####[53]####
        } catch (Exception e) {//####[54]####
            e.printStackTrace();//####[55]####
        }//####[56]####
        Path optimalPath = getSmallestPathFromList();//####[58]####
        setScheduleOnGraph(optimalPath);//####[59]####
    }//####[60]####
//####[62]####
    private static volatile Method __pt__parallelSearch__method = null;//####[62]####
    private synchronized static void __pt__parallelSearch__ensureMethodVarSet() {//####[62]####
        if (__pt__parallelSearch__method == null) {//####[62]####
            try {//####[62]####
                __pt__parallelSearch__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__parallelSearch", new Class[] {//####[62]####
                    //####[62]####
                });//####[62]####
            } catch (Exception e) {//####[62]####
                e.printStackTrace();//####[62]####
            }//####[62]####
        }//####[62]####
    }//####[62]####
    TaskIDGroup<Void> parallelSearch() {//####[62]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[62]####
        return parallelSearch(new TaskInfo());//####[62]####
    }//####[62]####
    TaskIDGroup<Void> parallelSearch(TaskInfo taskinfo) {//####[62]####
        // ensure Method variable is set//####[62]####
        if (__pt__parallelSearch__method == null) {//####[62]####
            __pt__parallelSearch__ensureMethodVarSet();//####[62]####
        }//####[62]####
        taskinfo.setParameters();//####[62]####
        taskinfo.setMethod(__pt__parallelSearch__method);//####[62]####
        taskinfo.setInstance(this);//####[62]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[62]####
    }//####[62]####
    public void __pt__parallelSearch() {//####[62]####
        while (!openQueue.isEmpty()) //####[63]####
        {//####[63]####
            if (threadPathList.size() > 0) //####[64]####
            {//####[64]####
                astarAlgo();//####[65]####
                break;//####[66]####
            }//####[67]####
            astarAlgo();//####[68]####
        }//####[70]####
    }//####[71]####
//####[71]####
//####[73]####
    private void astarAlgo() {//####[73]####
        StateWeights stateWeight = openQueue.poll();//####[75]####
        if (stateWeight == null) //####[76]####
        {//####[76]####
            TaskNode initialNode = new TaskNode();//####[77]####
            Path initialPath = new Path(initialNode);//####[78]####
            stateWeight = new StateWeights(initialPath, 0.0);//####[79]####
        }//####[80]####
        if (isComplete(stateWeight)) //####[81]####
        {//####[81]####
            threadPathList.add(stateWeight.getState());//####[83]####
            return;//####[84]####
        } else {//####[85]####
            expandState(stateWeight, options.getNumProcessors());//####[87]####
        }//####[88]####
        closedQueue.add(stateWeight);//####[89]####
    }//####[90]####
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
        }//####[112]####
        return optimalPath;//####[113]####
    }//####[114]####
}//####[114]####
