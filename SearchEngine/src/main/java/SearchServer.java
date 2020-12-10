import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * SearchServer class to start servers.
 * 
 * @author arnau
 *
 */
public class SearchServer {
	/**
	 * Port used to start server
	 */
	public static int PORT;

	/**
	 * SearchServer Constructor method
	 * 
	 * @param port          to use
	 * @param invertedIndex used for SearchEngine
	 * @throws Exception to throw
	 */
	public SearchServer(int port, ThreadSafeInvertedIndex invertedIndex) throws Exception {
		PORT = port;
		Server server = new Server(PORT);
		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(new ServletHolder(new SearchEngineServlet(invertedIndex)), "/");
		server.setHandler(handler);
		server.start();
		server.join();
	}
}
