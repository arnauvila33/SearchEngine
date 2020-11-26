import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Inverted Index Builder class builds an inverted index data structure.
 * 
 * @author Arnau Vila
 *
 */
public class InvertedIndexBuilder {

	/**
	 * Method used to fill the inverted index with the path given.
	 * 
	 * @param invertedIndex invertedIndex to build.
	 * @param path          path given.
	 * @throws IOException exception.
	 */
	public static void fillInvertedIndex(InvertedIndex invertedIndex, Path path) throws IOException {
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
	public static void computeSingleFile(InvertedIndex invertedIndex, Path inputPath) throws IOException {
		int i = 1;
		try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8);) {
			String line = null;
			Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			String path = inputPath.toString();
			while ((line = reader.readLine()) != null) {
				for (String word : TextParser.parse(line)) {
					invertedIndex.add(stemmer.stem(word).toString(), path, i++);
				}
			}
		}
	}

	/**
	 * Compute Directory computes the inputPath given. It calls functions as needed.
	 * 
	 * @param inputPath     the path of thew text file to compute.
	 * @param invertedIndex the TextFileIndex used to write the invertedindex.
	 * @throws IOException exception
	 */
	public static void computeDirectory(InvertedIndex invertedIndex, Path inputPath) throws IOException {
		ArrayList<Path> pathList = traverseDirectory(inputPath, new ArrayList<Path>());

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
	public static ArrayList<Path> traverseDirectory(Path directory, ArrayList<Path> list) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					traverseDirectory(path, list);
				} else {
					String lower = path.toString().toLowerCase();
					if (lower.endsWith(".txt") || lower.endsWith(".text")) {
						list.add(path);
					}
				}
			}
			return list;
		}
	}
	

}
