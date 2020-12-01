import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface of query structure
 * 
 * @author arnau
 *
 */
public interface QueryStructureInterface {

	/**
	 * Processes the QueryStructure
	 * 
	 * @param path  passed
	 * @param exact boolean
	 * @throws IOException
	 */
	public void processQueryStructure(Path path, boolean exact) throws IOException;

	/**
	 * Prints Query to JSON
	 * 
	 * @param path passed
	 * @throws IOException exception
	 */
	public void toJson(Path path) throws IOException;

	/**
	 * Processes a Single Query result
	 * 
	 * @param line  passed
	 * @param exact passed
	 */
	public void processResult(String line, boolean exact);
}
