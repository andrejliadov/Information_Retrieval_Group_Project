package com.assignment2.query;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.simple.SimpleQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class QueryReader {
    
    private static final String TOPIC_PATH = "topics";
    
    private static final String TOPIC = "top";
    private static final String NUMBER = "num";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "desc";
    private static final String NARRATIVE = "narr";
    
    private static final String QUERIED_FIELD = "text";
    
    private static final float TITLE_TERM_WEIGHT = 1.0f;
    private static final float DESC_TERM_WEIGHT = 1.5f;
    private static final float NARR_TERM_WEIGHT = 0.5f;
    
    private static String[] tokenise(String queryString) {
        return queryString
            .replace(",", "")
            .replace(".", "")
            .split(" ");
    }
    
    private static Query parseTitle(String title) {
        BooleanQuery.Builder bq = new BooleanQuery.Builder();
        String[] terms = tokenise(title);
        for (String termString : terms) {
            Query termQuery = new TermQuery(new Term(QUERIED_FIELD, termString));
            bq.add(new BoostQuery(termQuery, TITLE_TERM_WEIGHT), BooleanClause.Occur.SHOULD);
        }
        return bq.build();
    }
    
    private static Query parseDescription(Analyzer analyzer, String description) {
        SimpleQueryParser parser = new SimpleQueryParser(analyzer, QUERIED_FIELD);
        Query parsedQuery = parser.parse(description.trim());
    	
        return new BoostQuery(parsedQuery, DESC_TERM_WEIGHT);
    }
    
    private static Query parseNarrative(String narrative) {
        return null;
    }
    
    private static Query generateQuery(Analyzer analyzer, String title, String description, String narrative) {
        Query titleQuery = parseTitle(title);
        Query descQuery = parseDescription(analyzer, description);
        // Query narrQuery = parseNarrative(narrative);
        
        BooleanQuery.Builder bq = new BooleanQuery.Builder();
        bq.add(titleQuery, BooleanClause.Occur.SHOULD);
        bq.add(descQuery, BooleanClause.Occur.SHOULD);
        return bq.build();
    }
    
    private static NumberedQuery parseQuery(Analyzer analyzer, Element element) {
        // query number
        int number = Integer.parseInt(
            element.getElementsByTag(NUMBER)
                .first().ownText()
                .replace("Number: ", "")
        );
        
        // list of comma-separated terms
        String title = element
            .getElementsByTag(TITLE)
            .first().ownText();
        
        // English sentence query
        String description = element
            .getElementsByTag(DESCRIPTION)
            .first().ownText()
            .replace("Description: ", "");
        
        // narrative explains additional relevant terms plus non-relevant terms, will be more difficult to parse
        String narrative = element
            .getElementsByTag(NARRATIVE)
            .first().ownText()
            .replace("Narrative: ", "");
        
        Query query = generateQuery(analyzer, title, description, narrative);
        return new NumberedQuery(number, query);
    }
    
    public static List<NumberedQuery> readQueries(Analyzer analyzer) throws IOException {
        List<NumberedQuery> queryList = new ArrayList<NumberedQuery>();
        
        File topicFile = new File(TOPIC_PATH);
        Elements topics = Jsoup
            .parse(topicFile, StandardCharsets.UTF_8.name())
            .getElementsByTag(TOPIC);

        topics.forEach(element -> 
            queryList.add(
                parseQuery(analyzer, element)
            )
        );
        
        return queryList;
    }
}
