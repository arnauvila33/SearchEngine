import java.nio.file.Path;
import java.nio.file.Paths;
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
		InvertedIndex invertedIndex = new InvertedIndex();
		// QuerieStructure object
		QueryStructure queryStructure=new QueryStructure();

		if (argumentMap.hasFlag("-path")) {
			try {
				InvertedIndexBuilder.fillInvertedIndex(invertedIndex, argumentMap.getPath("-path"));
			} catch (Exception e) {
				System.out.println("Unable to build inverted index from path: " + argumentMap.getPath("-path"));
			}
		}
		
		if (argumentMap.hasFlag("-index")) {
			Path path = argumentMap.getPath("-index", Paths.get("index.json"));
			try {
				invertedIndex.toJson(path);
			} catch (Exception e) {
				System.out.println("Unable to write to the json file at" + path);
			}
		}
		
		if (argumentMap.hasFlag("-queries")) {
			try {
				queryStructure.processQuerie(invertedIndex, argumentMap.getPath("-queries"),
						argumentMap.hasFlag("-exact"));
			} catch (Exception e) {
				System.out.println("Unable to build querie from path" + argumentMap.getPath("-queries"));
			}
		}
		
		if (argumentMap.hasFlag("-results")) {
			Path path = argumentMap.getPath("-results", Paths.get("results.json"));
			try {
				queryStructure.toJson(path);
			} catch (Exception e) {
				System.out.println("Unable to write to the json file at" + path);
			}
		}
		
		if (argumentMap.hasFlag("-counts")) {
			Path path = argumentMap.getPath("-counts", Paths.get("counts.json"));
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
