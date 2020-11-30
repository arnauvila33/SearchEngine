import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface of query structure
 * @author arnau
 *
 */
public interface QueryStructureInterface {
	
	/**
	 * Processes the QueryStructure
	 * @param path passed
	 * @param exact boolean
	 */
	public void processQueryStructure(Path path, boolean exact);
	
	/**
	 * Prints Query to JSON
	 * @param path passed
	 * @throws IOException exception
	 */
	public void toJson(Path path) throws IOException;
}