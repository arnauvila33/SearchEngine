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
public class QueryStructure {

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

	/**
	 * Process Querie processes the querie with one thread.
	 * 
	 * @param path  path used to read querie
	 * @param exact boolean that determines exact/partial Search 
	 */
	public void processQuery(Path path, boolean exact) {

		try { // TODO Try with resources?
			BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
			String line = null;
			while ((line = reader.readLine()) != null) {
				processResult(line, exact);
			}
		} catch (IOException e) {
			System.out.println(e);
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

	/**
	 * Prints it to JSON
	 * 
	 * @param path to print to
	 * @throws IOException exception
	 */
	public void toJson(Path path) throws IOException {
		SimpleJsonWriter.asQueryStructure(queryStructure, path);
	}
	
	


} // TODO Why all these blank lines