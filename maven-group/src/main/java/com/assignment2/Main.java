package com.assignment2;

import com.assignment2.index.BuildIndex;
import com.assignment2.query.NumberedQuery;
import com.assignment2.query.QueryReader;
import com.assignment2.search.Search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        Analyzer analyzer = new MyAnalyzer();
        Similarity similarityFunction = new BM25Similarity();
        
        String test = "Still, Mr. Barr’s resignation allows him to avoid further confrontation with the president over his refusal to advance Mr. Trump’s attempts to rewrite the election results or his efforts to interfere in criminal inquiries into Mr. Biden’s family.";
        TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(test));
        
        CharTermAttribute cattr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
          System.out.println(cattr.toString());
        }
        tokenStream.end();
        tokenStream.close();

        
        Directory directory = BuildIndex.startBuildIndex(analyzer);
        List<NumberedQuery> queries = QueryReader.readQueries(analyzer);
        Search.search(directory, queries, similarityFunction);
        directory.close();
	}
}
