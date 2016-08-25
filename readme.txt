**********BLACH PROJECT README FILE**********

*********OVERVIEW*********
The aim of this project was to use AI and parallel processing to solve a diﬃcult scheduling problem. 
An algorithm needed to be created that can quickly and efficiently schedule tasks to multiple processors in an optimal way.
Along with this the algorithm needed to be parallelised and produce a live and interesting visualisation for the user.
As a team we decided to implement an A* algorithm due to the benefit of its speed. Our algorithm creates a valid and optimal schedule
and also parallelises. Along with this it produces a colourful and intuitive visualisation to show the user the algorithms process
through the task graph.

*********BUILD*********
In order to build from the source code you will need to have Maven installed. The pom.xml file can be used to create a Maven project
and then the ParaTask runtime libraries will need to be downloaded and added to the Maven dependencies.
Once this is done the Maven Install function can be used to build a jar file.

*********HOW TO RUN*********
In order to run the jar file you need to first open the command prompt.
Then navigate to the directory where the generated jar can be found. 
The jar can be run using the command "java -jar filename.jar inputfile.dot numProc". Where the filename.jar is the name of the generated
jar file, the input.dot is the file containing the graph to be input and numProc is the number of processors that you want the tasks
to be scheduled on, e.g. 4. As well as this there are a few optional arguments that can be added to the end of the command. These are:
-p numThreads : This tells the program how many threads to run on when parallelising. numThreads should be an integer indicating the number
              of threads to run on.
-v : Add this to the end of the command to enable visualisation.
-o output : Add this to call your output file a specified name, in this case the output would be called output.
An example using all of these arguments would be:
java -jar 306.jar Nodes7.dot 2 -v -p 4 -o SCHEDULE
This command would run the 306.jar file, use the Nodes7.dot file as input, schedule the tasks on 2 processors, create a visualization,
parallelise on 4 threads and create an output file called SCHEDULE.dot.

*********OTHER INFO*********
All other documentation and details about the project can be found in the Wiki of our Github repository, 306project1.


