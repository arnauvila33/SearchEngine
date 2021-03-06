import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

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

		if (result) {
			countMap.put(location, countMap.getOrDefault(location, 0) + 1);
		}
	}

	/**
	 * Merges two inverted indexes together
	 * 
	 * @param invertedIndex the inverted index to useto merge
	 */
	public void addAll(InvertedIndex invertedIndex) {
		for (String word : invertedIndex.invertedIndex.keySet()) {
			if (!this.invertedIndex.keySet().contains(word))
				this.invertedIndex.put(word, invertedIndex.invertedIndex.get(word));
			else {
				for (String path : invertedIndex.invertedIndex.get(word).keySet()) {
					if (!this.invertedIndex.get(word).keySet().contains(path))
						this.invertedIndex.get(word).put(path, invertedIndex.invertedIndex.get(word).get(path));
					else {
						this.invertedIndex.get(word).get(path).addAll(invertedIndex.invertedIndex.get(word).get(path));
					}
				}
			}
		}

		for (String path : invertedIndex.countMap.keySet()) {
			this.countMap.put(path, invertedIndex.countMap.getOrDefault(path, 0) + this.countMap.getOrDefault(path, 0));
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
	 * 
	 * @author arnau
	 *
	 */
	public class SingleResult implements Comparable<SingleResult> {

		/**
		 * where
		 */
		private final String where;
		/**
		 * count
		 */
		private int count;
		/**
		 * score
		 */
		private double score;

		/**
		 * Constructor method.
		 * 
		 * @param where path passed
		 */
		public SingleResult(String where) {
			this.where = where;
			this.count = 0;
			this.score = 0;
		}

		/**
		 * Method to update the score and count
		 * 
		 * @param key for invertedIndex.
		 */
		private void updateValues(String key) {
			this.count += size(key, where);
			this.score = (double) count / (double) getCount(where);
		}

		/**
		 * Get method.
		 * 
		 * @return where
		 */
		public String getWhere() {
			return where;
		}

		/**
		 * Get method.
		 * 
		 * @return count
		 */
		public int getCountt() {
			return count;
		}

		/**
		 * Get method
		 * 
		 * @return score
		 */
		public double getScore() {
			return score;
		}

		@Override
		public int compareTo(SingleResult list) {
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
	 * @param words words passed to search
	 * @param exact boolean to check partial/exact
	 * @return ArrayList with the queries found
	 */
	public ArrayList<SingleResult> search(Set<String> words, boolean exact) {
		if (exact)
			return exactSearch(words);
		else
			return partialSearch(words);
	}

	/**
	 * Exact Search of the queries passed.
	 * 
	 * @param queries Set of words
	 * @return an ArrayList of single results
	 */
	public ArrayList<SingleResult> exactSearch(Set<String> queries) {
		ArrayList<SingleResult> queryResults = new ArrayList<SingleResult>();
		Map<String, SingleResult> map = new HashMap<String, SingleResult>();
		for (String query : queries) {
			if (contains(query)) {
				makeSingleResult(map, queryResults, query);
			}
		}
		Collections.sort(queryResults);
		return queryResults;
	}

	/**
	 * Method that makes the queryResults list
	 * 
	 * @param map          map used
	 * @param queryResults the list to be made
	 * @param word         the query searched for
	 */
	public void makeSingleResult(Map<String, SingleResult> map, List<SingleResult> queryResults, String word) {
		Iterator<Map.Entry<String, TreeSet<Integer>>> iterator = invertedIndex.get(word).entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, TreeSet<Integer>> entry = iterator.next();
			String path = entry.getKey();
			if (!map.containsKey(path)) {
				SingleResult temp = new SingleResult(path);
				queryResults.add(temp);
				map.put(path, temp);
			}
			map.get(path).updateValues(word);
		}
	}

	/**
	 * Partial search of the queries passed.
	 * 
	 * @param queries Set of words
	 * @return an ArrayList of single results
	 */
	public ArrayList<SingleResult> partialSearch(Set<String> queries) {
		ArrayList<SingleResult> queryResults = new ArrayList<SingleResult>();
		System.out.println("Doing the search");
		Map<String, SingleResult> map = new HashMap<String, SingleResult>();
		for (String query : queries) {
			for (String word : invertedIndex.tailMap(query).keySet()) {
				if (!word.startsWith(query)) {
					break;
				} else {
					makeSingleResult(map, queryResults, word);
				}
			}

		}
		Collections.sort(queryResults);
		return queryResults;
	}

}
