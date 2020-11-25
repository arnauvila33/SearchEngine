import java.io.IOException;
import java.nio.file.Path;
import java.util.*;







/**
 * A special type of simpleIndex that indexes the UNIQUE words that were found
 * in a text file. THREAD-SAFE
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class ThreadSafeInvertedIndex extends InvertedIndex{

	/** Lock used to make class thread safe */
	private final SimpleReadWriteLock lock;
	


	/**
	 * Constructor for Inverted Index
	 */
	public ThreadSafeInvertedIndex() {

		lock = new SimpleReadWriteLock();
	}
	
	@Override
	public void add(String word, String location, int position) {
		lock.writeLock().lock();

		try {
			super.add(word, location, position);
		}
		finally {
			lock.writeLock().unlock();
		}
	}
	@Override
	public boolean contains(String word) {
		lock.readLock().lock();

		try {
			return super.contains(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	@Override
	public boolean contains(String word, String path) {
		lock.readLock().lock();

		try {
			return super.contains(word,path);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	@Override
	public boolean contains(String word, String path, int position) {
		lock.readLock().lock();

		try {
			return super.contains(word,path,position);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public Collection<String> get() {
		lock.readLock().lock();

		try {
			return super.get();
		}
		finally {
			lock.readLock().unlock();
		}
	}
	@Override
	public Collection<String> get(String word) {
		lock.readLock().lock();

		try {
			return super.get(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	@Override
	public Collection<Integer> get(String word, String path) {
		lock.readLock().lock();

		try {
			return super.get(word,path);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public Integer getCount(String word) {
		lock.readLock().lock();
		try {
			return super.getCount(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	@Override
	public int size() {
		lock.readLock().lock();

		try {
			return super.size();
		}
		finally {
			lock.readLock().unlock();
		}
	}
	@Override
	public int size(String word) {
		lock.readLock().lock();

		try {
			return super.size(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	@Override
	public int size(String word, String path) {
		lock.readLock().lock();

		try {
			return super.size(word,path);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	@Override
	public void toJson(Path path) throws IOException {
		lock.readLock().lock();

		try {
			super.toJson(path);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	@Override
	public void countToJson(Path path) throws IOException {
		lock.readLock().lock();

		try {
			super.countToJson(path);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public String toString() {
		lock.readLock().lock();

		try {
			return super.toString();
		}
		finally {
			lock.readLock().unlock();
		}
	}
	



	
	



}
