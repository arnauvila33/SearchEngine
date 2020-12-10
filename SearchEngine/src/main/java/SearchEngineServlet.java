import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 * SearchEngineServlet class
 * 
 * @author arnau
 *
 */
public class SearchEngineServlet extends HttpServlet {

	/** Class version for serialization, in [YEAR][TERM] format (unused). */
	private static final long serialVersionUID = 202040;

	/** The logger to use for this servlet. */
	private static Logger log = Log.getRootLogger();

	/** Template for HTML. **/
	private final String htmlTemplate;

	/**
	 * Inverted Index used in the Servlet
	 */
	private ThreadSafeInvertedIndex invertedIndex;

	/**
	 * The Results for the Search Engine
	 */
	private ArrayList<String> results;

	/**
	 * SearchEngine constructor
	 * 
	 * @param invertedIndex passed to use
	 * @throws IOException thrown
	 */
	public SearchEngineServlet(ThreadSafeInvertedIndex invertedIndex) throws IOException {
		super();
		results = new ArrayList<String>();
		this.invertedIndex = invertedIndex;
		htmlTemplate = Files.readString(Path.of("html", "index.html"), StandardCharsets.UTF_8);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");

		log.info("MessageServlet ID " + this.hashCode() + " handling GET request.");

		// used to substitute values in our templates
		Map<String, String> values = new HashMap<>();
		values.put("title", "Arnau's Browser");

		// setup form
		values.put("method", "POST");
		values.put("action", request.getServletPath());

		// compile all of the messages together
		// keep in mind multiple threads may access this at once!
		values.put("messages", String.join("\n\n", results));

		// generate html from template
		StringSubstitutor replacer = new StringSubstitutor(values);
		String html = replacer.replace(htmlTemplate);

		// output generated html
		PrintWriter out = response.getWriter();
		out.println(html);
		out.flush();

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		log.info("MessageServlet ID " + this.hashCode() + " handling POST request.");

		ArrayList<String> newResults;
		Collections.synchronizedList(newResults = new ArrayList<String>());

		String message = request.getParameter("message");
		String[] descending = request.getParameterValues("descending");
		String[] exact = request.getParameterValues("exact");
		String url = request.getParameter("url");
		String max = request.getParameter("max");

		if (url != null && request.getParameter("crawl") != null) {
			@SuppressWarnings("unused")
			WebCrawler temp;
			if (max != null)
				temp = new WebCrawler(invertedIndex, url, Integer.valueOf(max), 5);
			else
				temp = new WebCrawler(invertedIndex, url, 0, 5);
		}

		message = message == null ? "" : message;

		// avoid xss attacks using apache commons text
		// comment out if you don't have this library installed
		message = StringEscapeUtils.escapeHtml4(message);

		TreeSet<String> stems = TextFileStemmer.uniqueStems(message);
		ArrayList<InvertedIndex.SingleResult> searchResults = null;
		log.info("STEMS: " + stems);
		if (!stems.isEmpty()) {

			if (exact == null)
				searchResults = invertedIndex.partialSearch(stems);
			else {
				searchResults = invertedIndex.exactSearch(stems);
			}
			synchronized (newResults) {
				for (InvertedIndex.SingleResult result : searchResults) {
					newResults.add("<a href=" + result.getWhere() + ">" + result.getWhere() + "</a><br>");
				}
			}
		}
		results = newResults;
		if (descending != null && descending.length != 0) {
			Collections.reverse(results);
			Collections.reverse(searchResults);
		}

		response.setStatus(HttpServletResponse.SC_OK);

		if (request.getParameter("lucky") != null && searchResults.size() != 0) {
			response.sendRedirect(searchResults.get(0).getWhere());
		} else
			response.sendRedirect(request.getServletPath());

	}

}
