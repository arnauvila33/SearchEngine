import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * TODO Design
 * 
 * In cases where there are common methods, but you can't reuse very much
 * code and have to either break encapsulation or create new private data, the
 * extends relationship doesn't really end up helping very much.
 * 
 * Create an interface with the common methods in your single and
 * multithreaded classes. Instead of extending, have both implement that
 * interface. Each class will have its own data and implementations. (There
 * will be some opportunity still for code reuse, which becomes more apparent
 * after you have the rest optimized.)
 * 
 * public class MultithreadQueryStructure implements QueryStructureInterface {
 * public class QueryStructure implements QueryStructureInterface { 
 * 
 * ...pass in the numebr of threads to the constructor instead the method for this
 */

/**
 * Multithreading Query Structure class
 * 
 * @author arnau
 *
 */
public class MultithreadQueryStructure extends QueryStructure {

	/**
	 * holds a list of queries.
	 */
	private final Map<String, ArrayList<InvertedIndex.SingleResult>> queryStructure;
	/**
	 * invertedIndex used to build queryStructure
	 */
	private final ThreadSafeInvertedIndex invertedIndex;

	/**
	 * Constructor
	 * 
	 * @param invertedIndex to use
	 */
	public MultithreadQueryStructure(ThreadSafeInvertedIndex invertedIndex) {
		super(invertedIndex);
		this.invertedIndex = invertedIndex;
		queryStructure = new TreeMap<String, ArrayList<InvertedIndex.SingleResult>>();
	}

	/**
	 * ProcessQueryMultithreading
	 * 
	 * @param path    path
	 * @param exact   bool
	 * @param threads thread number
	 */
	public void processQueryMultithreading(Path path, boolean exact, int threads) {

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String line = null;
			WorkQueue queue = new WorkQueue(threads);
			while ((line = reader.readLine()) != null) {
				queue.execute(new Task(line, exact));
			}
			queue.join();
		} catch (IOException e) {
			System.out.println("Unable to read file " + path);
		}

	}

	@Override
	public void toJson(Path path) throws IOException {
		synchronized(queryStructure) {
			SimpleJsonWriter.asQueryStructure(queryStructure, path);
		}
	}

	/**
	 * Task class used for partial/exact search with multithreading.
	 * 
	 * @author arnau
	 *
	 */
	private class Task implements Runnable {

		/**
		 * exact boolean
		 */
		private final boolean exact;
		/**
		 * string line
		 */
		private final String line;

		/**
		 * Task constructor
		 * 
		 * @param invertedIndex  object
		 * @param queryStructure map
		 * @param line           string
		 * @param exact          boolean
		 */
		public Task(String line, boolean exact) {
			this.exact = exact;
			this.line = line;
		}

		@Override
		public void run() {
			TreeSet<String> stems = TextFileStemmer.uniqueStems(line);
			String queryString = String.join(" ", stems);
			synchronized (queryStructure) {
				if (stems.isEmpty() || queryStructure.containsKey(queryString)) {
					return;
				}
			}
			ArrayList<InvertedIndex.SingleResult> results = invertedIndex.search(stems, exact);
			synchronized (queryStructure) {
				queryStructure.put(queryString, results);
			}
		}
	}

}
