import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

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
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asArray(Collection<Integer> elements, Writer writer, int level) throws IOException {
		
		
		writer.write("[");
		writer.write("\n");
		Iterator<Integer> it=elements.iterator();
		for(Integer x:elements) {
			indent(it.next(),writer,level+1);
			if(it.hasNext())
				writer.write(",");
			writer.write("\n");
			
		}
		indent(writer,level);
		writer.write("]");
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asObject(Map<String, Integer> elements, Writer writer, int level) throws IOException {
		
		indent(writer,level);
		writer.write("{");
		int c=0;
		for(Map.Entry<String, Integer> entry : elements.entrySet()) {
			c++;
			writeEntry(entry, writer, 1);
			if(c!=elements.size())
				writer.write(",");
			
		}
		writer.write("\n");
		writer.write("}");
		
	}
	
	/**
	 * Writes invertedIndex in pretty json format
	 * @param m map passed
	 * @param writer used
	 * @param level to indent
	 * @throws IOException
	 */
	public static void invertedIndex(Map<String, TreeMap<String, ArrayList<Integer>>> m, Writer writer, int level)throws IOException {
		indent(writer,level);
		writer.write("{");
		int c=0;
		int c1=0;
		writer.write("\n");
		for(Entry<String, TreeMap<String, ArrayList<Integer>>> entry: m.entrySet()) {
			c++;
			indent(entry.getKey(),writer,level+1);
			writer.write(": {");
			//asArray(entry.getValue(), writer, level+1);
			
			writer.write("\n");
			for(Entry<String, ArrayList<Integer>> ent: entry.getValue().entrySet()) {
				c1++;
				indent(ent.getKey(),writer,level+2);
				writer.write(": ");
				asArray(ent.getValue(),writer,level+2);
				if(c1!=entry.getValue().size())
					writer.write(",");
				writer.write("\n");				
			}
			indent(writer,level+1);
			writer.write("}");
			if(c!=m.size())
				writer.write(",");
			writer.write("\n");
			
			
			c1=0;
			
			
		}
		
		writer.write("}");
	}
	/**
	 * Writes the elements as a pretty JSON object with a nested array. The
	 * generic notation used allows this method to be used for any type of map
	 * with any type of nested collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level)
			throws IOException {
		indent(writer,level);
		writer.write("{");
		int c=0;
		writer.write("\n");
		for(Entry<String, ? extends Collection<Integer>> entry: elements.entrySet()) {
			c++;
			indent(entry.getKey(),writer,level+1);
			writer.write(": ");
			asArray(entry.getValue(), writer, level+1);
			if(c!=elements.size())
				writer.write(",");
			writer.write("\n");
			
			
		}
		
		writer.write("}");

		/*
		 * 
		 *
		 * The parameter syntax for elements looks like:
		 *
		 * Map<String, ? extends Collection<Integer>> elements
		 *
		 * The syntax above makes this method directly useful for your project.
		 * However, you may not know how to interpret this syntax yet. It behaves
		 * as if it were this instead:
		 *
		 * HashMap<String, HashSet<Integer>> elements
		 *
		 * You may want to use the "var" keyword here to make dealing with the syntax
		 * a little bit easier.
		 */

		
	}

	/*
	 * Todo: You are encouraged to include helper methods below. Here are a few
	 * that you might find useful. Consider adding others as well.
	 */

	/**
	 * Indents using a tab character by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times the number of times to write a tab symbol
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
	 * @param writer the writer to use
	 * @param times the number of times to indent
	 * @throws IOException if an IO error occurs
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(Integer element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write(element.toString());
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "}
	 * quotation marks.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param times the number of times to indent
	 * @throws IOException if an IO error occurs
	 *
	 * @see #indent(Writer, int)
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
	 * @param entry the nested entry to write
	 * @param writer the writer to use
	 * @param level the initial indentation level
	 * @throws IOException if an IO error occurs
	 */
	public static void writeEntry(Entry<String, Integer> entry, Writer writer, int level) throws IOException {
		writer.write('\n');
		indent(entry.getKey(), writer, level);
		writer.write(": ");
		writer.write(entry.getValue().toString());
	}

	
	
	
	/*
	 * These methods are provided for you. No changes are required.
	 */

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static void asArray(Collection<Integer> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static String asArray(Collection<Integer> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static void asObject(Map<String, Integer> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static String asObject(Map<String, Integer> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static String asNestedArray(Map<String, ? extends Collection<Integer>> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asNestedArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Returns inverted index in a pretty json format to string
	 * @param elements map
	 * @return string
	 */
	public static String asinvertedIndex(Map<String, TreeMap<String, ArrayList<Integer>>> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			invertedIndex(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
	/**
	 * Writes the inverted index in a pretty json format to a file
	 * @param elements map
	 * @param path used
	 * @throws IOException
	 */
	public static void asinvertedIndex(Map<String, TreeMap<String, ArrayList<Integer>>> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			invertedIndex(elements, writer, 0);
		}
	}

	/**
	 * A simple main method that demonstrates this class.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) {
		// MODIFY AS NECESSARY TO DEBUG YOUR CODE

		TreeSet<Integer> elements = new TreeSet<>();
		System.out.println("Empty:");
		System.out.println(asArray(elements));

		elements.add(65);
		System.out.println("\nSingle:");
		System.out.println(asArray(elements));

		elements.add(66);
		elements.add(67);
		System.out.println("\nSimple:");
		System.out.println(asArray(elements));
	}
}
