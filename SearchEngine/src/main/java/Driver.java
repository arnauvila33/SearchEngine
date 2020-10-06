import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;



/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 */
public class Driver {


	/*
	 * TODO You should throw exceptions in most places, as that makes your code
	 * more general for other developers to use.
	 *
	 *  Except... code that interacts directly with the general user should make sure
	 *  that all console output is both user friendly and informative.
	 *
	 *  "Unable to build the inverted index from path: " + path
	 *  "Unable to write the inverted index ..."
	 */

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
		
		//Used to calculate InvertedIndex
		InvertedIndex invertedIndex=new InvertedIndex();
		invertedIndex.makeInvertedIndex(args);
		
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
