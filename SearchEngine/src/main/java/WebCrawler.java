
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Web Crawler class 
 * @author arnau
 *
 */
public class WebCrawler {
	
	/**
	 * InvertedIndex reference
	 */
	private final InvertedIndex invertedIndex;
	
	/**
	 * Total links visited
	 */
	private final ArrayList<URL> totLinks;
	
	/**
	 * The count of total links
	 */
	private int count = 0;
	
	/**
	 * Total of links to crawl
	 */
	private final int total;
	/**
	 * The queue to use
	 */
	private final Queue<URL> queue=new LinkedList<URL>();

	
	/**
	 * WebCrawler constructor
	 * @param invertedIndex passed
	 * @param url passed
	 * @param total passed
	 */
	public WebCrawler(InvertedIndex invertedIndex, String url, int total) {
		totLinks = new ArrayList<URL>();
		this.invertedIndex = invertedIndex;
		this.total = total;
		try {
			processURL(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ProcessUrl method
	 * @param url passed
	 * @throws MalformedURLException exception
	 */
	private void processURL(URL url) throws MalformedURLException {
		queue.add(url);
		while ((count < total)&&queue.size()>0) {
			ArrayList<URL> temp= scrapper(queue.poll());
			queue.addAll(temp);
		}
	}
	
	/**
	 * Scraps url
	 * @param url passed
	 * @return the list of urls to crawl
	 */
	private ArrayList<URL> scrapper(URL url) {
		ArrayList<URL> links = new ArrayList<URL>();
		try {

			if (!totLinks.contains(url)) {
				totLinks.add(url);
				String html = HtmlFetcher.fetch(url, 3);
				if (html != null) {
					html = HtmlCleaner.stripBlockElements(html);
					for (URL x : LinkParser.getValidLinks(url, html)) {
						if (!totLinks.contains(x))
							links.add(x);
					}

					html = HtmlCleaner.stripTags(html);
					html = HtmlCleaner.stripEntities(html);
					// System.out.println(url + "\n");
					computeSingleUrl(url.toString(), html);
					count++;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return links;
	}
	
	/**
	 * ComputesingleUrl stems and adds url to invertedIndex.
	 * @param url passed
	 * @param html passed
	 * @throws IOException e
	 */
	private void computeSingleUrl(String url, String html) throws IOException {
		int i = 1;
		Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		for (String word : TextParser.parse(html)) {
			invertedIndex.add(stemmer.stem(word).toString(), url, i++);
		}

	}
	/**
	 * Task class that Builds the invertedIndex using multithreading.
	 * 
	 * @author arnau
	 *
	 */
	private static class Task implements Runnable {
		/**
		 * path
		 */
		private final String html;
		/**
		 * invertedIndex
		 */
		private final URL url;
		
		/**
		 * InvertedIndex reference
		 */
		private final InvertedIndex invertedIndex;

		/**
		 * Task constructor
		 * @param path path
		 * @param invertedIndex invertedIndex passed
		 */
		public Task(URL url, String html,InvertedIndex invertedIndex) {
			this.url = url;
			this.html = html;
			this.invertedIndex=invertedIndex;
		}

		@Override
		public void run() {
			
			/*try {			
				html = HtmlFetcher.fetch(url, 3);
				if (html != null) {
					html = HtmlCleaner.stripBlockElements(html);
					links1.addAll(LinkParser.getValidLinks(url, html));
					html = HtmlCleaner.stripHtml(html);
					System.out.println(url+"\n");
					computeSingleFile(url.toString(), html);
					count++;
					for (URL x : links1) {
						processURL(x);
					}
				}
				 
			} catch (IOException e) {
				e.printStackTrace();
			}
		*/}
	}

}
