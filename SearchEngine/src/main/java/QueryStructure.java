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
 * QueryStructure class.
 * 
 * @author arnau
 *
 */
public class QueryStructure implements QueryStructureInterface {

	/**
	 * holds a list of queries.
	 */
	private final Map<String, ArrayList<InvertedIndex.SingleResult>> queryStructure;
	/**
	 * invertedIndex used to build queryStructure
	 */
	private final InvertedIndex invertedIndex;

	/**
	 * Constructor
	 * 
	 * @param invertedIndex used
	 */
	public QueryStructure(InvertedIndex invertedIndex) {
		queryStructure = new TreeMap<String, ArrayList<InvertedIndex.SingleResult>>();
		this.invertedIndex = invertedIndex;
	}

	@Override
	public void processQueryStructure(Path path, boolean exact) throws IOException {
		BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
		String line = null;
		while ((line = reader.readLine()) != null) {
			processResult(line, exact);
		}
	}

	/**
	 * Builds singleResult and puts in in the query.
	 * 
	 * @param line  line of file
	 * @param exact exact search or partial
	 */
	public void processResult(String line, boolean exact) {
		TreeSet<String> stems = TextFileStemmer.uniqueStems(line);
		String queryString = String.join(" ", stems);

		if (!stems.isEmpty() && !queryStructure.containsKey(queryString)) {
			ArrayList<InvertedIndex.SingleResult> results = invertedIndex.search(stems, exact);
			queryStructure.put(queryString, results);
		}
	}

	@Override
	public void toJson(Path path) throws IOException {
		SimpleJsonWriter.asQueryStructure(queryStructure, path);
	}

}