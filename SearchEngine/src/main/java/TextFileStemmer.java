import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for parsing and stemming text and text files into collections
 * of stemmed words.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 *
 * @see TextParser
 */
public class TextFileStemmer {
	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Returns the collection passed with cleaned and stemmed words.
	 * 
	 * @param line      the line of words to clean, split, and stem
	 * @param stemmer   the stemmer to use
	 * @param container container passed to stem
	 */
	private static void stemIntoCollection(String line, Stemmer stemmer, Collection<String> container) {
		String[] words = TextParser.parse(line);
		for (String word : words) {
			container.add(stemmer.stem(word).toString());
		}
	}

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return a list of cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static ArrayList<String> listStems(String line, Stemmer stemmer) {
		ArrayList<String> list = new ArrayList<String>();
		stemIntoCollection(line, stemmer, list);
		return list;
	}

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line
	 * using the default stemmer.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return a list of cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 * @see #listStems(String, Stemmer)
	 */
	public static ArrayList<String> listStems(String line) {
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		ArrayList<String> list = listStems(line, stemmer);
		return list;
	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from
	 * the provided line using the default stemmer.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static TreeSet<String> uniqueStems(String line) {
		TreeSet<String> set = new TreeSet<String>();
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		stemIntoCollection(line, stemmer, set);
		return set;
	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from
	 * the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static TreeSet<String> uniqueStems(String line, Stemmer stemmer) {
		TreeSet<String> set = new TreeSet<String>();
		stemIntoCollection(line, stemmer, set);
		return set;
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param inputFile the input file to parse
	 * @param list      the Collection passed to stem
	 * @throws IOException if unable to read or parse file
	 *
	 * @see #uniqueStems(String)
	 * @see TextParser#parse(String)
	 */
	public static void listStems(Path inputFile, Collection<String> list) throws IOException {
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				stemIntoCollection(line, stemmer, list);
			}
		}
	}
}