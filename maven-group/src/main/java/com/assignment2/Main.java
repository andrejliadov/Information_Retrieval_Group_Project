package com.assignment2;

import com.assignment2.index.BuildIndex;
import com.assignment2.query.NumberedQuery;
import com.assignment2.query.QueryReader;
import com.assignment2.search.Search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static CharArraySet getStopWords() throws IOException {
        InputStream stopWordFile = Main.class.getClassLoader().getResourceAsStream("stopwords.txt");
        List<String> stopWords = Arrays.asList(
            new String(stopWordFile.readAllBytes(), StandardCharsets.UTF_8.name())
            .split("\\s+")
        );
        return new CharArraySet(stopWords, true);
    }
    
    public static void main(String[] args) throws IOException {
        Analyzer analyzer = new StandardAnalyzer(getStopWords());
        Similarity similarityFunction = new BM25Similarity();
        
        Directory directory = BuildIndex.startBuildIndex(analyzer);
        List<NumberedQuery> queries = QueryReader.readQueries(analyzer);
        Search.search(directory, queries, similarityFunction);
        directory.close();
	}
}
