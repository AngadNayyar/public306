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
public class AStarParrVis extends AStarParent {//####[21]####
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
    public AStarParrVis(DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph, Options options, VisualisationGraph visualGraphObj) {//####[23]####
        this.graph = graph;//####[24]####
        this.options = options;//####[25]####
        this.visualGraphObj = visualGraphObj;//####[26]####
    }//####[27]####
//####[29]####
    public AStarParrVis(DefaultDirectedWeightedGraph<TaskNode, DefaultEdge> graph) {//####[29]####
        this.graph = graph;//####[30]####
    }//####[31]####
//####[33]####
    public void solveAstar() throws InterruptedException {//####[33]####
        long startTime = System.currentTimeMillis();//####[35]####
        long counter = 500;//####[36]####
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");//####[39]####
        TaskNode initialNode = new TaskNode();//####[42]####
        Path initialPath = new Path(initialNode);//####[43]####
        StateWeights initialSW = new StateWeights(initialPath, 0.0);//####[44]####
        openQueue.add(initialSW);//####[45]####
        TaskIDGroup taskGroup = new TaskIDGroup(options.getNumThreads());//####[48]####
        for (int i = 0; i < options.getNumThreads(); i++) //####[49]####
        {//####[49]####
            TaskID id = parallelSearch();//####[50]####
            taskGroup.add(id);//####[51]####
        }//####[52]####
        try {//####[54]####
            taskGroup.waitTillFinished();//####[55]####
        } catch (Exception e) {//####[56]####
            e.printStackTrace();//####[57]####
        }//####[58]####
        Path optimalPath = getSmallestPathFromList();//####[60]####
        setScheduleOnGraph(optimalPath);//####[61]####
        Thread.sleep(Math.max(counter, 0));//####[62]####
        counter -= 10;//####[63]####
        StateWeights o = new StateWeights(optimalPath, 0.0);//####[64]####
        visualGraphObj.update(o, options);//####[65]####
    }//####[66]####
//####[68]####
    private static volatile Method __pt__parallelSearch__method = null;//####[68]####
    private synchronized static void __pt__parallelSearch__ensureMethodVarSet() {//####[68]####
        if (__pt__parallelSearch__method == null) {//####[68]####
            try {//####[68]####
                __pt__parallelSearch__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__parallelSearch", new Class[] {//####[68]####
                    //####[68]####
                });//####[68]####
            } catch (Exception e) {//####[68]####
                e.printStackTrace();//####[68]####
            }//####[68]####
        }//####[68]####
    }//####[68]####
    TaskIDGroup<Void> parallelSearch() throws InterruptedException {//####[68]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[68]####
        return parallelSearch(new TaskInfo());//####[68]####
    }//####[68]####
    TaskIDGroup<Void> parallelSearch(TaskInfo taskinfo) throws InterruptedException {//####[68]####
        // ensure Method variable is set//####[68]####
        if (__pt__parallelSearch__method == null) {//####[68]####
            __pt__parallelSearch__ensureMethodVarSet();//####[68]####
        }//####[68]####
        taskinfo.setParameters();//####[68]####
        taskinfo.setMethod(__pt__parallelSearch__method);//####[68]####
        taskinfo.setInstance(this);//####[68]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[68]####
    }//####[68]####
    public void __pt__parallelSearch() throws InterruptedException {//####[68]####
        long startTime = System.currentTimeMillis();//####[69]####
        long counter = 50;//####[70]####
        while (!openQueue.isEmpty()) //####[71]####
        {//####[71]####
            StateWeights stateWeight = openQueue.poll();//####[74]####
            if (stateWeight == null) //####[75]####
            {//####[75]####
                TaskNode initialNode = new TaskNode();//####[76]####
                Path initialPath = new Path(initialNode);//####[77]####
                stateWeight = new StateWeights(initialPath, 0.0);//####[78]####
            }//####[79]####
            visualGraphObj.update(stateWeight, options);//####[80]####
            if (isComplete(stateWeight)) //####[81]####
            {//####[81]####
                threadPathList.add(stateWeight.getState());//####[83]####
                Thread.sleep(Math.max(counter, 0));//####[84]####
                counter -= 1;//####[85]####
                visualGraphObj.update(stateWeight, options);//####[86]####
                break;//####[87]####
            } else {//####[88]####
                visualGraphObj.updateNode(stateWeight.state.getCurrent());//####[90]####
                expandState(stateWeight, options.getNumProcessors());//####[91]####
                Thread.sleep(Math.max(counter, 0));//####[92]####
                counter -= 1;//####[93]####
                visualGraphObj.update(stateWeight, options);//####[94]####
            }//####[95]####
            closedQueue.add(stateWeight);//####[96]####
        }//####[97]####
    }//####[98]####
//####[98]####
//####[101]####
    private Path getSmallestPathFromList() {//####[101]####
        int smallestFinPath = Integer.MAX_VALUE;//####[103]####
        int finishTimeOfPath = 0;//####[104]####
        Path optimalPath = null;//####[105]####
        for (Path p : threadPathList) //####[107]####
        {//####[107]####
            finishTimeOfPath = 0;//####[108]####
            for (TaskNode n : p.getPath()) //####[110]####
            {//####[110]####
                if (n.finishTime > finishTimeOfPath) //####[111]####
                {//####[111]####
                    finishTimeOfPath = n.finishTime;//####[112]####
                }//####[113]####
            }//####[114]####
            if (finishTimeOfPath < smallestFinPath) //####[116]####
            {//####[116]####
                smallestFinPath = finishTimeOfPath;//####[117]####
                optimalPath = p;//####[118]####
            }//####[119]####
        }//####[121]####
        return optimalPath;//####[122]####
    }//####[123]####
}//####[123]####
