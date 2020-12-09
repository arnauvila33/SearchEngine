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
public class WebCrawler2 {

	/**
	 * Total links visited
	 */
	private final ArrayList<URL> totLinks;



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
	public WebCrawler2(ThreadSafeInvertedIndex invertedIndex, String url, int total, int threads) throws MalformedURLException {
		totLinks = new ArrayList<URL>();
		// this.invertedIndex = invertedIndex;

		queuew = new WorkQueue(threads);
		//totLinks.add(new URL(url));
		queuew.execute(new Task(new URL(url), invertedIndex, totLinks, queuew,1, total));
		queuew.join();
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
		 * invertedIndex
		 */
		private final URL url;

		/**
		 * invertedIndex
		 */
		private final ThreadSafeInvertedIndex invertedIndex;

		private final ArrayList<URL> totLinks;

		private final WorkQueue queuew;
		
		private int count;
		private final int total;

		/**
		 * Task constructor
		 * 
		 * @param url           passed
		 * @param html          passed
		 * @param invertedIndex to use
		 */
		public Task(URL url, ThreadSafeInvertedIndex invertedIndex, ArrayList<URL> totLinks, WorkQueue queuew, int count, int total) {
			this.url = url;
			this.queuew = queuew;
			this.invertedIndex = invertedIndex;
			this.totLinks=totLinks;
			this.count=count;
			this.total=total;
		}

		@Override
		public void run() {
			InvertedIndex local=new InvertedIndex();
			//ArrayList<URL> links = new ArrayList<URL>();
			System.out.println("hERE!");
			try {
				if (!totLinks.contains(url)&&count<=total) {
					
						totLinks.add(url);
					
					String html = HtmlFetcher.fetch(url, 3);
					if (html != null) {

						html = HtmlCleaner.stripBlockElements(html);

						for (URL x : LinkParser.getValidLinks(url, html)) {
							queuew.execute(new Task(x, invertedIndex, totLinks, queuew, count, total));
						}

						html = HtmlCleaner.stripTags(html);
						html = HtmlCleaner.stripEntities(html);
						computeSingleUrl(url.toString(), html, local);
						// queuew.execute(new Task(url.toString(), html, invertedIndex));
						invertedIndex.addAll(local);
						count++;
						
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}