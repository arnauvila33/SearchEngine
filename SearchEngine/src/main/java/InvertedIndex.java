import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class InvertedIndex {
	
	
	/**
	 * Used to deal with the unique cases
	 * @param argumentMap ArgumentMap passed
	 * @return boolean to see if it should stop the execution
	 * @throws IOException exception
	 */
	private static boolean checkExceptions(ArgumentMap argumentMap) throws IOException {

		//Unique Case to deal with an empty output
		if(argumentMap.hasFlag("-path")==false&&argumentMap.hasFlag("-index")==true) {
			TextFileIndex textFileIndex=new TextFileIndex();
			SimpleJsonWriter.asinvertedIndex(textFileIndex.map,Paths.get("index.json"));
			System.out.println("No input path given. Printed an empty file.");		
			return false;
		}

		//If there is no input path
		if(argumentMap.hasFlag("-path")==false||argumentMap.getPath("-path")==null) {
			System.out.println("No input path given.");	
			return false;

		}
		
		//If the path is not readable or non-existent
		if(!Files.isReadable(argumentMap.getPath("-path"))&&!Files.exists(argumentMap.getPath("-path"))) {
			System.out.println("Path given is not readable or does not exist.");	
			return false;
		}
		return true;

	}

	// TODO Use Java naming conventions (avoid abbreviations, camel case, etc.)
	// TODO Also format code

	/**
	 * Main method for invertedIndex. Processes the path and index given, and calls the functions needed.
	 * @param args The arguments passed
	 * @throws Exception exception
	 */
	public void makeInvertedIndex(String[] args) throws Exception {

		boolean isOutput=true; //Checks if there's going to be an output file.

		Path outputFile=null;

		//ArgumentMap used to handle args
		ArgumentMap argumentMap=new ArgumentMap(args);

		//Checks and deals the Unique cases
		if(!checkExceptions(argumentMap)) {
			System.out.println("Please try again with a valid path and index.");
			return;
		}
		
		
		if(!argumentMap.hasFlag("-index")){
			isOutput=false;
		}else if(argumentMap.getPath("-index")==null)
			outputFile=argumentMap.getPath("-index",Paths.get("index.json"));
		else {
			outputFile=argumentMap.getPath("-index");
		}
		if(argumentMap.getString("-path").toLowerCase().endsWith(".txt")||argumentMap.getString("-path").toLowerCase().endsWith(".text")||argumentMap.getString("-path").toLowerCase().endsWith(".md")){
			computeSingleFile(argumentMap.getPath("-path"),outputFile,isOutput);
		}
		else {
			computeDirectory(argumentMap.getPath("-path"),outputFile,isOutput);
		}

	}

	/**
	 * Used to compute a singleTxt file. 
	 * @param inputPath the path of thew text file to compute.
	 * @param outputPath the path where the output file will be written.
	 * @param printOutput Boolean that checks if there should be an output file or not.
	 * @throws IOException exception 
	 */
	private static void computeSingleFile(Path inputPath,Path outputPath,boolean printOutput) throws IOException {
		ArrayList<String> input=TextFileStemmer.listStems(inputPath);
		TextFileIndex textFileIndex=new TextFileIndex();
		int i=1;
	
		for(String x:input) {
			textFileIndex.add(inputPath.toString(), x, i++);
		}

		printOutputFile(outputPath,printOutput,textFileIndex);
	}

	/**
	 * Compute Directory computes the inputPath given. It calls functions as needed.
	 * 
	 * @param inputPath the path of thew text file to compute.
	 * @param outputPath  the path where the output file will be written.
	 * @param printOutput Boolean that checks if there should be an output file or not.
	 * @throws IOException exception
	 */
	private static void computeDirectory(Path inputPath, Path outputPath, boolean printOutput)throws IOException{
		TextFileIndex textFileIndex=new TextFileIndex();
		ArrayList<Path> pathList=new ArrayList<Path>();
		pathList=traverseDirectory(inputPath,pathList);

		for(Path path:pathList) {
			ArrayList<String> input=TextFileStemmer.listStems(path);
			int i=1;
			for(String x:input) {
				textFileIndex.add(path.toString(), x, i++);
			}

		}
		printOutputFile(outputPath,printOutput,textFileIndex);
	}
	
	/**
	 * It prints the textFileIndex as an InvertedIndex in the outputPath if printOutput is true.
	 * 
	 * @param outputPath path to write to.
	 * @param printOutput boolean that decides if we should write to output file or not.
	 * @param textFileInd the textFileIndex used to write to path.
	 * @throws IOException exception
	 */
	private static void printOutputFile(Path outputPath, boolean printOutput, TextFileIndex textFileInd) throws IOException {
		if(printOutput) {
			SimpleJsonWriter.asinvertedIndex(textFileInd.map,outputPath.normalize());
		}
	}

	/**
	 * Recursive function used to traverse all directories and find all .txt files.
	 * 
	 * @param directory The path directory to look for text files.
	 * @param list The list where all the text files are stored.
	 * @return the list with all the text files found.
	 * @throws IOException exception
	 */
	private static ArrayList<Path> traverseDirectory(Path directory, ArrayList<Path> list) throws IOException {
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

}
