import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

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
			System.out.println("Unable to read path: "+inputPath);
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
				InvertedIndex local=new InvertedIndex();
				computeSingleFile(local, path);
				invertedIndex.addAll(local);
			} catch (IOException e) {
				System.out.println("Unable to compute file "+path);
			}

		}
	}
}
