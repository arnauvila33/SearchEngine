import java.util.*;

/**
 * Single Querie class is the structure to hold one querie.
 * 
 * @author arnau
 *
 */
public class SingleQuerie implements Comparable<SingleQuerie> {

	// TODO Improve encapsulation (make members private)
	
	/*
	 * TODO Whenever you have a class like this where all of the data depends on a specific
	 * instance of another object (in this case the inverted index), it often makes sense
	 * to nest that class inside as a non-static inner class.

	 * Specifically, if we nest this search result class inside of your inverted index,
	 * it will make this relationship  between the two explicit, better encapsulate the
	 * search result members used for sorting, and can even simplify your search code
	 * later on. 
	 */	
	
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
	@Override // TODO Always use this annocation
	public int compareTo(SingleQuerie list) {
		if (Double.compare(score, list.score) != 0)
			return Double.compare(list.score, score);
		if (Integer.compare(count, list.count) != 0)
			return Integer.compare(list.count, count);
		// TODO Don't have to convert to lowercase
		if (where.toLowerCase().compareToIgnoreCase(list.where.toLowerCase()) != 0)
			return where.toLowerCase().compareToIgnoreCase(list.where.toLowerCase());
		return 0;
	}

}