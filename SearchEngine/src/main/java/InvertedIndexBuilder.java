
import java.io.IOException;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;

public class InvertedIndexBuilder {

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
		if (input.size() > 0)
			// countmap.put(inputPath.toString(), 0);
			for (String stems : input) {
				// countmap.replace(inputPath.toString(), countmap.get(inputPath.toString())+1);
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
