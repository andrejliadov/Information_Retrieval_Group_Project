package com.assignment2;

import com.assignment2.analyzer.MyAnalyzer;
import com.assignment2.index.BuildIndex;
import com.assignment2.query.NumberedQuery;
import com.assignment2.query.QueryReader;
import com.assignment2.search.Search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.List;

/**
 * the main class for this project
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Analyzer analyzer = new MyAnalyzer();
        float k = 0.75f;
        float b = 0.70f;
        Similarity similarityFunction = new BM25Similarity(k, b);

        Directory directory = BuildIndex.startBuildIndex(analyzer);
        List<NumberedQuery> queries = QueryReader.readQueries(analyzer);
        Search.search(directory, queries, similarityFunction);
        directory.close();
	}
}
