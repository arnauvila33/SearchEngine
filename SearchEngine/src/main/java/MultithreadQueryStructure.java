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
 * Multithreading Query Structure class
 * 
 * @author arnau
 *
 */
public class MultithreadQueryStructure implements QueryStructureInterface {

	/**
	 * holds a list of queries.
	 */
	private final Map<String, ArrayList<InvertedIndex.SingleResult>> queryStructure;
	/**
	 * invertedIndex used to build queryStructure
	 */
	private final ThreadSafeInvertedIndex invertedIndex;
	/**
	 * Threads used
	 */
	private final int threads;

	/**
	 * Constructor
	 * 
	 * @param invertedIndex to use
	 * @param threads number of threads to use
	 */
	public MultithreadQueryStructure(ThreadSafeInvertedIndex invertedIndex, int threads) {
		//super(invertedIndex);
		this.invertedIndex = invertedIndex;
		queryStructure = new TreeMap<String, ArrayList<InvertedIndex.SingleResult>>();
		this.threads=threads;
	}


	@Override
	public void processQueryStructure(Path path, boolean exact) {
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
