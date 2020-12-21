package com.assignment2;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.en.*;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.util.ElisionFilter;
import org.apache.lucene.util.CharsRef;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class MyAnalyzer extends StopwordAnalyzerBase {

    private static final String stop_file_path = "stopwords/stopwords2.txt";

    public MyAnalyzer() throws IOException {
        super(StopwordAnalyzerBase.loadStopwordSet(Paths.get(stop_file_path)));
    }

    protected TokenStreamComponents createComponents(String fieldName) {

//        Tokenizer stdTokenizer = new ClassicTokenizer();
        Tokenizer stdTokenizer = new StandardTokenizer();
//        TokenStream tokenStream = new ClassicFilter(stdTokenizer);
//        tokenStream = new ASCIIFoldingFilter(tokenStream);
        TokenStream tokenStream = new EnglishPossessiveFilter(stdTokenizer);
        tokenStream = new LowerCaseFilter(tokenStream);
        tokenStream = new StopFilter(tokenStream, stopwords);
//        tokenStream = new FlattenGraphFilter(
//                new SynonymGraphFilter(tokenStream, "",
//                        true));
//
//

//        tokenStream = new WordDelimiterGraphFilter(tokenStream,WordDelimiterGraphFilter.GENERATE_WORD_PARTS
//                | WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS
//                | WordDelimiterGraphFilter.SPLIT_ON_CASE_CHANGE
//                | WordDelimiterGraphFilter.SPLIT_ON_NUMERICS
//                | WordDelimiterGraphFilter.STEM_ENGLISH_POSSESSIVE, null);

        tokenStream = new SnowballFilter(tokenStream, new EnglishStemmer());

        tokenStream = new EnglishMinimalStemFilter(tokenStream);
        tokenStream = new PorterStemFilter(tokenStream);

        // ElisionFilter

        //CapitalizationFilter

        // TrimFilter Trims leading and trailing whitespace from Tokens in the stream.

        // LengthFilter Removes words that are too long or too short from the stream.

        // KStemFilter A high-performance kstem filter for english.

        // SetKeywordMarkerFilter


        return new TokenStreamComponents(stdTokenizer, tokenStream);
    }

    protected TokenStream normalize(String fieldName, TokenStream in) {
        return new LowerCaseFilter(in);
    }



}
