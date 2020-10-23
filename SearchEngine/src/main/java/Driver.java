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

		// Used to calculate InvertedIndex
		InvertedIndexBuilder invertedIndexBuilder = new InvertedIndexBuilder();
		invertedIndexBuilder.makeInvertedIndex(args);
		
		/*
		 * TODO The args should never leave Driver. 
		 * 
		 * ArgumentMap map = ...
		 * InvertedIndex index = ...
		 * 
		 * if (map.has the path flag) {
		 * 		build
		 * }
		 * 
		 * if (map has the index flag) {
		 * 		write
		 * }
		 */

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
