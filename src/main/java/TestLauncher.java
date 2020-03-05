import search.FindText;
import util.FileReader;

import java.io.File;
import java.io.IOException;

public class TestLauncher {

    public static void main(String[] args) throws IOException {

        FileReader reader = new FileReader(new File("/home/nikita/Загрузки/2600-0.txt"), 10000);

        new FindText(reader).findAll("войн").forEach(System.out::println);
    }
}
