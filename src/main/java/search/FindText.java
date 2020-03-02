package search;

import bean.Part;
import util.FileReader;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class FindText {
    private final FileReader reader;

    public FindText(FileReader reader) {
        this.reader = reader;
    }

    public List<Part> findAll(String pattern) {
        List<Part> indexes = new ArrayList<>();

        int sizeLastData = 0;

        while(reader.ready()){
            String next = reader.next();
            sizeLastData = findOne(indexes, reader.getPageId(), next, pattern, sizeLastData);
        }

        if(sizeLastData != 0){
            indexes.remove(indexes.size() - 1);
        }

        return indexes;
    }

    public int findAll(ObjectOutputStream stream, String pattern) throws IOException {
        List<Part> indexes = new ArrayList<>();

        AtomicInteger count = new AtomicInteger();

        int sizeLastData = 0;

        while(reader.ready()){
            sizeLastData = findOne(indexes, reader.getPageId(), reader.next(), pattern, sizeLastData);

            List<Part> parts = indexes.stream()
                    .filter(part -> part.getIndexEnd() != null)
                    .collect(Collectors.toList());

            parts.forEach(part -> {
                try {
                    count.incrementAndGet();
                    stream.writeObject(part);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            stream.flush();

            indexes.removeAll(parts);
        }

        if(sizeLastData != 0){
            indexes.remove(indexes.size() - 1);
        }

        return count.get();
    }

    public int findOne(List<Part> indexes, int pageId, String data, String pattern, int sizeLastData){
        if(pattern.equals(data)){
            indexes.add(new Part(0, pageId,
                    data.length() - 1, pageId));
            return 0;
        }

        if(indexes.size() > 0 && sizeLastData != 0) {
            Part last = indexes.get(indexes.size() - 1);

            String s = pattern.substring(sizeLastData);

            Integer head = getHeadEnd(data, s);

            if(head != null){
                last.setIndexEnd(head)
                        .setPageEnd(pageId);
            }
            else if(isCenter(data, s)){
                return sizeLastData + data.length();
            }
            else {
                indexes.remove(last);
            }
        }

        indexes.addAll(getInBlock(data, pattern, pageId));

        Integer tail = getTail(data, pattern);

        if(tail != null){
            indexes.add(new Part(tail, pageId));
            return data.length() - tail;
        }

        return 0;
    }

    private List<Part> getInBlock(String data, String pattern, int pageId){
        List<Part> indexes = new ArrayList<>();

        for(int index = 0; index < data.length();){
            int found = data.indexOf(pattern, index);

            if(found == -1) {
                break;
            }

            index = found + 1;

            indexes.add(new Part(found, pageId,
                    found + pattern.length() - 1, pageId));
        }

        return indexes;
    }

    private Integer getHeadEnd(String data, String pattern){
        return data.indexOf(pattern) == 0 ?
                pattern.length() -1 : null;
    }

    private boolean isCenter(String data, String pattern){
        return pattern.indexOf(data) == 0;
    }

    private Integer getTail(String data, String pattern){

        for(int i = pattern.length() - 1; i > 0; i--){
            String tail = pattern.substring(0, i);

            int index = data.lastIndexOf(tail);

            if(index != -1 && index == data.length() - i){
                return index;
            }
        }

        return null;
    }
}
