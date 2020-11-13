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

		// TODO Simplify below, no need to have both putIfAbsent and getOrDefault and replace
		// TODO Can basically have 1-2 methods total inside the if, none outside
		countMap.putIfAbsent(location, 0);
		if (result) {
			countMap.replace(location, countMap.get(location) + 1);
			countMap.getOrDefault(location, countMap.get(location) + 1);
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
	class SingleResult implements Comparable<SingleResult> { // TODO public

		/**
		 * where
		 */
		private String where;
		/**
		 * count
		 */
		private int count;
		/**
		 * score
		 */
		private double score;

		/**
		 * Constructor method
		 * 
		 * @param where the path
		 * @param count the count of words
		 * @param score the score
		 */
		public SingleResult(String where, int count, double score) {
			this.where = where;
			this.count = count;
			this.score = score;
		}
		
		/* TODO Create 1 method that updates values for you. Then, don't require
		 * those values in the constructor. So change to this:
		public SingleResult(String where) {
			this.where = where;
			this.count = 0;
			this.score = 0;
		}

		private void updateValues(String key) {
		    this.count += ... access invertedIndex directly
		    this.score = ... access countMap directly
		}
		 */


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
		public int getCount() {
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
		ArrayList<SingleResult> querieResults = new ArrayList<SingleResult>(); // TODO Rename to queryResults or results
		/*
		 * TODO 
		 * Choose a better, more efficient map implementation.
		 * Don't store String (location) to Integer (count). Store the actual
		 * SingleResult object instead, update the count stored by that object.
		 * Allows you to eliminate the loop creating these objects below.
		 * 
		 * Map<String, Integer> map --> Map<String, SingleResult> map
		 */
		Map<String, Integer> map = new TreeMap<String, Integer>(); 
		for (String query : queries) {
			if (contains(query)) {
				Iterator<Map.Entry<String, TreeSet<Integer>>> iterator = invertedIndex.get(query).entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, TreeSet<Integer>> entry = iterator.next();
					
					/*
					 * TODO Change this to check if you need to create a new result. If so
					 * create one and add to both the map and the queryResults list.
					 * 
					 * Then call the new updateValues(...) method to set the count/score of
					 * the stored result object.
					 */
					map.putIfAbsent(entry.getKey(), 0);
					int sum = map.get(entry.getKey()) + size(query, entry.getKey());
					map.replace(entry.getKey(), sum);
				}
			}
		}
		
		// TODO Remove this loop, integrate this work into the loop above
		for (Entry<String, Integer> entry : map.entrySet()) {
			double score = (double) entry.getValue() / (double) getCount(entry.getKey());
			SingleResult querieTemp = new SingleResult(entry.getKey(), entry.getValue(), score);
			querieResults.add(querieTemp);
		}
		Collections.sort(querieResults);
		return querieResults;
	}

	/**
	 * Partial search of the queries passed.
	 * 
	 * @param queries Set of words
	 * @return an ArrayList of single results
	 */
	public ArrayList<SingleResult> partialSearch(Set<String> queries) {
		// TODO See exactSearch comments
		ArrayList<SingleResult> queryResults = new ArrayList<SingleResult>();
		Map<String, Integer> map = new TreeMap<String, Integer>();
		for (String query : queries) {
			/*
			 * TODO This is doing a linear search for a consecutive chunk of elements.
			 * We fix these types of linear searches differently. Here, the key
			 * observation to make is that our data is sorted. Anytime we have sorted
			 * data, we can do something like a binary search to speed things up. In
			 * this case, we don't need to explicitly do a binary search---this kind
			 * of functionality is built into tree data structures. Look at this
			 * lecture example:
			 *
			 * https://github.com/usf-cs212-fall2020/lectures/blob/87a9175b8b45b077e0845bee90d90a63ef5d8b3b/DataStructures/src/main/java/FindDemo.java#L145-L163
			 *
			 * You can take a similar approach using TreeMaps too! If you aren't sure
			 * how to adapt this for partial search, reach out on Piazza!
			 */
			for (String word : invertedIndex.keySet()) {
				if (word.startsWith(query)) {
					Iterator<Map.Entry<String, TreeSet<Integer>>> iterator = invertedIndex.get(word).entrySet()
							.iterator();
					while (iterator.hasNext()) {
						Entry<String, TreeSet<Integer>> entry = iterator.next();
						map.putIfAbsent(entry.getKey(), 0);
						int sum = map.get(entry.getKey()) + size(word, entry.getKey());
						map.replace(entry.getKey(), sum);
					}
				}
			}

		}
		for (Entry<String, Integer> entry : map.entrySet()) {
			double score = (double) entry.getValue() / (double) getCount(entry.getKey());
			SingleResult temp = new SingleResult(entry.getKey(), entry.getValue(), score);
			queryResults.add(temp);
		}
		Collections.sort(queryResults);
		return queryResults;
	}

}
