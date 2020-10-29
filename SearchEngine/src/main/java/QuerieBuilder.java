import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * QuerieBuilder class builds the QuerieStructure passed
 * 
 * @author arnau
 *
 */
public class QuerieBuilder {

	/**
	 * Process Querie processes the querie with one thread.
	 * 
	 * @param querieStructure The querie structure to build to.
	 * @param invertedIndex   invertedIndex to use to search.
	 * @param path            path used to read querie
	 * @param exact           boolean that determines exact/partial Search
	 * @throws IOException exception
	 */

	public static void processQuerie(QuerieStructure querieStructure, InvertedIndex invertedIndex, Path path,
			boolean exact) throws IOException {

		BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);

		String line = null;
		while ((line = reader.readLine()) != null) {
			TreeSet<String> list = new TreeSet<String>();
			list = TextFileStemmer.uniqueStems(line);
			String QuerieString = String.join(" ", list);
			ArrayList<SingleQuerie> queries = new ArrayList<SingleQuerie>();
			if (list.size() > 0) {
				TreeSet<String> words = getWordsList(list, exact, invertedIndex);
				TreeSet<String> paths = getPathList(words, invertedIndex);
				queries = search(words, paths, invertedIndex);
				querieStructure.add(QuerieString, queries);
			}
		}

	}

	/**
	 * Search method used to build querie Structure
	 * 
	 * @param words         words passed to search
	 * @param paths         paths passed to search in
	 * @param invertedIndex the invertedIndex strucure used to find other words.
	 * @return ArrayList with the queries found
	 */
	public static ArrayList<SingleQuerie> search(TreeSet<String> words, TreeSet<String> paths,
			InvertedIndex invertedIndex) {
		ArrayList<SingleQuerie> querieResults = new ArrayList<SingleQuerie>();

		Iterator<String> pathsIterator = paths.iterator();
		while (pathsIterator.hasNext()) {
			String where = pathsIterator.next();
			int count = 0;
			for (String word : words) {
				if (invertedIndex.get(word).contains(where)) {
					count += invertedIndex.size(word, where);
				}
			}
			double score = (double) count / (double) invertedIndex.getCount(where);
			SingleQuerie querieTemp = new SingleQuerie(where, count, score);
			querieResults.add(querieTemp);
		}
		Collections.sort(querieResults);
		return querieResults;
	}

	/**
	 * gets the word list of words to find.
	 * 
	 * @param querie        the words to find
	 * @param exact         Determines if it is exact/partial search
	 * @param invertedIndex structure to search.
	 * @return the set of words.
	 */
	private static TreeSet<String> getWordsList(TreeSet<String> querie, boolean exact, InvertedIndex invertedIndex) {
		if (exact)
			return querie;
		else
			return getPartialWords(querie, invertedIndex);
	}

	/**
	 * Returns partial words for partial search.
	 * 
	 * @param querie        querie passed
	 * @param invertedIndex structure to search
	 * @return the set of partial words.
	 */
	private static TreeSet<String> getPartialWords(TreeSet<String> querie, InvertedIndex invertedIndex) {
		TreeSet<String> foundWords = new TreeSet<String>();
		for (String word : invertedIndex.get()) {
			for (String querieWord : querie) {
				if (word.startsWith(querieWord)) {
					foundWords.add(word);
				}
			}
		}
		return foundWords;
	}

	/**
	 * Returns the list of paths of the words passed.
	 * 
	 * @param words         used to find their respective paths.
	 * @param invertedIndex structure to search.
	 * @return the set of paths
	 */
	private static TreeSet<String> getPathList(TreeSet<String> words, InvertedIndex invertedIndex) {
		TreeSet<String> foundpaths = new TreeSet<String>();
		for (String word : words) {
			foundpaths.addAll(invertedIndex.get(word));
		}
		return foundpaths;
	}

}