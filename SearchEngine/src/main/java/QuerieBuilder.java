import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class QuerieBuilder {

	/**
	 * process Querie processes the querie passed and processes results
	 * 
	 * @param querie        the querie path
	 * @param textFileIndex the inverted index structure used
	 * @throws IOException exception
	 */

	public QuerieBuilder(QuerieStructure querie, InvertedIndex invertedIndex, Path path, boolean exact)
			throws IOException {
		if (path != null && Files.isReadable(path) && Files.exists(path))
			processQuerie(querie, invertedIndex, path, exact);
		else
			System.out.print("Please input a correct querie path");

	}

	private static void processQuerie(QuerieStructure querieSearchList, InvertedIndex invertedIndex, Path path,
			boolean exact) throws IOException {

		BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);

		String line = null;
		while ((line = reader.readLine()) != null) {
			TreeSet<String> list = new TreeSet<String>();
			list = TextFileStemmer.uniqueStems(line);
			String QuerieString = String.join(" ", list);
			ArrayList<SingleQuerie> queries = new ArrayList<SingleQuerie>();
			if (list.size() > 0) {
				TreeSet<String> words = getWordsList(list, exact, invertedIndex);
				TreeSet<String> paths = getPathList(words, exact, invertedIndex);
				queries = search(words, paths, invertedIndex);
				querieSearchList.add(QuerieString, queries);
			}
			/*
			 * if (exact && list.size() != 0) { queries = exactSearch(list, invertedIndex);
			 * querieSearchList.add(QuerieString, queries); } else if (list.size() != 0) {
			 * queries = partialSearch(list, invertedIndex);
			 * querieSearchList.add(QuerieString, queries); }
			 */

		}

	}

	/**
	 * orderQuerieList by bubble sort
	 * 
	 * @param list list of SingleQuerie objects to sort based on
	 *             SingleQuerie.compareTo
	 * @return returns the ordered list
	 */
	private static ArrayList<SingleQuerie> orderQuerieList(ArrayList<SingleQuerie> list) {

		SingleQuerie temp;
		for (int j = 0; j < list.size(); j++) {

			for (int i = 0; i < list.size() - 1; i++) {
				if (list.get(i).compareTo(list.get(j)) < 0) {
					temp = list.get(i);
					list.set(i, list.get(j));
					list.set(j, temp);

				}
			}
		}

		return list;
	}
	public static TreeSet<String> getPartialPaths(TreeSet<String> querie,InvertedIndex invertedIndex) {
		Collection<String> invertedIndexWords=invertedIndex.get();
		
		TreeSet<String> foundPaths=new TreeSet<String>();
		for(String word:invertedIndexWords) {
			for(String querieWord:querie) {
				if(word.startsWith(querieWord)) {
					foundPaths.addAll(invertedIndex.get(word));
				}
			}
		}
		return foundPaths;
	}
	
	public static TreeSet<String> getPartialWords(TreeSet<String> querie, InvertedIndex invertedIndex){
		Collection<String> invertedIndexWords=invertedIndex.get();
		TreeSet<String> foundWords=new TreeSet<String>();
		for(String word:invertedIndexWords) {
			for(String querieWord:querie) {
				if(word.startsWith(querieWord)) {
					foundWords.add(word);
				}
			}
		}
		return foundWords;
	}
	private static TreeSet<String> getWordsList(TreeSet<String> querie, boolean exact,InvertedIndex invertedIndex) {
		if(exact)
			return querie;
		else return getPartialWords(querie, invertedIndex);
	}
	
	private static TreeSet<String> getPathList(TreeSet<String> words, boolean exact, InvertedIndex invertedIndex) {
		TreeSet<String> foundpaths = new TreeSet<String>();
		for (String word : words) {
			foundpaths.addAll(invertedIndex.get(word));
		}
		return foundpaths;
	}
	private static ArrayList<SingleQuerie> search(TreeSet<String> words, TreeSet<String> paths,InvertedIndex invertedIndex){
		ArrayList<SingleQuerie> querieResults = new ArrayList<SingleQuerie>();
		//TreeSet<String> foundPaths = new TreeSet<String>();
		if (words.size() < 1) {
			return querieResults;
		}
		//Iterator<String> iterator = querie.iterator();
		
		//Adds all the paths these words have to a TreeSet to not get repetition
		//foundPaths=getPartialPaths(querie, invertedIndex);
		
		Iterator<String> pathsIterator=paths.iterator();
		while(pathsIterator.hasNext()) {
			String where = pathsIterator.next();
			int count = 0;
			for (String word : words) {
				if (invertedIndex.get(word).contains(where)) {
					count += invertedIndex.get(word, where).size();
				}
			}
			double score = (double) count / (double) invertedIndex.getCount(where);
			SingleQuerie querieTemp = new SingleQuerie(where, count, score);
			querieResults.add(querieTemp);
		}
		querieResults = orderQuerieList(querieResults);
		return querieResults;
	}
	
	/*
	private static ArrayList<SingleQuerie> partialSearch(TreeSet<String> querie, InvertedIndex invertedIndex) {
		ArrayList<SingleQuerie> querieResults = new ArrayList<SingleQuerie>();
		TreeSet<String> foundPaths = new TreeSet<String>();
		if (querie.size() < 1) {
			return querieResults;
		}
		//Iterator<String> iterator = querie.iterator();
		
		//Adds all the paths these words have to a TreeSet to not get repetition
		foundPaths=getPartialPaths(querie, invertedIndex);
		
		Iterator<String> foundPathsIterator=foundPaths.iterator();
		while(foundPathsIterator.hasNext()) {
			String where = foundPathsIterator.next();
			int count = 0;
			for (String words : getPartialWords(querie, invertedIndex)) {
				if (invertedIndex.get(words).contains(where)) {
					count += invertedIndex.get(words, where).size();
				}
			}
			double score = (double) count / (double) invertedIndex.getCount(where);
			SingleQuerie querieTemp = new SingleQuerie(where, count, score);
			querieResults.add(querieTemp);
		}
		querieResults = orderQuerieList(querieResults);
		return querieResults;
	}
	/**
	 * ExactSearch method that looks into the inverted index to search for the
	 * querie.
	 * 
	 * @param querie        the querie passed to search
	 * @param invertedIndex the inverted index structure used
	 * @return the list with all the querie results ordered
	 */
	private static ArrayList<SingleQuerie> exactSearch(TreeSet<String> querie, InvertedIndex invertedIndex) {
		ArrayList<SingleQuerie> querieResults = new ArrayList<SingleQuerie>();
		TreeSet<String> foundpaths = new TreeSet<String>();
		if (querie.size() < 1) {
			return querieResults;
		}
		Iterator<String> iterator = querie.iterator();
		
		//Adds all the paths these words have to a TreeSet to not get repetition
		while(iterator.hasNext()) {
			foundpaths.addAll(invertedIndex.get(iterator.next()));
		}
		
		Iterator<String> foundPathsIterator=foundpaths.iterator();
		while(foundPathsIterator.hasNext()) {
			String where = foundPathsIterator.next();
			int count = 0;
			for (String words : querie) {
				if (invertedIndex.get(words).contains(where)) {
					count += invertedIndex.get(words, where).size();
				}
			}
			double score = (double) count / (double) invertedIndex.getCount(where);
			SingleQuerie querieTemp = new SingleQuerie(where, count, score);
			querieResults.add(querieTemp);
		}
	/*	while (iterator.hasNext()) {
			Collection<String> pathss = invertedIndex.get(iterator.next());
			for (String path : pathss) {
				if (!foundpaths.contains(path)) {
					foundpaths.add(path);
					String where = path.toString();
					int count = 0;

					for (String words : querie) {
						if (invertedIndex.get(words).contains(path)) {
							count += invertedIndex.get(words, path.toString()).size();
						}
					}
					double score = (double) count / (double) invertedIndex.getCount(path.toString());
					SingleQuerie querieTemp = new SingleQuerie(where, count, score);
					querieResults.add(querieTemp);
				}
			}
		}*/
		querieResults = orderQuerieList(querieResults);
		return querieResults;

	}
	/**
	 * This function does a partial search on the querie of words given. It uses the
	 * inverted index structure for better performance.
	 * 
	 * @param querie        the querie passed to search
	 * @param invertedIndex the inverted index structure used
	 * @return the list with all the querie results ordered.
	 */
	/*private static ArrayList<SingleQuerie> partialSearch(TreeSet<String> querie, InvertedIndex invertedIndex) {
		ArrayList<SingleQuerie> querieResults = new ArrayList<SingleQuerie>();
		TreeSet<Path> foundpaths = new TreeSet<Path>();
		if (querie.size() < 1) {
			return querieResults;
		}
		Iterator<String> iterator=querie.iterator();
		while(iterator.hasNext()) {
			Collection<Collection<Path>> pathss = invertedIndex.getPartial(iterator.next());

			for (Collection<Path> listOfWords : pathss) {

				for (Path path : listOfWords) {

					if (!foundpaths.contains(path)) {
						int count = 0;
						foundpaths.add(path);
						String where = path.toString();

						for (String words : querie) {
							Collection<String> fullWords = invertedIndex.getPartialWords(words);
							for (String fullWord : fullWords) {
								count += invertedIndex.get(fullWord, path.toString()).size();
							}

						}
						double score = (double) count / (double) invertedIndex.getCount(path.toString());
						SingleQuerie querieTemp = new SingleQuerie(where, count, score);
						querieResults.add(querieTemp);

					}

				}

			}
		}
		querieResults = orderQuerieList(querieResults);
		return querieResults;

	}*/
}
