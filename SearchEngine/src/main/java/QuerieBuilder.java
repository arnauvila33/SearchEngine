import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;

public class QuerieBuilder {

	public QuerieBuilder(QuerieStructure querie, InvertedIndex invertedIndex, Path path, boolean exact)
			throws IOException {
		if (path != null && Files.isReadable(path) && Files.exists(path)) {
			processQuerie(querie, invertedIndex, path, exact);
		} else
			System.out.print("Please input a correct querie path");

	}

	public static void processQuerie(QuerieStructure querieSearchList, InvertedIndex invertedIndex, Path path,
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
				querieSearchList.add(QuerieString, queries);
			}
		}

	}

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

	private static TreeSet<String> getWordsList(TreeSet<String> querie, boolean exact, InvertedIndex invertedIndex) {
		if (exact)
			return querie;
		else
			return getPartialWords(querie, invertedIndex);
	}

	private static TreeSet<String> getPathList(TreeSet<String> words, InvertedIndex invertedIndex) {
		TreeSet<String> foundpaths = new TreeSet<String>();
		for (String word : words) {
			foundpaths.addAll(invertedIndex.get(word));
		}
		return foundpaths;
	}

}