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
	 * @throws IOException exception
	 */
	public void processQuery(Path path, boolean exact) throws IOException {

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

		if (!stems.isEmpty()) {
			/*
			 * TODO What happens if the lines "hello world" and "world hello" appear
			 * in the same query file? Both will stem and sort to the same joined
			 * "hello world" string. If you already found results "hello world" there
			 * is no need to do it again, when your code encounters "world hello" in
			 * the file. In other words...
			 *
			 * if the joined string is already in your results map, return (don't
			 * search);
			 */
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
		SimpleJsonWriter.asQuerieStructure(queryStructure, path);
	}

}