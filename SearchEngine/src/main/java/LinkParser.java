import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses URL links from the anchor tags within HTML text.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class LinkParser {

	/**
	 * Removes the fragment component of a URL (if present), and properly encodes
	 * the query string (if necessary).
	 * 
	 * @param url the url to normalize
	 * @return normalized url
	 * @throws URISyntaxException    if unable to craft new URI
	 * @throws MalformedURLException if unable to craft new URL
	 */
	public static URL normalize(URL url) throws MalformedURLException, URISyntaxException {
		return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
				url.getQuery(), null).toURL();
	}

	/**
	 * Returns a list of all the valid HTTP(S) links found in the href attribute of
	 * the anchor tags in the provided HTML. The links will be converted to absolute
	 * using the base URL and normalized (removing fragments and encoding special
	 * characters as necessary).
	 * 
	 * Any links that are unable to be properly parsed (throwing an
	 * {@link MalformedURLException}) or that do not have the HTTP/S protocol will
	 * not be included.
	 *
	 * @param base the base url used to convert relative links to absolute3
	 * @param html the raw html associated with the base url
	 * @return list of all valid http(s) links in the order they were found
	 */
	public static ArrayList<URL> getValidLinks(URL base, String html) {
		/*
		 * TODO A starter template is given below, but you do not have to use it.
		 */

		ArrayList<URL> list = new ArrayList<URL>();

		
		String regexTag = "(?i)<a\\s*(?:[^>]*?\\s*)?href\\s*=\\s*\"([^\"]*)\"";
		
		Pattern patternTag = Pattern.compile(regexTag);
		//Pattern pattern = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*)|'[^']*'|([^'\">\\s]+))");
	
		
		Matcher tagMatcher = patternTag.matcher(html);

		
		while (tagMatcher.find()) {
	
				try {
					URL absolute = new URL(base, tagMatcher.group(1));
					//System.out.println("NormalizedURL: " + normalize(absolute));
					list.add(normalize(absolute));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		}

		return list;

	}
}

