package com.assignment2;

import com.assignment2.analyser.BiGramAnalyser;
import com.assignment2.index.BuildIndex;
import com.assignment2.query.NumberedQuery;
import com.assignment2.query.QueryReader;
import com.assignment2.search.Search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.AttributeFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static CharArraySet getStopWords() throws IOException {
        InputStream stopWordFile = Main.class.getClassLoader().getResourceAsStream("stopwords.txt");
        List<String> stopWords = Arrays.asList(stopWordFile.readAllBytes().toString().split("\n"));
        return new CharArraySet(stopWords, true);
    }
    
    public static void main(String[] args) throws IOException {
        Analyzer analyzer = new BiGramAnalyser(getStopWords());        
        Similarity similarityFunction = new BM25Similarity();
        
        Directory directory = BuildIndex.startBuildIndex(analyzer);
        
        List<NumberedQuery> queries = QueryReader.readQueries(analyzer);
        Search.search(directory, queries, similarityFunction);
        
        directory.close();
	}
}
