package search;

import bean.Part;
import util.FileReader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchEngine implements AutoCloseable{
    private File rootFile;
    private final int sizeBlock;

    private ObjectInputStream inputStream;

    private File currentFile;

    private Part prev;

    private Map<File, File> temps = new HashMap<>();

    public SearchEngine(File rootFile, int sizeBlock) {
        this.rootFile = rootFile;
        this.sizeBlock = sizeBlock;
    }

    public int search(String pattern, String extension, SearchResult result) throws IOException {
        return engine(rootFile, pattern, extension, result);
    }

    public int engine(File file, String pattern, String extension, SearchResult result) throws IOException{
        int count = 0;

        if(file.isDirectory()){

            File[] files = file.listFiles();

            if(files != null) {
                for (File item : files) {
                    if(item.exists() && item.canRead()) {
                        count += engine(item, pattern, extension, result);
                    }
                }
            }

        } else {
            if(!extension.isEmpty() &&
                    !file.getName().toLowerCase().contains("." + extension)) {
                return 0;
            }

            File tempFile = File.createTempFile("TextFinder", rootFile.getName());

            try(ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(tempFile))) {

                count = new FindText(new FileReader(file, sizeBlock))
                        .findAll(outputStream, pattern);
            }

            temps.put(file, tempFile);

            result.apply(file.getName(), count);
        }

        return count;
    }

    public List<Part> next(int pageIndex) throws IOException, ClassNotFoundException {
        List<Part> parts = new ArrayList<>();

        if(prev != null){

            if(prev.getPageEnd() == pageIndex) {
                parts.add(prev);
                prev = null;
            }
            else if(prev.getPageStart() <= pageIndex){
                parts.add(prev);
                return parts;
            }
            else {
                return parts;
            }
        }

        if(inputStream != null) {
            try {
                while (true) {
                    Part part = (Part) inputStream.readObject();

                    if (part.getPageStart() > pageIndex) {
                        prev = part;
                        break;
                    }

                    parts.add(part);

                    if(part.getPageEnd() > pageIndex){
                        prev = part;
                        break;
                    }
                }
            } catch (EOFException ignored){
            }
        }

        return parts;
    }

    public List<Part> prev(int pageIndex) throws IOException, ClassNotFoundException{
        newCurrent(currentFile);

        prev = null;

        List<Part> parts = null;

        for(int i = 0; i < pageIndex -1; i++){
            parts = next(i);
        }

        return parts;
    }

    public boolean ready(){
        return inputStream != null;
    }

    public void newCurrent(File currentFile) throws IOException {
        this.currentFile = currentFile;

        if(inputStream != null) {
            inputStream.close();
        }

        File tempFile = temps.get(currentFile);

        if(tempFile != null) {
            inputStream = new ObjectInputStream(new FileInputStream(tempFile));
        }
    }

    @Override
    public void close() throws IOException {
        temps.values()
                .forEach(File::delete);

        if(inputStream != null){
            inputStream.close();
        }
    }
}
