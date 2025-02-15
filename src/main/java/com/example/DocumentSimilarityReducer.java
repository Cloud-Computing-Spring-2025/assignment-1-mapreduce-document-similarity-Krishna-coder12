package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {

    private final List<String> docIds = new ArrayList<>();
    private final List<HashSet<String>> wordSets = new ArrayList<>();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text value : values) {
            HashSet<String> wordSet = new HashSet<>(Arrays.asList(value.toString().split(",")));
            docIds.add(key.toString());
            wordSets.add(wordSet);
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        int numDocs = docIds.size();

        for (int i = 0; i < numDocs; i++) {
            for (int j = i + 1; j < numDocs; j++) {
                HashSet<String> intersection = new HashSet<>(wordSets.get(i));
                intersection.retainAll(wordSets.get(j));

                HashSet<String> union = new HashSet<>(wordSets.get(i));
                union.addAll(wordSets.get(j));

                if (!union.isEmpty()) {
                    double similarity = (double) intersection.size() / union.size();

                    // Only output pairs with similarity >= 50% (0.50 in decimal)
                    if (similarity >= 0.50) {
                        String outputKey = "(" + docIds.get(i) + ", " + docIds.get(j) + ")";
                        String outputValue = (int) (similarity * 100) + "%";  // Convert to percentage
                        context.write(new Text(outputKey), new Text(outputValue));
                    }
                }
            }
        }
    }
}
