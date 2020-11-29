import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;


/**
 * Multithreading inverted index builder class
 * @author arnau
 *
 */
public class MultithreadInvertedIndexBuilder extends InvertedIndexBuilder{
	/**
	 * fillInvertedIndexMultithreading
	 * 
	 * @param invertedIndex the invertedIndex
	 * @param inputPath     the path
	 * @param threads       thread num
	 */
	public static void fillInvertedIndexMultithread(ThreadSafeInvertedIndex invertedIndex, Path inputPath, int threads) {
		try {
			WorkQueue queue = new WorkQueue(threads);
			if (Files.isDirectory(inputPath)) {
				ArrayList<Path> pathList = new ArrayList<Path>();
				pathList = traverseDirectory(inputPath, pathList);
				for (Path path : pathList) {
					queue.execute(new Task(path, invertedIndex));
				}
			} else {
				queue.execute(new Task(inputPath, invertedIndex));
			}
			queue.join();
		} catch (IOException exception) {
			System.out.println(exception); // TODO Better output
		}

	}
	
	// TODO Remove, use InvertedIndexBuilder version where needed to avoid so much duplicate code
	// TODO Move the local/addAll logic into the run method
	/**
	 * Computes a single file with a local InvertedIndex
	 * @param invertedIndex thread safe
	 * @param inputPath path to use
	 * @throws IOException exception
	 */
	public static void computeSingleFile(ThreadSafeInvertedIndex invertedIndex, Path inputPath) throws IOException {
		int i = 1;
		InvertedIndex local=new InvertedIndex();
		try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8);) {
			String line = null;
			Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			String path = inputPath.toString();
			while ((line = reader.readLine()) != null) {
				for (String word : TextParser.parse(line)) {
					local.add(stemmer.stem(word).toString(), path, i++);
				}
			}
			invertedIndex.addAll(local);
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
		private final Path path;
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
		public Task(Path path, ThreadSafeInvertedIndex invertedIndex) {
			this.path = path;
			this.invertedIndex = invertedIndex;
		}

		@Override
		public void run() {
			try {

				computeSingleFile(invertedIndex, path);

			} catch (IOException e) {
				e.printStackTrace(); // TODO Fix
			}

		}
	}
}
