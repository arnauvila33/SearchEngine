import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Inverted Inex class builds an inverted index data structure.
 * 
 * @author Arnau Vila
 *
 */
public class InvertedIndexBuilder {

	/**
	 * ArgumentMap used.
	 */
	private static ArgumentMap argumentMap;

	/**
	 * Used to deal with the unique cases
	 * 
	 * @return boolean to see if it should stop the execution
	 * @throws IOException exception
	 */
	private static boolean checkExceptions() throws IOException {

		// Unique Case to deal with an empty output
		if (argumentMap.hasFlag("-path") == false && argumentMap.hasFlag("-index") == true) {
			InvertedIndex textFileIndex = new InvertedIndex();
			SimpleJsonWriter.asinvertedIndex(textFileIndex.getMap(), Paths.get("index.json"));
			System.out.println("No input path given. Printed an empty file.");
			return false;
		}

		// If there is no input path
		if (argumentMap.hasFlag("-path") == false || argumentMap.getPath("-path") == null) {
			System.out.println("No input path given.");
			return false;

		}

		// If the path is not readable or non-existent
		if (!Files.isReadable(argumentMap.getPath("-path")) && !Files.exists(argumentMap.getPath("-path"))) {
			System.out.println("Path given is not readable or does not exist.");
			return false;
		}
		return true;

	}

	/**
	 * Main method for invertedIndex. Processes the path and index given, and calls
	 * the functions needed.
	 * 
	 * @param args The arguments passed
	 * @throws Exception exception
	 */
	public void makeInvertedIndex(String[] args) throws Exception {
		// ArgumentMap used to handle args
		argumentMap = new ArgumentMap(args);

		// Checks and deals the Unique cases
		if (!checkExceptions()) {
			System.out.println("Please try again with a valid path and index.");
			return;
		}

		InvertedIndex textFileIndex = new InvertedIndex();
		if (argumentMap.getString("-path").toLowerCase().endsWith(".txt")
				|| argumentMap.getString("-path").toLowerCase().endsWith(".text")
				|| argumentMap.getString("-path").toLowerCase().endsWith(".md")) {
			computeSingleFile(argumentMap.getPath("-path"), textFileIndex);
		} else {
			computeDirectory(argumentMap.getPath("-path"), textFileIndex);
		}
		printOutputFile(textFileIndex);

	}

	/**
	 * Used to compute a singleTxt file.
	 * 
	 * @param inputPath     the path of thew text file to compute.
	 * @param textFileIndex the TextFileIndex used to write the invertedindex.
	 * @throws IOException exception
	 */
	private static void computeSingleFile(Path inputPath, InvertedIndex textFileIndex) throws IOException {
		ArrayList<String> input = new ArrayList<String>();// TextFileStemmer.listStems(path);
		TextFileStemmer.listStems(inputPath, input);
		int i = 1;

		for (String x : input) {
			textFileIndex.add(inputPath.toString(), x, i++);
		}
	}

	/**
	 * Compute Directory computes the inputPath given. It calls functions as needed.
	 * 
	 * @param inputPath     the path of thew text file to compute.
	 * @param textFileIndex the TextFileIndex used to write the invertedindex.
	 * @throws IOException exception
	 */
	private static void computeDirectory(Path inputPath, InvertedIndex textFileIndex) throws IOException {
		ArrayList<Path> pathList = new ArrayList<Path>();
		pathList = traverseDirectory(inputPath, pathList);

		for (Path path : pathList) {
			computeSingleFile(path,textFileIndex);
		}
	}

	/**
	 * It prints the textFileIndex as an InvertedIndex in the outputPath if
	 * printOutput is true.
	 * 
	 * @param textFileInd the textFileIndex used to write to path.
	 * @throws IOException exception
	 */
	private static void printOutputFile(InvertedIndex textFileInd) throws IOException {
		Path outputFile;
		if (argumentMap.hasFlag("-index")) {
			if (argumentMap.getPath("-index") == null)
				outputFile = argumentMap.getPath("-index", Paths.get("index.json"));
			else {
				outputFile = argumentMap.getPath("-index");
			}
			SimpleJsonWriter.asinvertedIndex(textFileInd.getMap(), outputFile.normalize());
		}
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
