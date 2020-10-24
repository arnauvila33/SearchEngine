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
	 * @throws Exception exception
	 */
	public static void main(String[] args) throws Exception {
		// store initial start time
		Instant start = Instant.now();

		// stores the args as flags
		ArgumentMap argumentMap = new ArgumentMap(args);
		// InvertedIndex object
		InvertedIndex invertedIndex = new InvertedIndex();

		if (argumentMap.hasFlag("-path")) {
			new InvertedIndexBuilder(invertedIndex, argumentMap.getPath("-path"));
		}
		if (argumentMap.hasFlag("-index")) {
			invertedIndex.toJson(argumentMap.getPath("-index"));
		}

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
