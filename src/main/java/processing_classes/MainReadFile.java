package processing_classes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainReadFile {

	public static void main(String[] args) throws IOException {
		
		String line; 	
		String nodeName, edgeOne, edgeTwo; 
		int nodeWeight, edgeWeight; 
		
		File inputfile = null;
		if (0 < args.length) {
		   inputfile = new File(args[0]);
		} else {
		   System.err.println("Invalid arguments count:" + args.length);
		}
				
		BufferedReader br = new BufferedReader(new FileReader(inputfile));
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			Pattern nodePattern = Pattern.compile("^([\\w]+)[\\s]*\\[[\\s]*Weight[\\s]*=([\\d]+)\\]"); 
			Matcher nodeMatch = nodePattern.matcher(line); 
			
			Pattern edgePattern = Pattern.compile("^([\\w]+)[\\s]*->[\\s]*([\\w]+)[\\s]*\\[[\\s]*Weight[\\s]*=([\\d]+)\\]"); 
			Matcher edgeMatch = edgePattern.matcher(line); 
			
			while (nodeMatch.find()) {
				nodeName = nodeMatch.group(1);
				nodeWeight = Integer.parseInt(nodeMatch.group(2));
		        System.out.println("name: " + nodeName);
		        System.out.println("weight: " + nodeWeight);
		    }	
		
			while (edgeMatch.find()) {
				edgeOne = edgeMatch.group(1);
				edgeTwo = edgeMatch.group(2);
				edgeWeight = Integer.parseInt(edgeMatch.group(3));
		        System.out.println("name: " + edgeOne);
		        System.out.println("name: " + edgeTwo);
		        System.out.println("weight: " + edgeWeight);
		    }
		
		}
		
		
		
		br.close(); 
		
		
		
	}
	

}
