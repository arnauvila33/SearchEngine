import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * A special type of simpleIndex that indexes the UNIQUE words that were found
 * in a text file.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class InvertedIndex {

	/** map where values are stored */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

	/** map where count is stored */
	private final TreeMap<String, Integer> countMap; 
	
	
	

	/**
	 * Constructor for Inverted Index
	 */
	public InvertedIndex() {
		invertedIndex = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
		countMap = new TreeMap<String, Integer>();

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
		boolean result = invertedIndex.get(word).get(location).add(position);

		countMap.putIfAbsent(location, 0);
		if (result) {
			countMap.replace(location, countMap.get(location) + 1);
			// https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/util/Map.html#merge(K,V,java.util.function.BiFunction)
		}

	}

	/**
	 * Determines whether the location is stored in the index.
	 *
	 * @param word the location to lookup
	 * @return {@true} if the location is stored in the index
	 */
	public boolean contains(String word) {
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
	public boolean contains(String word, String path) {
		return contains(word) && invertedIndex.get(word).containsKey(path);
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
		return contains(word, path) && invertedIndex.get(word).get(path).contains(position);
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
	 * Returns the counts of the word given
	 * 
	 * @param word to find count
	 * @return Integer
	 */
	public Integer getCount(String word) {
		return countMap.get(word);
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

	/**
	 * Prints the count to the path given as a JSON file.
	 * 
	 * @param path path to use to print
	 * @throws IOException exception
	 */
	public void countToJson(Path path) throws IOException { 
		SimpleJsonWriter.asObject(countMap, path);
	}

	@Override
	public String toString() {
		return invertedIndex.toString();
	}
	
	/**
	 * SingleQuery class
	 * @author arnau
	 *
	 */
	class SingleQuery implements Comparable<SingleQuery>{

		/**
		 * where
		 */
		public String where;
		/**
		 * count
		 */
		public int count;
		/**
		 * score
		 */
		public double score;

		/**
		 * Constructor method
		 * 
		 * @param where the path
		 * @param count the count of words
		 * @param score the score
		 */
		public SingleQuery(String where, int count, double score) {
			this.where = where;
			this.count = count;
			this.score = score;
		}

		/**
		 * CompareTo method adapted to the class
		 * 
		 * @param list the list to compare to
		 * @return The int used to sort
		 */
		@Override
		public int compareTo(SingleQuery list) {
			if (Double.compare(score, list.score) != 0)
				return Double.compare(list.score, score);
			if (Integer.compare(count, list.count) != 0)
				return Integer.compare(list.count, count);
			if (where.compareToIgnoreCase(list.where.toLowerCase()) != 0)
				return where.compareToIgnoreCase(list.where.toLowerCase());
			return 0;
		}
	}
	
	/**
	 * Search method used to build querie Structure
	 * 
	 * @param words         words passed to search
	 * @param paths         paths passed to search in
	 * @return ArrayList with the queries found
	 */
	public ArrayList<SingleQuery> search(TreeSet<String> words, TreeSet<String> paths) {
		ArrayList<SingleQuery> querieResults = new ArrayList<SingleQuery>();

		Iterator<String> pathsIterator = paths.iterator();
		while (pathsIterator.hasNext()) {
			String where = pathsIterator.next();
			int count = 0;
			for (String word : words) {
				if (get(word).contains(where)) {
					count += size(word, where);
				}
			}
			double score = (double) count / (double) getCount(where);
			SingleQuery querieTemp = new SingleQuery(where, count, score);
			querieResults.add(querieTemp);
		}
		Collections.sort(querieResults);
		return querieResults;
	}

	/**
	 * gets the word list of words to find.
	 * 
	 * @param querie        the words to find
	 * @param exact         Determines if it is exact/partial search
	 * @return the set of words.
	 */
	public TreeSet<String> getWordsList(TreeSet<String> querie, boolean exact) {
		if (exact)
			return querie;
		else
			return getPartialWords(querie);
	}

	/**
	 * Returns partial words for partial search.
	 * 
	 * @param querie        querie passed
	 * @return the set of partial words.
	 */
	private TreeSet<String> getPartialWords(TreeSet<String> querie) {
		TreeSet<String> foundWords = new TreeSet<String>();
		for (String word : get()) {
			for (String querieWord : querie) {
				if (word.startsWith(querieWord)) {
					foundWords.add(word);
				}
			}
		}
		return foundWords;
	}

	/**
	 * Returns the list of paths of the words passed.
	 * 
	 * @param words         used to find their respective paths.
	 * @return the set of paths
	 */
	public TreeSet<String> getPathList(TreeSet<String> words) {
		TreeSet<String> foundpaths = new TreeSet<String>();
		for (String word : words) {
			foundpaths.addAll(get(word));
		}
		return foundpaths;
	}
	
}
