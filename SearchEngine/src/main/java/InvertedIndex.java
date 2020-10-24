import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * A special type of {@link SimpleIndex} that indexes the UNIQUE words that were
 * found in a text file.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class InvertedIndex {

	/** map where values are stored */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

	/**
	 * Constructor for Inverted Index
	 */
	public InvertedIndex() {
		invertedIndex = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
	}

	/**
	 * Adds the location and word to map.
	 * 
	 * @param word     the word foundd
	 * @param location the location the word was found
	 * @param position position
	 */
	public void add(String word, String location, int position) {
		invertedIndex.putIfAbsent(word, new TreeMap<>());
		invertedIndex.get(word).putIfAbsent(location, new TreeSet<>());
		invertedIndex.get(word).get(location).add(position);
	}

	/**
	 * Determines whether the location is stored in the index.
	 *
	 * @param word the location to lookup
	 * @return {@true} if the location is stored in the index
	 */
	public boolean contains(String word) {
		return !invertedIndex.isEmpty() && invertedIndex.containsKey(word);

	}

	/**
	 * Determines whether the word is stored in the index and the path is stored for
	 * that word.
	 *
	 * @param word the location to lookup
	 * @param path the word in that location to lookup
	 * 
	 * @return {@true} if the location and word is stored in the index
	 */
	public boolean contains(String word, String path) {
		return contains(word) && invertedIndex.get(word).containsKey(path.toString());
	}

	/**
	 * Determines whether the word is stored in the index, and the path is stored in
	 * that word, and that the position is in that path.
	 * 
	 * @param word     to look
	 * @param path     to look
	 * @param position to look
	 * @return {@true} if the location, word, and position is stored in the index
	 */
	public boolean contains(String word, String path, int position) {
		return contains(word, path) && invertedIndex.get(word).get(path.toString()).contains(position);
	}

	/**
	 * Returns an unmodifiable view of the words stored in the index.
	 *
	 * @return an unmodifiable view of the locations stored in the index
	 * @see Collections#unmodifiableCollection(Collection)
	 */
	public Collection<String> get() {
		return Collections.unmodifiableCollection(invertedIndex.keySet());
	}

	/**
	 * Returns a collection of paths for the word given.
	 *
	 * @param word the location to lookup
	 * @return an unmodifiable view of the words stored for the location
	 */
	public Collection<String> get(String word) {
		if (contains(word))
			return Collections.unmodifiableCollection(invertedIndex.get(word).keySet());
		else
			return Collections.emptySet();
	}

	/**
	 * Returns the positions of the word in the path give.
	 * 
	 * @param word word to look for
	 * @param path path to look for
	 * @return the list of positions
	 */
	public Collection<Integer> get(String word, String path) {
		if (contains(word, path)) {
			return Collections.unmodifiableCollection(invertedIndex.get(word).get(path));
		} else
			return Collections.emptySet();
	}

	/**
	 * Returns the number of words stored in the index.
	 *
	 * @return 0 if the index is empty, otherwise the number of locations in the
	 *         index
	 */
	public int size() {
		return invertedIndex.size();
	}

	/**
	 * Returns the number of paths stored for the given word.
	 *
	 * @param word the location to lookup
	 * @return 0 if the location is not in the index or has no words, otherwise the
	 *         number of words stored for that element
	 */
	public int size(String word) {
		return get(word).size();
	}

	/**
	 * Returns the number of positions in the path given for the word given.
	 * 
	 * @param word word to use to search
	 * @param path to use to search
	 * @return 0 if word or path are not present, or the actual size
	 */
	public int size(String word, String path) {
		return get(word, path).size();
	}

	/**
	 * Prints the invertedIndex to the path given
	 * 
	 * @param path the path to print the invertedIndex to
	 * @throws IOException exception
	 */
	public void toJson(Path path) throws IOException {
			SimpleJsonWriter.asinvertedIndex(invertedIndex, path);
	}

	@Override
	public String toString() {
		return invertedIndex.toString();
	}

}
