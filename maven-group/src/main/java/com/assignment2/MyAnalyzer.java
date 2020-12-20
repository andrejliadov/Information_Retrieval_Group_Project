package com.assignment2;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.en.*;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.synonym.WordnetSynonymParser;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.ElisionFilter;
import org.apache.lucene.util.CharsRef;
import org.tartarus.snowball.ext.EnglishStemmer;

import com.assignment2.filter.LemmaFilter;

import edu.stanford.nlp.simple.Sentence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.text.ParseException;

public class MyAnalyzer extends StopwordAnalyzerBase {

    private static final String stop_file_path = "stopwords/stopwords2.txt";
    private static final String synonym_file_path = "wn_s.pl";
    private static Reader synonymReader;
    private static String temp = "";

    public MyAnalyzer() throws IOException {
        super(StopwordAnalyzerBase.loadStopwordSet(Paths.get(stop_file_path)));
        synonymReader = new FileReader(synonym_file_path);
        
    }

    protected TokenStreamComponents createComponents(String fieldName) {

//        Tokenizer stdTokenizer = new ClassicTokenizer();
        Tokenizer stdTokenizer = new StandardTokenizer();
//        TokenStream tokenStream = new ClassicFilter(stdTokenizer);
//        tokenStream = new ASCIIFoldingFilter(tokenStream);
        TokenStream tokenStream = new EnglishPossessiveFilter(stdTokenizer);
        tokenStream = new LowerCaseFilter(tokenStream);
        tokenStream = new StopFilter(tokenStream, stopwords);
        tokenStream = new LemmaFilter(tokenStream);
        /*WordnetSynonymParser parser = new WordnetSynonymParser(true, false, new StandardAnalyzer());
        
		try {
			//parser.parse(synonymReader);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//tokenStream = new SynonymGraphFilter(tokenStream, parser.build(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        tokenStream = new FlattenGraphFilter(
//                new SynonymGraphFilter(tokenStream, "",
//                        true));*/
//
//

//        tokenStream = new WordDelimiterGraphFilter(tokenStream,WordDelimiterGraphFilter.GENERATE_WORD_PARTS
//                | WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS
//                | WordDelimiterGraphFilter.SPLIT_ON_CASE_CHANGE
//                | WordDelimiterGraphFilter.SPLIT_ON_NUMERICS
//                | WordDelimiterGraphFilter.STEM_ENGLISH_POSSESSIVE, null);

        //tokenStream = new SnowballFilter(tokenStream,
          //      new EnglishStemmer());

        //tokenStream = new EnglishMinimalStemFilter(tokenStream);
        //tokenStream = new PorterStemFilter(tokenStream);

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
