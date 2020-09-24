import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;



/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 */
public class Driver {
	
	/**
	 * Used to deal with the unique cases
	 * @param am ArgumentMap passed
	 * @return boolean to see if it should stop the execution
	 * @throws IOException
	 */
	private static boolean checkExceptions(ArgumentMap am) throws IOException {
		
		//Unique Case to deal with an empty output
		if(am.hasFlag("-path")==false&&am.hasFlag("-index")==true) {
			TextFileIndex txtF=new TextFileIndex();
			SimpleJsonWriter.asinvertedIndex(txtF.map,Paths.get("index.json"));
			return false;
		}
		
		//If there is no 
		if(am.hasFlag("-path")==false||am.getPath("-path")==null) {
			return false;
			
		}

		if(!Files.isReadable(am.getPath("-path"))&&!Files.exists(am.getPath("-path"))) {
			return false;
		}
		return true;
		
	}
	
	/**
	 * Main method for invertedIndex
	 * @param args The arguments passed
	 * @throws Exception
	 */
	private static void invertedIndex(String[] args) throws Exception {
		
		boolean outputFile=true; //Checks if there's going to be an output file.
		boolean singleFile=false; //Checks if there's going to be a single file or multiple.
		Path out=null;
		
		//ArgumentMap used to handle args
		ArgumentMap am=new ArgumentMap(args);
		
		//Checks and deals the Unique cases
		if(!checkExceptions(am)) {
			return;
		}
	
		
		if(!am.hasFlag("-index")){
			outputFile=false;
		}else if(am.getPath("-index")==null)
			out=am.getPath("-index",Paths.get("index.json"));
		else {			
			out=am.getPath("-index");			
		}
		
		if(am.getString("-path").toLowerCase().endsWith(".txt")||am.getString("-path").toLowerCase().endsWith(".text")||am.getString("-path").toLowerCase().endsWith(".md")){
			singleTxtPath(am.getPath("-path"),out,outputFile);
		}		
		else {
			directoryPath(am.getPath("-path"),out,outputFile);
		}
		
	}
	
	/**
	 * Used to compute a singleTxt file
	 * @param in path
	 * @param out path
	 * @param outputFile see if it should have output file or not
	 * @throws IOException
	 */
	private static void singleTxtPath(Path in,Path out,boolean outputFile) throws IOException {
		ArrayList<String> input=TextFileStemmer.listStems(in);
		TextFileIndex txtF=new TextFileIndex();		
		int i=1;
		//System.out.print(input.toString());
		for(String x:input) {
			txtF.add(in.toString(), x, i++);
		}
	
		if(outputFile==true) {
			//System.out.println(SimpleJsonWriter.asinvertedIndex(txtF.map));		
			SimpleJsonWriter.asinvertedIndex(txtF.map,out.normalize());
		}		
	}
	
	/**
	 * 
	 * @param in path
	 * @param out path
	 * @param outputFile see if it should have an output file or not
	 * @throws IOException
	 */
	private static void directoryPath(Path in, Path out, boolean outputFile)throws IOException{
		TextFileIndex txtF=new TextFileIndex();
		ArrayList<Path> list=new ArrayList<Path>();
		list=traverseDirectory(in,list);
		
		for(Path path:list) {
			ArrayList<String> input=TextFileStemmer.listStems(path);
			int i=1;
			for(String x:input) {
				txtF.add(path.toString(), x, i++);
			}
		
		}		
		if(outputFile==true) {
			//System.out.println(SimpleJsonWriter.asinvertedIndex(txtF.map));
			//System.out.println(out.toString()+"   "+out);
			SimpleJsonWriter.asinvertedIndex(txtF.map,out.normalize());
		}
	}
	/**
	 * Recursive function used to traverse all directories and find .txt files.
	 * @param directory directory to look for
	 * @param list list to keep all text files
	 * @return the list with all the text files
	 * @throws IOException
	 */
	private static ArrayList<Path> traverseDirectory(Path directory, ArrayList<Path> list) throws IOException {
		//ArrayList<Path> list=new ArrayList<>();
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			// use an enhanced-for or for-each loop for efficiency and simplicity
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					
					traverseDirectory(path, list);
				}else if(path.toString().toLowerCase().endsWith(".txt")||path.toString().toLowerCase().endsWith(".text")) {
					
					list.add(path);
				}

			
			}
			return list;
		}
	}
	
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// store initial start time
		Instant start = Instant.now();
	
		// output arguments
		System.out.println(Arrays.toString(args));
		
		// calls main InvertedAxis method
		invertedIndex(args);
		
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
