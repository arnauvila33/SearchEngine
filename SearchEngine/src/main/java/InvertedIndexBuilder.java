import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Inverted Inex class builds an inverted index data structure.
 * 
 * @author Arnau Vila
 *
 */
public class InvertedIndexBuilder {
	/**
	 * Constructor to build the inverted index with path given.
	 * 
	 * @param invertedIndex invertedIndex to build.
	 * @param path          to use to build inverted index.
	 * @throws IOException exception
	 */
	public InvertedIndexBuilder(InvertedIndex invertedIndex, Path path) throws IOException {
		if (path != null && Files.isReadable(path) && Files.exists(path))
			fillInvertedIndex(invertedIndex, path);
		else
			System.out.print("Please input a correct path"); // TODO Remove the else and the output
	}

	// TODO Make fillInvertedIndex a static method, remove the constructor above, and call this method in Driver
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
	
	// TODO Make the methods below public (mostly for multithreading later)

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

		for (String stems : input) {
			invertedIndex.add(stems, inputPath.toString(), i++);
		}
		
		/*
		 * TODO This is a general solution that reuses your stemming code well. That is
		 * always how you should initially solve a problem. When we refactor, we then ask,
		 * is this general and reusable solution also the most efficient one? In this case,
		 * it is not. Often, we will justify replicating logic/code to create a less-general
		 * more-specific solution for the sake of efficiency.
		 *
		 * Go ahead and replicate most of the stemmer logic here in this code, except when
		 * you have a stemmed word, immediately add to the index (never to a list). That
		 * means:
		 *
		 * creating a snowball stemmer here
		 * creating a buffered reader here and reading line-by-line
		 * calling TextParser.parse to parse a line into words
		 * stemming those words and adding them directly to the index (never a list)
		 */

		// TODO P.S. Keep your stemmer around. It is useful for projects 2 and 4.		
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
				/* TODO 
				 else {
					 String lower = path.toString().toLowerCase();
					 if (lower.endsWith(".txt") || lower.endsWith(".text")) {
						list.add(path);
					}*/
			}
			return list;
		}
	}

}
