import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndexBuilder {
	
	private static TreeMap<String,Integer> countmap=new TreeMap<String,Integer>();
	private static ArgumentMap argumentMap;
	
	
	public InvertedIndexBuilder(InvertedIndex invertedIndex, Path path) throws IOException {
		if (path != null && Files.isReadable(path) && Files.exists(path))
			fillInvertedIndex(invertedIndex, path);
		else
			System.out.print("Please input a correct path");
	}
	
	/**
	 * Method used to fill the inverted index with the path given.
	 * 
	 * @param invertedIndex invertedIndex to build.
	 * @param path          path given.
	 * @throws IOException exception.
	 */
	public void fillInvertedIndex(InvertedIndex invertedIndex, Path path) throws IOException {
		if (!Files.isDirectory(path)) {
			computeSingleFile(invertedIndex, path);
		} else {
			computeDirectory(invertedIndex, path);
		}
	}

	// TODO Use Java naming conventions (avoid abbreviations, camel case, etc.)
	// TODO Also format code

	/**
	 * Main method for invertedIndex. Processes the path and index given, and calls the functions needed.
	 * @param args The arguments passed
	 * @throws Exception exception
	 */
	/*
	public void makeInvertedIndex(String[] args) throws Exception {
		
		boolean isOutput=true; //Checks if there's going to be an output file.

		Path outputFile=null;

		//ArgumentMap used to handle args with flags
		argumentMap=new ArgumentMap(args);

		//Checks and deals the Unique cases
		if(!checkExceptions(argumentMap)) {
			System.out.println("Please try again with a valid path and index.");
			return;
		}
		
		boolean queries=true;
		if(!argumentMap.hasFlag("-queries")) {
			queries=false;
		}
	
		InvertedIndex textFileIndex;
			if(!argumentMap.hasFlag("-index")){
				isOutput=false;
			}else if(argumentMap.getPath("-index")==null)
				outputFile=argumentMap.getPath("-index",Paths.get("index.json"));
			else {
				outputFile=argumentMap.getPath("-index");
			}
			if(argumentMap.getString("-path").toLowerCase().endsWith(".txt")||argumentMap.getString("-path").toLowerCase().endsWith(".text")||argumentMap.getString("-path").toLowerCase().endsWith(".md")){
				textFileIndex=computeSingleFile(argumentMap.getPath("-path"),outputFile,isOutput);
			}
			else {
				textFileIndex=computeDirectory(argumentMap.getPath("-path"),outputFile,isOutput);
			}
			
			
			if(queries) {
				processQuerie(argumentMap.getPath("-queries"),textFileIndex);
			}
	
			
		
}*/


	/**
	 * Used to compute a singleTxt file.
	 * 
	 * @param inputPath     the path of thew text file to compute.
	 * @param invertedIndex the TextFileIndex used to write the invertedindex.
	 * @throws IOException exception
	 */
	private static void computeSingleFile(InvertedIndex invertedIndex, Path inputPath) throws IOException {
		ArrayList<String> input = new ArrayList<String>();
		TextFileStemmer.listStems(inputPath, input);
		int i = 1;
		if(input.size()>0)
			//countmap.put(inputPath.toString(), 0);
		for (String stems : input) {
			//countmap.replace(inputPath.toString(), countmap.get(inputPath.toString())+1);
			invertedIndex.add(stems, inputPath.toString(), i++);
		}
	}
	/**
	 * Compute Directory computes the inputPath given. It calls functions as needed.
	 * 
	 * @param inputPath     the path of thew text file to compute.
	 * @param invertedIndex the TextFileIndex used to write the invertedindex.
	 * @throws IOException exception
	 */
	private static void computeDirectory(InvertedIndex invertedIndex, Path inputPath) throws IOException {
		ArrayList<Path> pathList = new ArrayList<Path>();
		pathList = traverseDirectory(inputPath, pathList);

		for (Path path : pathList) {
			computeSingleFile(invertedIndex, path);
		}
	}
	

	
	/**
	 * This function outputs the count to the counts path
	 * @throws IOException exception
	 */
	private static void printCount() throws IOException {
		if(argumentMap.hasFlag("-counts"))
			SimpleJsonWriter.asObject(countmap,argumentMap.getPath("-counts").normalize());
	}

	/**
	 * Recursive function used to traverse all directories and find all .txt files.
	 * 
	 * @param directory The path directory to look for text files.
	 * @param list      The list where all the text files are stored.
	 * @return the list with all the text files found.
	 * @throws IOException exception
	 */
	private static ArrayList<Path> traverseDirectory(Path directory, ArrayList<Path> list) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					traverseDirectory(path, list);
				} else if (path.toString().toLowerCase().endsWith(".txt")
						|| path.toString().toLowerCase().endsWith(".text")) {
					list.add(path);
				}
			}
			return list;
		}
	}

}

