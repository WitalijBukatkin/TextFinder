/*
 * Copyright (c) 2020. Witalij Bukatkin
 * Github profile: https://github.com/witalijbukatkin
 */

package com.github.witalijbukatkin.textfinder.util;

import java.io.*;

public class FileReader implements AutoCloseable{
    private File file;

    private BufferedReader reader;

    private int size;

    private int sizeBlock;

    private int page = 0;

    public FileReader(File file, int sizeBlock) throws IOException {
        this.file = file;
        this.sizeBlock = sizeBlock;

        update();
    }

    public File getFile() {
        return file;
    }

    public void update() throws IOException{
        close();

        FileInputStream inputStream = new FileInputStream(file);

        size = inputStream.available();

        reader = new BufferedReader(new InputStreamReader(inputStream), sizeBlock);
    }

    public String next() {
        if(ready() && page < getPageCount()) {
            char[] chars = new char[sizeBlock];

            try {
                reader.read(chars);
                page ++;
                return String.valueOf(chars).replace("\n", "");
            } catch (IOException ignored) {
            }
        }

        return null;
    }

    public String prev(){
        if(page - 2 < 0){
            page = 0;
        } else {
            page -=2;
        }

        try {
            update();

            reader.skip(page * sizeBlock);

            return next();
        } catch (IOException e) {
            return null;
        }
    }

    public boolean ready(){
        try {
            return reader.ready();
        } catch (IOException e) {
            return false;
        }
    }

    public int getPageId(){
        return page;
    }

    public int getPageCount(){
        return size / sizeBlock + 1;
    }

    @Override
    public void close(){
        if(reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
