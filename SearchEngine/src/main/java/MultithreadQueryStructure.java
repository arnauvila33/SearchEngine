import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;



public class MultithreadQueryStructure extends QueryStructure{
	
	/**
	 * holds a list of queries.
	 */
	private final Map<String, ArrayList<InvertedIndex.SingleResult>> queryStructure;
	/**
	 * invertedIndex used to build queryStructure
	 */
	private final ThreadSafeInvertedIndex invertedIndex;
	
	public MultithreadQueryStructure(ThreadSafeInvertedIndex invertedIndex) {
		super(invertedIndex);
		this.invertedIndex=invertedIndex;
		queryStructure = new TreeMap<String, ArrayList<InvertedIndex.SingleResult>>();
	}
	/**
	 * ProcessQueryMultithreading
	 * @param path path
	 * @param exact bool
	 * @param threads thread number
	 */
	public void processQueryMultithreading(Path path, boolean exact, int threads)  {
		
		try(BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {	
			String line = null;
			WorkQueue queue = new WorkQueue(threads);
			while ((line = reader.readLine()) != null) {
				queue.execute(new Task(invertedIndex, queryStructure, line,exact));
			}
			queue.join();
		} catch (IOException e) {
			System.out.println(e);
		}
		

	}
	@Override
	public void toJson(Path path) throws IOException {
		SimpleJsonWriter.asQueryStructure(queryStructure, path);
	}
	/**
	 * Task class used for partial/exact search with multithreading.
	 * 
	 * @author arnau
	 *
	 */
	private class Task implements Runnable {
		
		/**
		 * queryStructure
		 */
		private final Map<String, ArrayList<InvertedIndex.SingleResult>> queryStructure;
		/**
		 * invertedIndex
		 */
		private final ThreadSafeInvertedIndex invertedIndex;
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
		 * @param invertedIndex object
		 * @param queryStructure map
		 * @param line string
		 * @param exact boolean
		 */
		public Task(ThreadSafeInvertedIndex invertedIndex,Map<String, ArrayList<InvertedIndex.SingleResult>> queryStructure, String line, boolean exact) {
			this.queryStructure=queryStructure;
			this.exact=exact;
			this.line=line;
			this.invertedIndex=invertedIndex;
		}

		@Override
		public void run() {
			System.out.println("Adding to query");
			TreeSet<String> stems = TextFileStemmer.uniqueStems(line);
			String queryString = String.join(" ", stems);
			if (!stems.isEmpty() && !queryStructure.containsKey(queryString)) {
				ArrayList<InvertedIndex.SingleResult> results = invertedIndex.search(stems, exact);
				System.out.println("Found");
				synchronized (queryStructure) { 
					queryStructure.put(queryString, results);
					System.out.println("Added");
				}
			}

		}
	}

}
