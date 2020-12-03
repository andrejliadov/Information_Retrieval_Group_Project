package com.assignment2.query;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.simple.SimpleQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class QueryReader {
    
    public static final String TOPIC_PATH = "topics";
    
    public static final String TOPIC = "top";
    public static final String NUMBER = "num";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "desc";
    public static final String NARRATIVE = "narr";
    
    
    private static Query generateQuery(String title, String description, String narrative) {
        BooleanQuery.Builder query = new BooleanQuery.Builder();
        for(String termString : title.split(",")){
            for (String words : termString.trim().split(" ")) {
                Term term = new Term("text", words);
                query.add(new TermQuery(term), BooleanClause.Occur.SHOULD);
            }
        }
        return query.build();
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
        
        Query query = generateQuery(title, description, narrative);
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
