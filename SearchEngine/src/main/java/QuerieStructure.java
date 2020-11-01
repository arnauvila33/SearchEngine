import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Querie structure class that holds the queries found.
 * 
 * @author arnau
 *
 */
public class QuerieStructure {

	/**
	 * Querie sturcture to hold single queries
	 */
	private final Map<String, ArrayList<SingleQuerie>> querieStructure;

	/**
	 * Constructor
	 */
	public QuerieStructure() {
		querieStructure = new TreeMap<String, ArrayList<SingleQuerie>>();
	}

	/**
	 * Adds word and querie list to the structure
	 * 
	 * @param word    to add
	 * @param queries list to add to that word
	 */
	public void add(String word, ArrayList<SingleQuerie> queries) {
		querieStructure.put(word, queries);
	}

	/**
	 * Contains method to see if it contains a word
	 * 
	 * @param word to check if it's in the structure
	 * @return boolean
	 */
	public boolean contains(String word) {
		return querieStructure.containsKey(word);
	}

	/**
	 * 
	 * @param words  words used
	 * @param querie querie used
	 * @return boolean
	 */
	public boolean contains(String words, SingleQuerie querie) {
		return contains(words) && querieStructure.get(words).contains(querie);
	}

	/**
	 * Returns the set of words in the structure.
	 * 
	 * @return the set of words in the structure.
	 */
	public Collection<String> get() {
		return Collections.unmodifiableCollection(querieStructure.keySet());
	}

	/**
	 * Returns the list of single queries
	 * 
	 * @param words to use
	 * @return the list of single queries
	 */
	public Collection<SingleQuerie> get(String words) {
		if (contains(words)) {
			return Collections.unmodifiableCollection(querieStructure.get(words));
		}
		return Collections.emptySet();
	}

	/**
	 * Size of structure
	 * 
	 * @return int
	 */
	public int size() {
		return querieStructure.size();
	}

	/**
	 * Size of list of word
	 * 
	 * @param words to use
	 * @return int
	 */
	public int size(String words) {
		return get(words).size();
	}

	/**
	 * Prints it to JSON
	 * 
	 * @param path to print to
	 * @throws IOException exception
	 */
	public void toJson(Path path) throws IOException {
		SimpleJsonWriter.asQuerieStructure(querieStructure, path);
	}
}