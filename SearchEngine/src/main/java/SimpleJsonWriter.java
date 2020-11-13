import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using tabs.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class SimpleJsonWriter {

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asArray(Collection<Integer> elements, Writer writer, int level) throws IOException {
		writer.write("[");
		Iterator<Integer> it = elements.iterator();
		if (it.hasNext()) {
			writer.write("\n");
			indent(it.next(), writer, level + 1);
		}
		while (it.hasNext()) {
			writer.write(",\n");
			indent(it.next(), writer, level + 1);
		}
		writer.write("\n");
		indent(writer, level);
		writer.write("]");
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asObject(Map<String, Integer> elements, Writer writer, int level) throws IOException {

		indent(writer, level);
		writer.write("{");
		Iterator<Entry<String, Integer>> iterator = elements.entrySet().iterator();
		if (iterator.hasNext()) {
			writeEntry(iterator.next(), writer, level + 1);
		}
		while (iterator.hasNext()) {
			writer.write(",");
			writeEntry(iterator.next(), writer, level + 1);
		}
		writer.write("\n}");

	}

	/**
	 * Writes the elements as a pretty JSON object with a nested array. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level)
			throws IOException {
		writer.write("{");
		indent(writer, level);
		var iterator = elements.entrySet().iterator();
		Entry<String, ? extends Collection<Integer>> entry;
		if (iterator.hasNext()) {
			writer.write("\n");
			entry = iterator.next();
			indent(entry.getKey(), writer, level + 1);
			writer.write(": ");
			asArray(entry.getValue(), writer, level + 1);
		}
		while (iterator.hasNext()) {
			writer.write(",\n");
			entry = iterator.next();
			indent(entry.getKey(), writer, level + 1);
			writer.write(": ");
			asArray(entry.getValue(), writer, level + 1);
		}
		writer.write("\n}");
	}

	/**
	 * Writes invertedIndex in pretty json format.
	 * 
	 * @param invertedIndex map passed
	 * @param writer        used to write in file.
	 * @param level         to indent
	 * @throws IOException exception
	 */
	public static void asInvertedIndex(Map<String, ? extends Map<String, ? extends Collection<Integer>>> invertedIndex,
			Writer writer, int level) throws IOException {
		indent(writer, level);
		writer.write("{");
		var iterator = invertedIndex.entrySet().iterator();
		Entry<String, ? extends Map<String, ? extends Collection<Integer>>> entry;
		if (iterator.hasNext()) {
			writer.write("\n");
			entry = iterator.next();
			indent(entry.getKey(), writer, level + 1);
			writer.write(": ");
			asNestedArray(entry.getValue(), writer, level + 1);
		}
		while (iterator.hasNext()) {
			writer.write(",\n");
			entry = iterator.next();
			indent(entry.getKey(), writer, level + 1);
			writer.write(": ");
			asNestedArray(entry.getValue(), writer, level + 1);
		}

		writer.write("\n}");
	}

	/**
	 * Writes the Query search as a pretty JSON file
	 * 
	 * @param <SingleQuery> class
	 * @param elements      the data structure that holds the querie results
	 * @param writer        the writer used
	 * @param level         the indent level
	 * @throws IOException exception
	 *
	 */
	public static <SingleQuery> void asQueryStructure(
			Map<String, ? extends Collection<InvertedIndex.SingleResult>> elements, Writer writer, int level)
			throws IOException {
		indent(writer, level);
		writer.write("{");
		writer.write("\n");
		var iterator = elements.entrySet().iterator();
		Entry<String, ? extends Collection<InvertedIndex.SingleResult>> entry;
		if (iterator.hasNext()) {
			entry = iterator.next();
			indent(entry.getKey(), writer, level + 1);
			writer.write(": ");
			asArrayQuery(entry.getValue(), writer, level + 1);
		}
		while (iterator.hasNext()) {
			entry = iterator.next();
			writer.write(",");
			writer.write("\n");
			indent(entry.getKey(), writer, level + 1);
			writer.write(": ");
			asArrayQuery(entry.getValue(), writer, level + 1);
		}

		writer.write("\n}");
	}

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asArrayQuery(Collection<InvertedIndex.SingleResult> elements, Writer writer, int level)
			throws IOException {

		writer.write("[");
		Iterator<InvertedIndex.SingleResult> iterator = elements.iterator();
		if (iterator.hasNext()) {
			asQuery(iterator.next(), writer, level);
		}
		while (iterator.hasNext()) {
			writer.write(",");
			asQuery(iterator.next(), writer, level);
		}
		writer.write("\n");
		indent(writer, level);
		writer.write("]");
	}

	/**
	 * Writes the querie as a pretty json file
	 * 
	 * @param querie the querie object to write
	 * @param writer the writer used
	 * @param level  the indent level passed
	 * @throws IOException exception
	 */
	public static void asQuery(InvertedIndex.SingleResult querie, Writer writer, int level) throws IOException {
		writer.write("\n");
		indent(writer, level + 1);
		writer.write("{\n");
		indent("where", writer, level + 2);
		writer.write(": ");
		writer.write('"' + querie.getWhere() + '"');
		writer.write(",\n");
		indent("count", writer, level + 2);
		writer.write(": ");
		writer.write(String.valueOf(querie.getCount()));
		writer.write(",\n");
		indent("score", writer, level + 2);
		writer.write(": ");
		writer.write(String.format("%.8f", querie.getScore()));
		writer.write("\n");
		indent(writer, level + 1);
		writer.write("}");
	}

	/**
	 * Indents using a tab character by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * @throws IOException if an IO error occurs
	 */
	public static void indent(Writer writer, int times) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Indents and then writes the integer element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void indent(Integer element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write(element.toString());
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Writes a map entry in pretty JSON format.
	 *
	 * @param entry  the nested entry to write
	 * @param writer the writer to use
	 * @param level  the initial indentation level
	 * @throws IOException if an IO error occurs
	 */
	public static void writeEntry(Entry<String, Integer> entry, Writer writer, int level) throws IOException {
		writer.write('\n');
		indent(entry.getKey(), writer, level);
		writer.write(": ");
		writer.write(entry.getValue().toString());
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 */
	public static void asArray(Collection<Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 */
	public static String asArray(Collection<Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 */
	public static void asObject(Map<String, Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 */
	public static String asObject(Map<String, Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 */
	public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 */
	public static String asNestedArray(Map<String, ? extends Collection<Integer>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Returns inverted index in a pretty json format to string
	 * 
	 * @param map Inverted Index passed
	 * @return string
	 */
	public static String asinvertedIndex(Map<String, ? extends Map<String, ? extends Collection<Integer>>> map) {
		try {
			StringWriter writer = new StringWriter();
			asInvertedIndex(map, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the inverted index in a pretty json format to a file
	 * 
	 * @param map  Inverted Index passed
	 * @param path used
	 * @throws IOException exception
	 */
	public static void asinvertedIndex(Map<String, ? extends Map<String, ? extends Collection<Integer>>> map, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asInvertedIndex(map, writer, 0);
		}
	}

	/**
	 * Returns inverted index in a pretty json format to string
	 * 
	 * @param elements map
	 * @return string
	 */
	public static String asQueryStructure(Map<String, ? extends Collection<InvertedIndex.SingleResult>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asQueryStructure(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the inverted index in a pretty json format to a file
	 * 
	 * @param elements map
	 * @param path     used
	 * @throws IOException exception
	 */
	public static void asQueryStructure(Map<String, ? extends Collection<InvertedIndex.SingleResult>> elements,
			Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asQueryStructure(elements, writer, 0);
		}
	}

}
