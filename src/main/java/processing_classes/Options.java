package processing_classes;

public class Options {

private int numProcessors;
private int numThreads;
private boolean parallelise;
private boolean visualisation;
private String outputFileName;
	
	//constructor
	public Options() {
		numProcessors = 0;
		numThreads = 0;
		parallelise = false;
		visualisation = false;
		outputFileName = "output.dot";
	}
	
	//getter processors
	public int getNumProcessors() {
		return numProcessors;
	}
	
	//setter processors
	public void setNumProcessors(int nP) {
		numProcessors = nP;
	}
	
	//getter for threads
	public int getNumThreads() {
		return numThreads;
	}
	
	//setter for threads
	public void setNumThreads(int nT) {
		numThreads = nT;
	}
	
	//getter parallelise
	public boolean getParallel() {
		return parallelise;
	}
	
	//setter parallelise
	public void setParallel(boolean p) {
		parallelise = p;
	}
	
	//getter visualisation
	public boolean getVisualisation() {
		return visualisation;
	}
	
	//setter visualisation
	public void setVisualisation(boolean nP) {
		visualisation = nP;
	}
	
	//getter output file name
	public String getOutputFileName() {
		return outputFileName;
	}
	
	//setter output file name
	public void setOutputFileName(String oFN) {
		outputFileName = oFN.split(".dot")[0] + "-output.dot";
	}
	
}