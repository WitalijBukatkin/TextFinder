import org.junit.jupiter.api.Test;
import search.SearchEngine;
import util.FileReader;

import java.io.File;
import java.io.IOException;

public class SearchEngineTest {
    private final SearchEngine searchEngine;

    private final File rootFile = new File("/home/nikita/Загрузки/2600-0.txt");

    private final FileReader fileReader = new FileReader(rootFile, 10000);

    public SearchEngineTest() throws IOException {
        searchEngine = new SearchEngine(rootFile, 10000);
        searchEngine.search("войн", "txt",  (name, count) -> {});
    }

    @Test
    void getNext() throws IOException, ClassNotFoundException {
        searchEngine.newCurrent(fileReader);

        for (int i = 0; i < 10; i++) {
            System.out.println(searchEngine.next());
        }
    }
}
