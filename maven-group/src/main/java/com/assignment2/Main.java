package com.assignment2;

import com.assignment2.index.BuildIndex;
import com.assignment2.query.NumberedQuery;
import com.assignment2.query.QueryReader;
import com.assignment2.search.Search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
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

    public static void main(String[] args) throws IOException {
        Analyzer analyzer = new MyAnalyzer();
        Float k = (float)0.75;
        Float b = (float)0.70;
        Similarity similarityFunction = new BM25Similarity(k, b);

        Directory directory = BuildIndex.startBuildIndex(analyzer);
        List<NumberedQuery> queries = QueryReader.readQueries(analyzer);
        Search.search(directory, queries, similarityFunction);
        directory.close();
	}
}
