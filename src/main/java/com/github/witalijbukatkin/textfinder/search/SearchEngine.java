/*
 * Copyright (c) 2020. Witalij Bukatkin
 * Github profile: https://github.com/witalijbukatkin
 */

package com.github.witalijbukatkin.textfinder.search;

import com.github.witalijbukatkin.textfinder.bean.Part;
import com.github.witalijbukatkin.textfinder.util.FileReader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchEngine implements AutoCloseable{
    private final int sizeBlock;

    private File rootFile;
    private int pageIndex;

    private ObjectInputStream currentInputStream;
    private FileReader currentReader;

    private Part prev;

    private Map<File, File> temps = new HashMap<>();

    private int count;

    private Thread threadAsync;

    public SearchEngine(File rootFile, int sizeBlock) {
        this.rootFile = rootFile;
        this.sizeBlock = sizeBlock;
    }

    public void search(String pattern, String extension, SearchResult result) throws IOException {
        count = engine(rootFile, pattern, extension, result);
    }

    public void searchAsync(Runnable runnable) {
        if(threadAsync == null){
            threadAsync = new Thread(runnable);
            threadAsync.start();
        }
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

    public List<Part> next() throws IOException, ClassNotFoundException {
        List<Part> parts = new ArrayList<>();

        if (pageIndex > currentReader.getPageCount()) {
            pageIndex = currentReader.getPageCount();
        }

        if (prev != null) {

            if (prev.getPageEnd() == pageIndex) {
                parts.add(prev);
                prev = null;
            } else if (prev.getPageStart() <= pageIndex) {
                parts.add(prev);
                pageIndex++;
                return parts;
            } else {
                pageIndex++;
                return parts;
            }
        }

        if (currentInputStream != null) {
            try {
                while (true) {
                    Part part = (Part) currentInputStream.readObject();

                    if (part.getPageStart() > pageIndex) {
                        prev = part;
                        break;
                    }

                    parts.add(part);

                    if (part.getPageEnd() > pageIndex) {
                        prev = part;
                        break;
                    }
                }
            } catch (EOFException ignored) {
            }
        }

        pageIndex++;
        return parts;
    }

    public List<Part> prev() throws IOException, ClassNotFoundException{
        newCurrent(currentReader);

        prev = null;

        List<Part> parts = null;

        int prevIndex = pageIndex;

        for(pageIndex = 0; pageIndex < prevIndex - 1;){
            parts = next();
        }

        return parts;
    }

    public void newCurrent(FileReader reader) throws IOException {
        this.currentReader = reader;

        if(currentInputStream != null) {
            currentInputStream.close();
            currentInputStream = null;
        }

        File tempFile = temps.get(reader.getFile());

        if(tempFile != null) {
            currentInputStream = new ObjectInputStream(new FileInputStream(tempFile));
        }

        pageIndex = 0;
        prev = null;
    }

    @Override
    public void close() throws IOException {
        temps.values()
                .forEach(File::delete);

        if(currentInputStream != null){
            currentInputStream.close();
        }
    }

    public int getCount() {
        return count;
    }

    public void threadAsyncReset() {
        threadAsync = null;
    }
}
