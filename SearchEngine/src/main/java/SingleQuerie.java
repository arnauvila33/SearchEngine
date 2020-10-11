import java.util.*;

public class SingleQuerie{
	public String where;
	public int count;
	public double score;
	

	
	Map<String,Object> querie=new HashMap<String,Object>();
	
	/**
	 * Constructor method 
	 * @param where the path
	 * @param count the count of words
	 * @param score the score 
	 */
	public SingleQuerie(String where, int count, double score) {
		this.where=where;
		this.count=count;
		this.score=score;
		querie.put("where: ", where);
		querie.put("count: ",  count);
		querie.put("cvore: ", score);
	}
	
	/**
	 * CompareTo method adapted to the class
	 * @param list the list to compare to
	 * @return The int used to sort
	 */
	public int compareTo(SingleQuerie list) {
		if(Double.compare(score, list.score)!=0)
			return Double.compare(score, list.score);
		if(Integer.compare(count, list.count)!=0)
			return Integer.compare(count, list.count);
		if(where.toLowerCase().compareToIgnoreCase(list.where.toLowerCase())!=0)
			return -1*where.toLowerCase().compareToIgnoreCase(list.where.toLowerCase());
		return 0;
	}
	


	

}
