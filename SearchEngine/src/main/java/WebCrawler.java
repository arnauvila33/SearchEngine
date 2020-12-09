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
 * 
 * @author arnau
 *
 */
public class WebCrawler {

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
	private final Queue<URL> queue = new LinkedList<URL>();

	/**
	 * Work Queue to use
	 */
	private WorkQueue queuew;

	/**
	 * WebCrawler constructor
	 * 
	 * @param invertedIndex passed
	 * @param url           passed
	 * @param total         passed
	 * @param threads       number of threads used
	 * @throws MalformedURLException
	 */
	public WebCrawler(ThreadSafeInvertedIndex invertedIndex, String url, int total, int threads)
			throws MalformedURLException {
		totLinks = new ArrayList<URL>();
		// this.invertedIndex = invertedIndex;
		this.total = total;

		processURL(new URL(url), invertedIndex, threads, totLinks);

	}

	/*
	 * public static void startCrawl(ThreadSafeInvertedIndex invertedIndex, String
	 * url, int total, int threads) throws MalformedURLException { ArrayList<URL>
	 * totLinks = new ArrayList<URL>(); Queue<URL> queue = new LinkedList<URL>();
	 * queue.add(new URL(url)); WorkQueue queuew=new WorkQueue(threads); int
	 * count=0; while(count<total && queue.size() > 0) { ArrayList<URL> temp =
	 * scrapper(queue.poll(), invertedIndex, totLinks, queuew); //queuew.execute(new
	 * Task(queue.poll().toString(), totLinks, invertedIndex)); if(temp.size()>0) {
	 * queue.addAll(temp); count++; } } queuew.join(); }
	 */

	/**
	 * ProcessUrl method
	 * 
	 * @param url           passed
	 * @param invertedIndex passed
	 * @param threads       number of threads used
	 * @throws MalformedURLException exception
	 */
	private void processURL(URL url, ThreadSafeInvertedIndex invertedIndex, int threads, ArrayList<URL> totLinks)
			throws MalformedURLException {
		queue.add(url);
		queuew = new WorkQueue(threads);
		while ((count < total) && queue.size() > 0) {
			// queue.execute(new Task(queue.poll(), invertedIndex));
			ArrayList<URL> temp = scrapper(queue.poll(), invertedIndex);
			queue.addAll(temp);

		}
		queuew.join();
	}

	/**
	 * Scraps url
	 * 
	 * @param url           passed
	 * @param invertedIndex passed
	 * @return the list of urls to crawl
	 */
	private ArrayList<URL> scrapper(URL url, ThreadSafeInvertedIndex invertedIndex) {
		ArrayList<URL> links = new ArrayList<URL>();
		try {

			if (!totLinks.contains(url)) {
				totLinks.add(url);
				String html = HtmlFetcher.fetch(url, 3);
				if (html != null) {

					html = HtmlCleaner.stripBlockElements(html);
					for (URL x : LinkParser.getValidLinks(url, html)) {
						links.add(x);
					}

					html = HtmlCleaner.stripTags(html);
					html = HtmlCleaner.stripEntities(html);
					queuew.execute(new Task(url.toString(), html, invertedIndex));
					count++;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return links;
	}

	/**
	 * Compute Single File
	 * 
	 * @param url           to use
	 * @param html          passed
	 * @param invertedIndex passed
	 * @throws IOException e
	 */
	private static void computeSingleUrl(String url, String html, InvertedIndex invertedIndex) throws IOException {
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
		private final String url;

		/**
		 * invertedIndex
		 */
		private final ThreadSafeInvertedIndex invertedIndex;

		/**
		 * Task constructor
		 * 
		 * @param url           passed
		 * @param html          passed
		 * @param invertedIndex to use
		 */
		public Task(String url, String html, ThreadSafeInvertedIndex invertedIndex) {
			this.url = url;
			this.html = html;
			this.invertedIndex = invertedIndex;
		}

		@Override
		public void run() {

			try {
				InvertedIndex local = new InvertedIndex();
				computeSingleUrl(url, html, local);
				invertedIndex.addAll(local);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}