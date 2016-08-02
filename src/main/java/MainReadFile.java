import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainReadFile {

	public static void main(String[] args) throws IOException {
		
		String line; 	
		String filename = "test.dot";
		String workingDirectory = System.getProperty("user.dir");
		String absoluteFilePath = workingDirectory + File.separator + filename;
		File file = new File(filename);
				
		BufferedReader br = new BufferedReader(new FileReader(file));
		while ((line = br.readLine()) != null) {
			System.out.println(line);
//			Pattern p = Pattern.compile("[a-zA-Z]+[\\s]*[ [\\s]*Weight[\\s]*=(\\d+)+]"); 
			Pattern p = Pattern.compile("^([a-zA-Z]+)[\\s]*[^-]"); 
			Pattern p2 = Pattern.compile("Weight[\\s]*=([0-9]+)");
			Matcher m = p.matcher(line); 
			Matcher m2 = p2.matcher(line); 
						
			while (m.find()) {
		        System.out.println("Found: " + m.group(1));
		        
		    }
			while (m2.find()) {
		        System.out.println("Found: " + m2.group(1));
		    }
			
			
		}
		
		
		
	}

}
