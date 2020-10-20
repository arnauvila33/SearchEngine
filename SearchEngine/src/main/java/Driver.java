import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/*
TODO Configure Eclipse so you can see and address these warnings:

Description	Resource	Path	Location	Type
Map.Entry is a raw type. References to generic type Map.Entry<K,V> should be parameterized	TextFileIndex.java	/SearchEngine/src/main/java	line 111	Java Problem
Map.Entry is a raw type. References to generic type Map.Entry<K,V> should be parameterized	TextFileIndex.java	/SearchEngine/src/main/java	line 130	Java Problem
The import java.io.IOException is never used	Driver.java	/SearchEngine/src/main/java	line 1	Java Problem
The import java.nio.file.DirectoryStream is never used	Driver.java	/SearchEngine/src/main/java	line 2	Java Problem
The import java.nio.file.Files is never used	Driver.java	/SearchEngine/src/main/java	line 3	Java Problem
The import java.nio.file.Path is never used	Driver.java	/SearchEngine/src/main/java	line 4	Java Problem
The import java.nio.file.Paths is never used	Driver.java	/SearchEngine/src/main/java	line 5	Java Problem
The import java.util.ArrayList is never used	Driver.java	/SearchEngine/src/main/java	line 8	Java Problem
The import java.util.Arrays is never used	Driver.java	/SearchEngine/src/main/java	line 9	Java Problem
The import java.util.Map.Entry is never used	TextFileIndex.java	/SearchEngine/src/main/java	line 3	Java Problem
The method toString() of type TextFileIndex should be tagged with @Override since it actually overrides a superclass method	TextFileIndex.java	/SearchEngine/src/main/java	line 155	Java Problem
The value of the local variable x is not used	SimpleJsonWriter.java	/SearchEngine/src/main/java	line 45	Java Problem
Unlikely argument type Path for containsKey(Object) on a Map<String,ArrayList<Integer>>	TextFileIndex.java	/SearchEngine/src/main/java	line 97	Java Problem
Unlikely argument type Path for containsKey(Object) on a Map<String,ArrayList<Integer>>	TextFileIndex.java	/SearchEngine/src/main/java	line 144	Java Problem
Unlikely argument type Path for get(Object) on a Map<String,ArrayList<Integer>>	TextFileIndex.java	/SearchEngine/src/main/java	line 145	Java Problem

For the import warnings, configure Eclipse to remove unused imports every time you save.

 */

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

	/*
	 * TODO
	 * Create a "data structure" class that is InvertedIndex
	 * 
	 * private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> map;
	 * 
	 * - constructor
	 * - override toString
	 * - 3x size or num methods (size() or numWords())
	 * - 3x contains or has methods (contains(String word) or hasWord(String word))
	 * - 3x safe get methods (get() or getWords())
	 * - public void add(String word, String location, int position)
	 * 
	 * -------
	 * 
	 * Create a "builder" class like InvertedIndexBuilder
	 * that does the traversing of directories, etc. to create an inverted index
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
