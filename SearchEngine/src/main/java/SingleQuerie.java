import java.util.*;

public class SingleQuerie{
	public String where;
	public int count;
	public double score;
	

	
	Map<String,Object> querie=new HashMap<String,Object>();
	
	public SingleQuerie(String wher, int coun, double scor) {
		where=wher;
		count=coun;
		score=scor;
		querie.put("where: ", where);
		querie.put("count: ",  count);
		querie.put("cvore: ", score);
	}
	

	public int compareTo(SingleQuerie list) {
		if(Double.compare(score, list.score)!=0)
			return Double.compare(score, list.score);
		if(Integer.compare(count, list.count)!=0)
			return Integer.compare(count, list.count);
		if(where.toLowerCase().compareToIgnoreCase(list.where.toLowerCase())!=0)
			return -1*where.toLowerCase().compareToIgnoreCase(list.where.toLowerCase());
		return 0;
	}
	
	public String print() {
		return where+"\n"+count+"\n"+score+"\n";
	}


	

}
