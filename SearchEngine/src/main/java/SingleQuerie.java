import java.util.*;

/**
 * Single Querie class is the structure to hold one querie.
 * 
 * @author arnau
 *
 */
public class SingleQuerie implements Comparable<SingleQuerie> {

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
	public SingleQuerie(String where, int count, double score) {
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
	public int compareTo(SingleQuerie list) {
		if (Double.compare(score, list.score) != 0)
			return Double.compare(list.score, score);
		if (Integer.compare(count, list.count) != 0)
			return Integer.compare(list.count, count);
		if (where.toLowerCase().compareToIgnoreCase(list.where.toLowerCase()) != 0)
			return where.toLowerCase().compareToIgnoreCase(list.where.toLowerCase());
		return 0;
	}

}