import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

/**
 * Driver class to handle args
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
		// QuerieStructure object
		QueryStructure queryStructure = null;
		
		if(argumentMap.hasFlag("-url")) {
			invertedIndex=new ThreadSafeInvertedIndex();
			WebCrawler webCrawler=new WebCrawler((ThreadSafeInvertedIndex) invertedIndex, argumentMap.getString("-url"),argumentMap.getInteger("-max", 1),argumentMap.getInteger("-threads", 5));
		}
 
		else if (argumentMap.hasFlag("-path")) {
			Path path = argumentMap.getPath("-path");
			if (argumentMap.hasFlag("-threads")) {
				invertedIndex=new ThreadSafeInvertedIndex();
				try {
					MultithreadInvertedIndexBuilder.fillInvertedIndexMultithread((ThreadSafeInvertedIndex) invertedIndex, path,
							argumentMap.getInteger("-threads", 5));
				} catch (Exception e) {
					System.out.println("Unable to build inverted index from path: " + path);
				}
			} else {
				invertedIndex=new InvertedIndex();
				try {
					InvertedIndexBuilder.fillInvertedIndex(invertedIndex, path);
				} catch (Exception e) {
					System.out.println("Unable to build inverted index from path: " + path);
				}
			}
		}
		else invertedIndex=new InvertedIndex();

		if (argumentMap.hasFlag("-queries")) {
			Path path = argumentMap.getPath("-queries");
			if (argumentMap.hasFlag("-threads")) {
				queryStructure=new MultithreadQueryStructure((ThreadSafeInvertedIndex)invertedIndex);
				try {
					((MultithreadQueryStructure) queryStructure).processQueryMultithreading(path, argumentMap.hasFlag("-exact"),
							argumentMap.getInteger("-threads", 5));
				} catch (Exception e) {
					System.out.println("Unable to build inverted index from path: " + argumentMap.getPath("-path"));
				}
			} else {
				queryStructure=new QueryStructure(invertedIndex);
				try {
					queryStructure.processQuery(path, argumentMap.hasFlag("-exact"));
				} catch (Exception e) {
					System.out.println("Unable to build querie from path" + path);
				}
			}
		} else queryStructure=new QueryStructure(invertedIndex);
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
