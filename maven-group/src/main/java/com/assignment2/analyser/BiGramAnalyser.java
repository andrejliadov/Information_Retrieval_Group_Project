package com.assignment2.analyser;

import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.CapitalizationFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.pattern.PatternReplaceFilter;
import org.apache.lucene.analysis.shingle.FixedShingleFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class BiGramAnalyser extends Analyzer {
	
	private static CharArraySet stopSet;
	
	public BiGramAnalyser(CharArraySet stopList){
		stopSet = stopList;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Pattern p = Pattern.compile("_");
		final Tokenizer tokenizer = new StandardTokenizer();
		final ShingleFilter shingleFilter = new ShingleFilter( new PorterStemFilter(new TrimFilter(new LowerCaseFilter(new StopFilter(tokenizer, stopSet)))), 2 );
		shingleFilter.setOutputUnigrams( true );
        return new TokenStreamComponents(tokenizer, shingleFilter);

	}

}
