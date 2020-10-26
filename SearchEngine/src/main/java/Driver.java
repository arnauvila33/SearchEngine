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
	 * @throws Exception exception
	 */
	public static void main(String[] args) throws Exception {
		// store initial start time
		Instant start = Instant.now();

		// stores the args as flags
		ArgumentMap argumentMap = new ArgumentMap(args);
		// InvertedIndex object
		InvertedIndex invertedIndex = new InvertedIndex();
		//QuerieStructure object 
		QuerieStructure querieStructure=new QuerieStructure();
		if (argumentMap.hasFlag("-path")) {
			new InvertedIndexBuilder(invertedIndex, argumentMap.getPath("-path"));
		}
		if (argumentMap.hasFlag("-index")) {
			invertedIndex.toJson(argumentMap.getPath("-index", Paths.get("index.json")));
		}
		if(argumentMap.hasFlag("-queries")) {
			new QuerieBuilder(querieStructure, invertedIndex, argumentMap.getPath("-queries"),argumentMap.hasFlag("-exact"));
		}
		if(argumentMap.hasFlag("-results")) {
			querieStructure.toJson(argumentMap.getPath("-results", Paths.get("results.json")));
		}
		if(argumentMap.hasFlag("-counts")) {
			invertedIndex.countToJason(argumentMap.getPath("-counts",Paths.get("counts.json")));
		}

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
