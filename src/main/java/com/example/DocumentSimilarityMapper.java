package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.StringTokenizer;

public class DocumentSimilarityMapper extends Mapper<Object, Text, Text, Text> {

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        // Split document ID and content
        String[] parts = value.toString().split("\\s+", 2);
        if (parts.length < 2) return;

        String documentId = parts[0];  // e.g., doc1
        String content = parts[1];

        // Extract unique words
        HashSet<String> wordSet = new HashSet<>();
        StringTokenizer tokenizer = new StringTokenizer(content);
        while (tokenizer.hasMoreTokens()) {
            wordSet.add(tokenizer.nextToken().toLowerCase());
        }

        // Emit document ID and word set
        context.write(new Text(documentId), new Text(String.join(",", wordSet)));
    }
}

