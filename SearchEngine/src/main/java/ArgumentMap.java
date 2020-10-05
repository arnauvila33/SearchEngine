import java.nio.file.Path;

import java.util.HashMap;
import java.util.Map;

/**
 * Parses and stores command-line arguments into simple key = value pairs.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class ArgumentMap {

	/**
	 * Stores command-line arguments in key = value pairs.
	 */
	private final Map<String, String> map;

	/**
	 * Initializes this argument map.
	 */
	public ArgumentMap() { 
		this.map = new HashMap<String, String>() ;
	}

	/**
	 * Initializes this argument map and then parsers the arguments into
	 * flag/value pairs where possible. Some flags may not have associated values.
	 * If a flag is repeated, its value is overwritten.
	 *
	 * @param args the command line arguments to parse
	 */
	public ArgumentMap(String[] args) {
		// DO NOT MODIFY; THIS METHOD IS PROVIDED
		this();
		parse(args);
	}

	/**
	 * Parses the arguments into flag/value pairs where possible. Some flags may
	 * not have associated values. If a flag is repeated, its value is
	 * overwritten.
	 *
	 * @param args the command line arguments to parse
	 */
	public void parse(String[] args) {
		if(args.length==1&&isFlag(args[0]))
			map.put(args[0], null);
		for(int i=0;i<args.length;i++) {
			if(isFlag(args[i])&&i!=args.length-1) {
				if(isValue(args[i+1]))
					map.put(args[i], args[i+1]);
				else {
					map.put(args[i], null);
				}
			}
			if(i==args.length-1&&isFlag(args[i])) {
				map.put(args[i], null);
			}
		}
		//throw new UnsupportedOperationException("Not yet implemented.");
	}

	/**
	 * Determines whether the argument is a flag. Flags start with a dash "-"
	 * character, followed by at least one other non-digit character.
	 *
	 * @param arg the argument to test if its a flag
	 * @return {@code true} if the argument is a flag
	 *
	 * @see String#startsWith(String)
	 * @see String#length()
	 * @see String#charAt(int)
	 * @see Character#isDigit(char)
	 */
	
	public static boolean isFlag(String arg) {
		
		
		if(arg==null||arg.length()<2)
			return false;
		if(arg.startsWith("-")) {
			if(!Character.isDigit(arg.charAt(arg.length()-1))) {
				return true;
			}
			else
				return isFlag(arg.substring(0,arg.length()-1));
		}
				
		return false;
			
	}
	


	/**
	 * Determines whether the argument is a value. Anything that is not a flag is
	 * considered a value.
	 *
	 * @param arg the argument to test if its a value
	 * @return {@code true} if the argument is a value
	 *
	 * @see String#startsWith(String)
	 * @see String#length()
	 */
	public static boolean isValue(String arg) {
		// DO NOT MODIFY; THIS METHOD IS PROVIDED
		return !isFlag(arg);
	}

	/**
	 * Returns the number of unique flags.
	 *
	 * @return number of unique flags
	 */
	public int numFlags() {
		return map.size();
		//throw new UnsupportedOperationException("Not yet implemented.");
	}

	/**
	 * Determines whether the specified flag exists.
	 *
	 * @param flag the flag find
	 * @return {@code true} if the flag exists
	 */
	public boolean hasFlag(String flag) {
		
		if(map.containsKey(flag))
			return true;
		return false;
		//throw new UnsupportedOperationException("Not yet implemented.");
	}

	/**
	 * Determines whether the specified flag is mapped to a non-null value.
	 *
	 * @param flag the flag to find
	 * @return {@code true} if the flag is mapped to a non-null value
	 */
	public boolean hasValue(String flag) {
		
		if(hasFlag(flag)) {
			if(map.get(flag)!=null)
				return true;
			return false;
		}
		return false;
		//throw new UnsupportedOperationException("Not yet implemented.");
	}

	/**
	 * Returns the value to which the specified flag is mapped as a
	 * {@link String}, or null if there is no mapping.
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         there is no mapping
	 */
	public String getString(String flag) {
		if(hasValue(flag)) {
			return map.get(flag);
		}
		return null;
		// 
		//throw new UnsupportedOperationException("Not yet implemented.");
	}

	/**
	 * Returns the value to which the specified flag is mapped as a
	 * {@link String}, or the default value if there is no mapping.
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @param defaultValue the default value to return if there is no mapping
	 * @return the value to which the specified flag is mapped, or the default
	 *         value if there is no mapping
	 */
	public String getString(String flag, String defaultValue) {
		
		if(hasValue(flag))
			return map.get(flag);
		return defaultValue;
		//throw new UnsupportedOperationException("Not yet implemented.");
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link Path},
	 * or {@code null} if unable to retrieve this mapping (including being unable
	 * to convert the value to a {@link Path} or no value exists).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         unable to retrieve this mapping
	 *
	 * @see Path#of(String, String...)
	 */
	public Path getPath(String flag) {
		if(hasValue(flag))
			return Path.of(map.get(flag));
		return null;
		
		
	}

	/**
	 * Returns the value the specified flag is mapped as a {@link Path}, or the
	 * default value if unable to retrieve this mapping (including being unable to
	 * convert the value to a {@link Path} or if no value exists).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag the flag whose associated value will be returned
	 * @param defaultValue the default value to return if there is no valid
	 *        mapping
	 * @return the value the specified flag is mapped as a {@link Path}, or the
	 *         default value if there is no valid mapping
	 */
	public Path getPath(String flag, Path defaultValue) {
		
		if(getPath(flag)==null)
			return defaultValue;
		return getPath(flag);
		//throw new UnsupportedOperationException("Not yet implemented.");
	}

	/**
	 * Returns the value the specified flag is mapped as an int value, or the
	 * default value if unable to retrieve this mapping (including being unable to
	 * convert the value to an int or if no value exists).
	 *
	 * @param flag the flag whose associated value will be returned
	 * @param defaultValue the default value to return if there is no valid
	 *        mapping
	 * @return the value the specified flag is mapped as a int, or the default
	 *         value if there is no valid mapping
	 */
	public int getInteger(String flag, int defaultValue) {
		// 
		if(hasValue(flag)) {
			return Integer.valueOf(map.get(flag));
		}
		return defaultValue;
		//throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override
	public String toString() {
		// DO NOT MODIFY; THIS METHOD IS PROVIDED
		return this.map.toString();
	}

	/**
	 * A simple main method that parses the command-line arguments provided and
	 * prints the result to the console.
	 *
	 * @param args the command-line arguments to parse
	 */
	public static void main(String[] args) {
		// Modify as needed to debug code
		if (args.length < 1) {
			args = new String[] {"--1", "ant", "-b", "bee", "-b", "bat", "cat", "-d", "-e", "elk", "-f"};
		}
		
		var map = new ArgumentMap(args);
		System.out.println(map);
	}
}
