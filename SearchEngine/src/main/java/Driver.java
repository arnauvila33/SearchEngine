import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

/**
 * Driver class to handle the input and start the program.
 * 
 * @author Arnau Vila
 *
 */
public class Driver {

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		
		// store initial start time
		Instant start = Instant.now();

		// stores the args as flags
		ArgumentMap argumentMap = new ArgumentMap(args);
		// InvertedIndex object
		InvertedIndex invertedIndex = null;
		// ThreadSafe InvertedIndex
		ThreadSafeInvertedIndex threadSafe = null;
		// QuerieStructure object
		QueryStructureInterface queryStructure = null;
		
	
		if (argumentMap.hasFlag("-threads")) {
			threadSafe = new ThreadSafeInvertedIndex();
			invertedIndex = threadSafe;
			MultithreadQueryStructure multiThread = null;
			try {
				multiThread = new MultithreadQueryStructure(threadSafe, argumentMap.getInteger("-threads", 5));
			} catch (Exception e) {
				System.out.println("Wrong thread input");
			}
			queryStructure = multiThread;
		} else {
			invertedIndex = new InvertedIndex();
			queryStructure = new QueryStructure(invertedIndex);
		}
		if (argumentMap.hasFlag("-url")) {
			try {
				@SuppressWarnings("unused")
				WebCrawler webCrawler = new WebCrawler(threadSafe,
					argumentMap.getString("-url"), argumentMap.getInteger("-max", 0),
					argumentMap.getInteger("-threads", 5));
			} catch (MalformedURLException e) {
				System.out.println("Unable to build crawler from url: " + argumentMap.getString("-url"));                                                                                                           
			}
		}
		else if (argumentMap.hasFlag("-Wpath")) {
			Path path = argumentMap.getPath("-path");
			try {
				if (argumentMap.hasFlag("-threads")) {
					MultithreadInvertedIndexBuilder.fillInvertedIndexMultithread(threadSafe, path,
							argumentMap.getInteger("-threads", 5));
				} else {
					InvertedIndexBuilder.fillInvertedIndex(invertedIndex, path);
				}
			} catch (Exception e) {
				System.out.println("Unable to build inverted index from path: " + path);
			}
		}
		if(argumentMap.hasFlag("-server")) {
			try {
				@SuppressWarnings("unused")
				SearchServer server=new SearchServer(argumentMap.getInteger("-server", 8080), threadSafe);
			} catch (Exception e) {
				System.out.println("Unable to start server at port: "+argumentMap.getInteger("-server", 8080));
			}
		}
		if (argumentMap.hasFlag("-queries")) {
			Path path = argumentMap.getPath("-queries");
			try {
				queryStructure.processQueryStructure(path, argumentMap.hasFlag("-exact"));
			} catch (Exception e) {
				System.out.println("Unable to build querie from path" + path);
			}

		}

		if (argumentMap.hasFlag("-index")) {
			Path path = argumentMap.getPath("-index", Path.of("index.json"));
			try {
				invertedIndex.toJson(path);
			} catch (Exception e) {
				System.out.println("Unable to write to the json file at" + path);
			}
		}

		if (argumentMap.hasFlag("-results")) {
			Path path = argumentMap.getPath("-results", Path.of("results.json"));
			try {
				queryStructure.toJson(path);
			} catch (Exception e) {
				System.out.println("Unable to write to the json file at" + path);
			}
		}

		if (argumentMap.hasFlag("-counts")) {
			Path path = argumentMap.getPath("-counts", Path.of("counts.json"));
			try {
				invertedIndex.countToJson(path);
			} catch (Exception e) {
				System.out.println("Unable to write to the json file at" + path);
			}
		}

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
