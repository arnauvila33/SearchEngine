import java.nio.file.Path;
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
public class TextFileIndex{
	
	/** map where values are stored */
	Map<String, TreeMap<String, ArrayList<Integer>>> map=new TreeMap<String ,TreeMap<String, ArrayList<Integer>>>();
	
	
	
	/**
	 * Adds the location and word.
	 *
	 * @param location the location the word was found
	 * @param word the word found
	 */
	public void add(String location, String word,int pos) {
		if(map.containsKey(word)) {
			if(map.get(word).containsKey(location)) {
				map.get(word).get(location).add(pos);
			}
			else {
				ArrayList<Integer> ls=new ArrayList<Integer>();
				ls.add(pos);
				map.get(word).put(location, ls);
			}
		}
		else {
			TreeMap<String, ArrayList<Integer>> map1=new TreeMap<String, ArrayList<Integer>>();
			ArrayList<Integer> ls=new ArrayList<Integer>();
			ls.add(pos);
			map1.put(location, ls);
			map.put(word, map1);
		}
		
	}

	/**
	 * Returns the number of paths stored for the given word.
	 *
	 * @param location the location to lookup
	 * @return 0 if the location is not in the index or has no words, otherwise
	 *         the number of words stored for that element
	 */
	public int size(String location) {
		if(map.containsKey(location))
			return map.get(location).size();
		return 0;
	}

	/**
	 * Returns the number of words stored in the index.
	 *
	 * @return 0 if the index is empty, otherwise the number of locations in the
	 *         index
	 */
	public int size() {
		return map.size();
		
	}

	/**
	 * Determines whether the location is stored in the index.
	 *
	 * @param location the location to lookup
	 * @return {@true} if the location is stored in the index
	 */
	public boolean contains(String location) {
		if(map.isEmpty())
			return false;
		return map.containsKey(location);
	}

	/**
	 * Determines whether the location is stored in the index and the word is
	 * stored for that location.
	 *
	 * @param location the location to lookup
	 * @param word the word in that location to lookup
	 * @return {@true} if the location and word is stored in the index
	 */
	public boolean contains(String location, Path path) {
		if(contains(location))
			return map.get(location).containsKey(path);
		return false;
	}

	/**
	 * Returns an unmodifiable view of the words stored in the index.
	 *
	 * @return an unmodifiable view of the locations stored in the index
	 * @see Collections#unmodifiableCollection(Collection)
	 */
	public Collection<String> get() {
		ArrayList<String> list=new ArrayList<String>();
		Iterator<?> it=map.entrySet().iterator();
		while(it.hasNext()) {
			list.add((String)((Map.Entry)it.next()).getKey());
		}
		return list;
	}

	/**
	 * Returns an unmodifiable view of the words stored in the index for the
	 * provided location, or an empty collection if the location is not in the
	 * index.
	 *
	 * @param location the location to lookup
	 * @return an unmodifiable view of the words stored for the location
	 */
	public Collection<Path> get(String location) {
		HashSet<Path> set=new HashSet<Path>();
		
		if(map.containsKey(location)) {
			Iterator<?> it=map.get(location).entrySet().iterator();
			while(it.hasNext())
				set.add((Path)((Map.Entry)it.next()).getKey());
		}		
		return set;
	}
	
	/**
	 * Returns the path of the word.
	 * @param location word
	 * @param loc path
	 * @return the listx`
	 */
	public Collection<Integer> get(String location, Path loc){
		ArrayList<Integer> l=new ArrayList<Integer>();
		if(map.containsKey(location)) {
			if(map.get(location).containsKey(loc)) {
				return map.get(location).get(loc);					
			}
		}
		return l;
	}
	
	/**
	 * toString modification
	 * 
	 */
	public String toString() {
		Collection<String> path=get();
		String res="";
		for(String x:path) {
			res+=x+": ";
			Collection<Path> st=get(x);
			for(Path y:st) {
				res+=" "+y+": ";
				Collection<Integer> co=get(x,y);
				for(Integer z:co) {
					res+=" "+z+"\t";
				}
			}
			res+="\n";
		}
		
		return res;
		
	}
	




}
