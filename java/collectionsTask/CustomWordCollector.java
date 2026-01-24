import java.util.*;
import java.util.stream.*;

class WordCountResult {
    int totalWordCount;
    int uniqueWordCount;

    WordCountResult(int total, int unique) {
        this.totalWordCount = total;
        this.uniqueWordCount = unique;
    }

    public String toString() {
        return "Total words: " + totalWordCount + ", Unique words: " + uniqueWordCount;
    }
}

public class CustomWordCollector {
    public static void main(String[] args) {
        List<String> sentences = Arrays.asList(
                "Hello world hello",
                "World is beautiful",
                "Beautiful day ahead");

        WordCountResult result = sentences.stream()
                .collect(Collector.of(
                        () -> new Object[] { 0, new HashSet<String>() },
                        (acc, sentence) -> {
                            String[] words = sentence.toLowerCase().split("\\s+");
                            acc[0] = (int) acc[0] + words.length;
                            ((Set<String>) acc[1]).addAll(Arrays.asList(words));
                        },
                        (acc1, acc2) -> {
                            acc1[0] = (int) acc1[0] + (int) acc2[0];
                            ((Set<String>) acc1[1]).addAll((Set<String>) acc2[1]);
                            return acc1;
                        },
                        acc -> new WordCountResult((int) acc[0], ((Set<String>) acc[1]).size())));

        System.out.println("Sentences: " + sentences);
        System.out.println(result);
    }
}
