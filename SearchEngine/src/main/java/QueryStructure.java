import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * QuerieBuilder class builds the QuerieStructure passed
 * 
 * @author arnau
 *
 */
public class QueryStructure {

	/**
	 * holds a list of queries.
	 */
	private final Map<String, ArrayList<InvertedIndex.SingleQuery>> queryStructure;

	/**
	 * Constructor for the QueryStructure
	 */
	public QueryStructure() {
		queryStructure = new TreeMap<String, ArrayList<InvertedIndex.SingleQuery>>();

	}

	/**
	 * Process Querie processes the querie with one thread.
	 * 
	 * @param invertedIndex   invertedIndex to use to search.
	 * @param path            path used to read querie
	 * @param exact           boolean that determines exact/partial Search
	 * @throws IOException exception
	 */

	public void processQuerie(InvertedIndex invertedIndex, Path path, boolean exact) throws IOException {

		BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);

		String line = null;
		while ((line = reader.readLine()) != null) {
			TreeSet<String> list = new TreeSet<String>();
			list = TextFileStemmer.uniqueStems(line);
			String QuerieString = String.join(" ", list);
			ArrayList<InvertedIndex.SingleQuery> queries = new ArrayList<InvertedIndex.SingleQuery>();
			if (list.size() > 0) {
				TreeSet<String> words = invertedIndex.getWordsList(list, exact);
				TreeSet<String> paths = invertedIndex.getPathList(words);
				queries = invertedIndex.search(words, paths);
				queryStructure.put(QuerieString, queries);
			}
		}

	}

	/**
	 * Prints it to JSON
	 * 
	 * @param path to print to
	 * @throws IOException exception
	 */
	public void toJson(Path path) throws IOException {
		SimpleJsonWriter.asQuerieStructure(queryStructure, path);
	}

}