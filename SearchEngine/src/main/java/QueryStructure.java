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

	// TODO private final InvertedIndex invertedIndex;
	
	/**
	 * Constructor for the QueryStructure
	 */
	public QueryStructure() { // TODO Pass in the invertedIndex to use here
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
			// TODO processQuerie(line, exact);
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
	
	/* TODO 
	public void processQuerie(String line, boolean exact) {
		TreeSet<String> stems = TextFileStemmer.uniqueStems(line);
		String queryString = String.join(" ", stems);
		
		if (!stems.isEmpty()) {
			ArrayList<InvertedIndex.SingleQuery> results = invertedIndex.search(stems, exact);
			queryStructure.put(queryString, results);
		}
	}
	*/

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