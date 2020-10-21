import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

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
	private final Map<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

	/**
	 * Constructor for Inverted Index
	 */
	public InvertedIndex() {
		invertedIndex = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
	}

	/**
	 * Adds the location and word to map.
	 *
	 * @param location the location the word was found
	 * @param word     the word foundd
	 * @param position position
	 */
	public void add(String location, String word, int position) {
		if (invertedIndex.containsKey(word)) {
			if (invertedIndex.get(word).containsKey(location)) {
				invertedIndex.get(word).get(location).add(position);
			} else {
				TreeSet<Integer> list = new TreeSet<Integer>();
				list.add(position);
				invertedIndex.get(word).put(location, list);
			}
		} else {
			TreeMap<String, TreeSet<Integer>> treeMap = new TreeMap<String, TreeSet<Integer>>();
			TreeSet<Integer> list = new TreeSet<Integer>();
			list.add(position);
			treeMap.put(location, list);
			invertedIndex.put(word, treeMap);
		}

	}

	/**
	 * Determines whether the location is stored in the index.
	 *
	 * @param word the location to lookup
	 * @return {@true} if the location is stored in the index
	 */
	public boolean contains(String word) {
		if (invertedIndex.isEmpty())
			return false;
		return invertedIndex.containsKey(word);
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
	public boolean contains(String word, Path path) {
		if (contains(word))
			return invertedIndex.get(word).containsKey(path.toString());
		return false;
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
	public boolean contains(String word, Path path, int position) {
		if (contains(word, path))
			return invertedIndex.get(word).get(path.toString()).contains(position);
		return false;
	}

	/**
	 * Returns an unmodifiable view of the words stored in the index.
	 *
	 * @return an unmodifiable view of the locations stored in the index
	 * @see Collections#unmodifiableCollection(Collection)
	 */
	public Collection<String> get() {
		ArrayList<String> list = new ArrayList<String>();
		Iterator<Entry<String, TreeMap<String, TreeSet<Integer>>>> it = invertedIndex.entrySet().iterator();
		while (it.hasNext()) {
			list.add((String) (it.next()).getKey());
		}
		return list;
	}

	/**
	 * Returns a collection of paths for the word given.
	 *
	 * @param word the location to lookup
	 * @return an unmodifiable view of the words stored for the location
	 */
	public Collection<Path> get(String word) {
		ArrayList<Path> list = new ArrayList<Path>();
		if (contains(word)) {
			Iterator<Entry<String, TreeSet<Integer>>> iterator = invertedIndex.get(word).entrySet().iterator();
			while (iterator.hasNext())
				list.add(Paths.get((iterator.next()).getKey()));
		}
		return list;
	}

	/**
	 * Returns the positions of the word in the path give.
	 * 
	 * @param word word to look for
	 * @param path path to look for
	 * @return the list of positions
	 */
	public Collection<Integer> get(String word, Path path) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		if (contains(word, path)) {
			Iterator<Integer> iterator = invertedIndex.get(word).get(path.toString()).iterator();
			while (iterator.hasNext())
				list.add(iterator.next());
		}
		return list;
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
	public int size(String word, Path path) {
		return get(word, path).size();
	}

	/**
	 * Returns the InvertedIndex structure
	 * 
	 * @return the inverted index map
	 */
	public Map<String, TreeMap<String, TreeSet<Integer>>> getMap() {
		return Collections.unmodifiableMap(invertedIndex);
	}

	@Override
	public String toString() {
		Collection<String> words = get();
		String result = "";
		for (String word : words) {
			result += word + ": ";
			Collection<Path> paths = get(word);
			for (Path path : paths) {
				result += " " + path + ": ";
				Collection<Integer> counts = get(word, path);
				for (Integer count : counts) {
					result += " " + count + "\t";
				}
			}
			result += "\n";
		}

		return result;

	}

}
