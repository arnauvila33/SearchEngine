
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
	//private final ThreadSafeInvertedIndex invertedIndex;
	
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
	
	private WorkQueue queuew;
	
	/*
	public static void buildIndexCrawler(ThreadSafeInvertedIndex invertedIndex, String url, int total, int threads) {
		int count=0;
		Queue<URL> URLqueue=new LinkedList<URL>();
		WorkQueue workQueue=new WorkQueue(threads);
		while(count<total) {
			workQueue.execute(new Task(processURL(URLqueue.poll(), invertedIndex)));
			count++;
		}
	}*/
	
	/**
	 * WebCrawler constructor
	 * @param invertedIndex passed
	 * @param url passed
	 * @param total passed
	 */
	public WebCrawler(ThreadSafeInvertedIndex invertedIndex, String url, int total, int threads) {
		totLinks = new ArrayList<URL>();
		//this.invertedIndex = invertedIndex;
		this.total = total;
		try {
			processURL(new URL(url), invertedIndex, threads);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ProcessUrl method
	 * @param url passed
	 * @throws MalformedURLException exception
	 */
	private void processURL(URL url, ThreadSafeInvertedIndex invertedIndex, int threads) throws MalformedURLException {
		queue.add(url);
		queuew = new WorkQueue(threads);
		while ((count < total)&&queue.size()>0) {
			//queue.execute(new Task(queue.poll(), invertedIndex));
			ArrayList<URL> temp= scrapper(queue.poll(), invertedIndex);
			queue.addAll(temp);
			count++;
		}
		queuew.join();
	}
	
	/**
	 * Scraps url
	 * @param url passed
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
					queuew.execute(new Task(url.toString(),html, invertedIndex));
					//computeSingleUrl(url.toString(), html);
					
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
	private static void computeSingleUrl(String url, String html, ThreadSafeInvertedIndex invertedIndex) throws IOException {
		int i = 1;
		InvertedIndex local=new InvertedIndex();
		Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		for (String word : TextParser.parse(html)) {
			local.add(stemmer.stem(word).toString(), url, i++);
		}
		invertedIndex.addAll(local);

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
		 * @param path          path
		 * @param invertedIndex invertedIndex passed
		 */
		public Task(String url, String html, ThreadSafeInvertedIndex invertedIndex) {
			this.url = url;
			this.html = html;
			this.invertedIndex = invertedIndex;
		}

		@Override
		public void run() {

			try {

				computeSingleUrl(url, html, invertedIndex);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
