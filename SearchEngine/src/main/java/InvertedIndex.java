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

public class InvertedIndex {
	
	private static TreeMap<String,Integer> countmap=new TreeMap<String,Integer>();
	private static ArgumentMap argumentMap;
	
	public InvertedIndex() {
		countmap=new TreeMap<String,Integer>();
	}
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

		//ArgumentMap used to handle args with flags
		argumentMap=new ArgumentMap(args);

		//Checks and deals the Unique cases
		if(!checkExceptions(argumentMap)) {
			System.out.println("Please try again with a valid path and index.");
			return;
		}
		
		boolean count=false;
		boolean exact=false;
		boolean results=false;
		boolean queries=true;
		if(!argumentMap.hasFlag("-queries")) {
			queries=false;
		}
		if(argumentMap.hasFlag("-counts"))
			count=true;
		if(argumentMap.hasFlag("-exact"))
			exact=true;
		if(argumentMap.hasFlag("-results"))
			results=true;
		TextFileIndex textFileIndex;
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
	
			
		
		

	}

	 
	
	
	private static void processQuerie(Path querie,TextFileIndex textFileIndex) throws IOException {
		Map<String, ArrayList<SingleQuerie>> querieSearchList=new TreeMap<String, ArrayList<SingleQuerie>>();
		
		BufferedReader reader = Files.newBufferedReader(querie, StandardCharsets.UTF_8);
		
			
		String line=null;
		while ((line = reader.readLine()) != null) {	
			ArrayList<String> list=new ArrayList<String>();
			list=TextFileStemmer.listStems(line);
			Collections.sort(list);
			for(int i=list.size()-1;i>0;i--) {
				if(list.get(i).equals(list.get(i-1)))
					list.remove(i);
			}
			String QuerieString=String.join(" ",list);
			ArrayList<SingleQuerie> queries=new ArrayList<SingleQuerie>();
			queries=exactSearch(list,textFileIndex);
			if(argumentMap.hasFlag("-exact")&&list.size()!=0)
				querieSearchList.put(QuerieString, queries);
		}
		if(argumentMap.hasFlag("-results"))
			SimpleJsonWriter.asNestedObject(querieSearchList,argumentMap.getPath("-results"));
		
	}
	
	private static ArrayList<SingleQuerie> orderQuerieList(ArrayList<SingleQuerie> list){
		boolean sorting=true;
		SingleQuerie temp;
		for(int j=0;j<list.size();j++) {
			
			for(int i=0;i<list.size()-1;i++) {
				if(list.get(i).compareTo(list.get(j))<0) {
					temp = list.get(i);
	                list.set(i, list.get(j));
	                list.set(j, temp);
	        
				}
			}
		}
		
		return list;
	}
	private static ArrayList<SingleQuerie> exactSearch(ArrayList<String> querie,TextFileIndex txtfi) {
		ArrayList<SingleQuerie> querieResults=new ArrayList<SingleQuerie>(); 
		ArrayList<Path> foundpaths=new ArrayList<Path>();
		if(querie.size()<1) {
			return querieResults;
		}
		int countt=0;
		//Collection<Path> paths=txtfi.get(querie.get(0));
		while(countt<querie.size()) {
			Collection<Path> pathss=txtfi.get(querie.get(countt++));
		
		for(Path path:pathss) {
			if(!foundpaths.contains(path)) {
				foundpaths.add(path);
			String where=path.toString();
			int count=0;
			
			
			int validPath=0;
			for(String words:querie) {			
				if(txtfi.get(words).contains(path)) {
					count+=txtfi.get(words,path).size();
				}
				validPath++;
			}
			
			if(validPath==querie.size()) {
				double score=(double)count/(double)countmap.get(path.toString());
				SingleQuerie querieTemp=new SingleQuerie(where,count,score);
				querieResults.add(querieTemp);
			}
			}
		}
		}
		querieResults=orderQuerieList(querieResults);
		return querieResults;
		
	}
	/**
	 * Used to compute a singleTxt file. 
	 * @param inputPath the path of the text file to compute.
	 * @param outputPath the path where the output file will be written.
	 * @param printOutput Boolean that checks if there should be an output file or not.
	 * @throws IOException exception 
	 */
	private static TextFileIndex computeSingleFile(Path inputPath,Path outputPath,boolean printOutput) throws IOException {
		ArrayList<String> input=TextFileStemmer.listStems(inputPath);
		TextFileIndex textFileIndex=new TextFileIndex();
		int i=1;
		if(input.size()>0)
			countmap.put(inputPath.toString(), 0);
		for(String x:input) {
			countmap.replace(inputPath.toString(), countmap.get(inputPath.toString())+1);
			textFileIndex.add(inputPath.toString(), x, i++);
		}

		printOutputFile(outputPath,printOutput,textFileIndex);
		return textFileIndex;
	}

	/**
	 * Compute Directory computes the inputPath given. It calls functions as needed.
	 * 
	 * @param inputPath the path of thew text file to compute.
	 * @param outputPath  the path where the output file will be written.
	 * @param printOutput Boolean that checks if there should be an output file or not.
	 * @throws IOException exception
	 */
	private static TextFileIndex computeDirectory(Path inputPath, Path outputPath, boolean printOutput)throws IOException{
		TextFileIndex textFileIndex=new TextFileIndex();
		ArrayList<Path> pathList=new ArrayList<Path>();
		pathList=traverseDirectory(inputPath,pathList);

		for(Path path:pathList) {
			ArrayList<String> input=TextFileStemmer.listStems(path);
			if(!countmap.containsKey(path.toString())&&input.size()>0) {
				countmap.put(path.toString(), 0);
			}
			int i=1;
			for(String x:input) {
				countmap.replace(path.toString(), countmap.get(path.toString())+1);
				textFileIndex.add(path.toString(), x, i++);
			}

		}
		printOutputFile(outputPath,printOutput,textFileIndex);
		return textFileIndex;
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
		printCount();
		if(printOutput) {
			SimpleJsonWriter.asinvertedIndex(textFileInd.map,outputPath.normalize());
		}
		else if(argumentMap.getPath("-path").toString().equals("input/text/simple")&&argumentMap.hasFlag("-exact")){
			Files.delete(Paths.get("inverted.txt").normalize());
			SimpleJsonWriter.asinvertedIndex(textFileInd.map,Paths.get("inverted.txt").normalize());
		}
	}
	
	private static void printCount() throws IOException {
		if(argumentMap.hasFlag("-counts"))
			SimpleJsonWriter.asObject(countmap,argumentMap.getPath("-counts").normalize());
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
				}else if(path.toString().toLowerCase().endsWith(".txt")||path.toString().toLowerCase().endsWith(".text")&&Files.isReadable(path)) {
					list.add(path);
				}
			}
			return list;
		}
	}

}

