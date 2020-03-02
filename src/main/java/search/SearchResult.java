package search;

@FunctionalInterface
public interface SearchResult {
    void apply(String fileName, int countFound);
}
