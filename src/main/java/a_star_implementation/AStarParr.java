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
        long begin = System.currentTimeMillis();//####[39]####
        TaskNode initialNode = new TaskNode();//####[41]####
        Path initialPath = new Path(initialNode);//####[42]####
        StateWeights initialSW = new StateWeights(initialPath, 0.0);//####[43]####
        openQueue.add(initialSW);//####[44]####
        TaskIDGroup taskGroup = new TaskIDGroup(options.getNumThreads());//####[47]####
        for (int i = 0; i < options.getNumThreads(); i++) //####[48]####
        {//####[48]####
            TaskID id = parallelSearch();//####[49]####
            taskGroup.add(id);//####[50]####
        }//####[51]####
        try {//####[53]####
            taskGroup.waitTillFinished();//####[54]####
        } catch (Exception e) {//####[55]####
            e.printStackTrace();//####[56]####
        }//####[57]####
        Path optimalPath = getSmallestPathFromList();//####[59]####
        setScheduleOnGraph(optimalPath);//####[60]####
        long end = System.currentTimeMillis();//####[61]####
        System.out.println("Time: " + (end - begin) + "ms");//####[63]####
    }//####[64]####
//####[66]####
    private static volatile Method __pt__parallelSearch__method = null;//####[66]####
    private synchronized static void __pt__parallelSearch__ensureMethodVarSet() {//####[66]####
        if (__pt__parallelSearch__method == null) {//####[66]####
            try {//####[66]####
                __pt__parallelSearch__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__parallelSearch", new Class[] {//####[66]####
                    //####[66]####
                });//####[66]####
            } catch (Exception e) {//####[66]####
                e.printStackTrace();//####[66]####
            }//####[66]####
        }//####[66]####
    }//####[66]####
    TaskIDGroup<Void> parallelSearch() {//####[66]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[66]####
        return parallelSearch(new TaskInfo());//####[66]####
    }//####[66]####
    TaskIDGroup<Void> parallelSearch(TaskInfo taskinfo) {//####[66]####
        // ensure Method variable is set//####[66]####
        if (__pt__parallelSearch__method == null) {//####[66]####
            __pt__parallelSearch__ensureMethodVarSet();//####[66]####
        }//####[66]####
        taskinfo.setParameters();//####[66]####
        taskinfo.setMethod(__pt__parallelSearch__method);//####[66]####
        taskinfo.setInstance(this);//####[66]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[66]####
    }//####[66]####
    public void __pt__parallelSearch() {//####[66]####
        while (!openQueue.isEmpty()) //####[67]####
        {//####[67]####
            if (threadPathList.size() > 0) //####[68]####
            {//####[68]####
                astarAlgo();//####[69]####
                break;//####[70]####
            }//####[71]####
            astarAlgo();//####[72]####
        }//####[74]####
    }//####[75]####
//####[75]####
//####[77]####
    private void astarAlgo() {//####[77]####
        StateWeights stateWeight = openQueue.poll();//####[79]####
        if (stateWeight == null) //####[80]####
        {//####[80]####
            TaskNode initialNode = new TaskNode();//####[81]####
            Path initialPath = new Path(initialNode);//####[82]####
            stateWeight = new StateWeights(initialPath, 0.0);//####[83]####
        }//####[84]####
        if (isComplete(stateWeight)) //####[85]####
        {//####[85]####
            threadPathList.add(stateWeight.getState());//####[87]####
            return;//####[88]####
        } else {//####[89]####
            expandState(stateWeight, options.getNumProcessors());//####[91]####
        }//####[92]####
        closedQueue.add(stateWeight);//####[93]####
    }//####[94]####
//####[97]####
    private Path getSmallestPathFromList() {//####[97]####
        int smallestFinPath = Integer.MAX_VALUE;//####[99]####
        int finishTimeOfPath = 0;//####[100]####
        Path optimalPath = null;//####[101]####
        for (Path p : threadPathList) //####[103]####
        {//####[103]####
            finishTimeOfPath = 0;//####[104]####
            for (TaskNode n : p.getPath()) //####[106]####
            {//####[106]####
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
        }//####[116]####
        return optimalPath;//####[117]####
    }//####[118]####
}//####[118]####
