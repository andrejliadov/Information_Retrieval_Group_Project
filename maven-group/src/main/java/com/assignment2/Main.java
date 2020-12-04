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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        
        InputStream stopWordFile = Main.class.getClassLoader().getResourceAsStream("stopwords.txt");
        List<String> stopWords = Arrays.asList(stopWordFile.readAllBytes().toString().split("\n"));
        CharArraySet stopwords = new CharArraySet(stopWords, true);
        
        Analyzer analyzer = new StandardAnalyzer(stopwords);
        Similarity similarityFunction = new BM25Similarity();
        
        Directory directory = BuildIndex.startBuildIndex(analyzer);
        List<NumberedQuery> queries = QueryReader.readQueries(analyzer);
        Search.search(directory, queries, similarityFunction);
        directory.close();
	}
}
