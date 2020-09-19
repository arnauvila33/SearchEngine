import java.io.File;
import java.io.IOException;
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
			throw new Exception("Introduce a path");
		}
		
		if(!am.hasFlag("-index")){
			outputFile=false;
		}else
			out=am.getPath("-index", Paths.get("index.txt"));
		
		if(am.getString("-path").toLowerCase().endsWith(".txt")||am.getString("-path").toLowerCase().endsWith(".text")){
			singleFile=true;
		}
		if(singleFile) {
			singleTxt(am.getPath("-path"),out);
		}
		//System.out.println("OutputFile: "+outputFile+" SingleFile: "+singleFile);
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
