
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class WebCrawler {

	private final InvertedIndex invertedIndex;
	//private final ArrayList<URL> links;
	private int count = 0;
	private final int total;
	private final Queue<URL> queue=new LinkedList<URL>();

	public WebCrawler(InvertedIndex invertedIndex, String url, int total) {
		//links = new ArrayList<URL>();
		this.invertedIndex = invertedIndex;
		this.total = total;
		try {
			processURL(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void processURL(URL url) throws MalformedURLException {
		queue.add(url);
		while ((count < total)&&queue.size()>0) {
			ArrayList<URL> temp= scrapper(queue.poll());
			queue.addAll(temp);
		}
	}
	
	private ArrayList<URL> scrapper(URL url) {
		ArrayList<URL> links=new ArrayList<URL>();
		try {
			//links.add(url);
			
			String html = HtmlFetcher.fetch(url, 3);
			if (html != null) {
				html = HtmlCleaner.stripBlockElements(html);
				for (URL x : LinkParser.getValidLinks(url, html)) {
					links.add(x);
				}
				
				html = HtmlCleaner.stripHtml(html);
				System.out.println(url + "\n");
				computeSingleFile(url.toString(), html);
				count++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return links;
	}

	
	
	
	
	private void computeSingleFile(String url, String html) throws IOException {
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
