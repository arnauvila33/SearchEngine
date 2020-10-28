import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class QuerieStructure {

	private final Map<String, ArrayList<SingleQuerie>> querieSearchList;

	public QuerieStructure() {
		querieSearchList = new TreeMap<String, ArrayList<SingleQuerie>>();
	}

	public void add(String word, ArrayList<SingleQuerie> queries) {
		querieSearchList.put(word, queries);
	}

	public boolean contains(String words) {
		return !querieSearchList.isEmpty() && querieSearchList.containsKey(words);
	}

	public boolean contains(String words, SingleQuerie querie) {
		return contains(words) && querieSearchList.get(words).contains(querie);
	}

	public Collection<String> get() {
		return Collections.unmodifiableCollection(querieSearchList.keySet());
	}

	public Collection<SingleQuerie> get(String words) {
		if (contains(words)) {
			return Collections.unmodifiableCollection(querieSearchList.get(words));
		}
		return Collections.emptySet();
	}

	public int size() {
		return querieSearchList.size();
	}

	public int size(String words) {
		return get(words).size();
	}

	public void toJson(Path path) throws IOException {
		SimpleJsonWriter.asQuerieStructure(querieSearchList, path);
	}
}