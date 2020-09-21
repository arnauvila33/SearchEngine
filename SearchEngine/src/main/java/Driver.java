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

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 */
public class Driver {
	
	
	private static void invertedIndex(String[] args) throws Exception {
		
		boolean outputFile=true;
		boolean singleFile=false;
		Path out=null;
		ArgumentMap am=new ArgumentMap(args);
		
		
		
		if(args.length<2||am.hasFlag("-path")==false) {
			return;
		}
		
		if(!am.hasFlag("-index")){
			outputFile=false;
		}else
			out=am.getPath("-index", Paths.get("index.txt"));

		if(am.getString("-path").toLowerCase().endsWith(".txt")||am.getString("-path").toLowerCase().endsWith(".text")||am.getString("-path").toLowerCase().endsWith(".md")){
			singleFile=true;
		}
		if(singleFile) {
			singleTxt(am.getPath("-path"),out);
		}
		else {
			TextFileIndex txtF=new TextFileIndex();
			ArrayList<Path> list=new ArrayList<Path>();
			list=traverseDirectory(am.getPath("-path"),list);
			
			for(Path path:list) {
				ArrayList<String> input=TextFileStemmer.listStems(path);
				int i=1;
				for(String x:input) {
					txtF.add(path.toString(), x, i++);
				}
			
			}
			SimpleJsonWriter json=new SimpleJsonWriter();
			
			if(out!=null) {
				//System.out.println(SimpleJsonWriter.asinvertedIndex(txtF.map));
				System.out.println(out.toString()+"   "+out);
				SimpleJsonWriter.asinvertedIndex(txtF.map,out);
			}
		}
		//System.out.println("OutputFile: "+outputFile+" SingleFile: "+singleFile);
	}
	
	private static boolean isTxtFile(Path p) {
		if(p.toString().toLowerCase().endsWith(".txt")||p.toString().toLowerCase().endsWith(".text")){
			if(p.toString().toLowerCase().endsWith(".text")) {
				/* Included to get rid of ".text" file in testSimpleDirectory();*/
				if(p.toString().charAt(p.toString().length()-6)!='\\'){ 
					return true;
				}
				else
					return false;
						
			}
			return true;
		}
		return false;
	}
	
	private static void singleTxt(Path in,Path out) throws IOException {
		TextFileStemmer stemmer=new TextFileStemmer();
		ArrayList<String> input=TextFileStemmer.listStems(in);
		TextFileIndex txtF=new TextFileIndex();
		SimpleJsonWriter json=new SimpleJsonWriter();
		int i=1;
		//System.out.print(input.toString());
		for(String x:input) {
			txtF.add(in.toString(), x, i++);
		}
	
		if(out!=null) {
			//System.out.println(SimpleJsonWriter.asinvertedIndex(txtF.map));
			
			SimpleJsonWriter.asinvertedIndex(txtF.map,out);
		}
		
		
	}
	private static ArrayList<Path> traverseDirectory(Path directory, ArrayList<Path> list) throws IOException {
		//ArrayList<Path> list=new ArrayList<>();
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			// use an enhanced-for or for-each loop for efficiency and simplicity
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					
					traverseDirectory(path, list);
				}else if(isTxtFile(path)) {
					
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
		/*
		 * TODO Modify this method as necessary. !
		 */
		
		
		// store initial start time
		Instant start = Instant.now();
	
		// output arguments
		System.out.println(Arrays.toString(args));
		invertedIndex(args);
		
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
		
	}

	/*
	 * Generally, "driver" classes are responsible for setting up and calling
	 * other classes, usually from a main() method that parses command-line
	 * parameters. If the driver were only responsible for a single class, we use
	 * that class name. For example, "TaxiDriver" is what we would name a driver
	 * class that just sets up and calls the "Taxi" class.
	 */
}
