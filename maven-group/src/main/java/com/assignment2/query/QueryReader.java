package com.assignment2.query;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.simple.SimpleQueryParser;
import org.apache.lucene.search.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * The class for parsing topic file and generate queries
 */
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


    /**
     * get the relevant content and irrelevant content from narrative
     */
    private static List<String> splitRelevance(String narrative) {

        List<String> result = new ArrayList<>();

        StringBuilder relevantStr = new StringBuilder();
        StringBuilder irrelevantStr = new StringBuilder();
        BreakIterator bi = BreakIterator.getSentenceInstance();
        bi.setText(narrative);
        int index = 0;
        while (bi.next() != BreakIterator.DONE) {
            String sentence = narrative.substring(index, bi.current());

            if (sentence.contains("not relevant") || sentence.contains("irrelevant")) {
                irrelevantStr.append(sentence.replaceAll("not relevant|irrelevant", ""));
            } else {
                relevantStr.append(sentence.replaceAll("a relevant document|relevant|will contain|will discuss|Discussions of|include|mentioning|must cite|etc.", " "));
            }
            index = bi.current();
        }
        result.add(relevantStr.toString());
        result.add(irrelevantStr.toString());
        return result;
    }


    /**
     * generate query
     */
    private static Query generateQuery(Analyzer analyzer, String title, String description, String narrative) throws ParseException {


        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

        QueryParser queryParser = new QueryParser("text", analyzer);

        if (title.length() > 0) {

            Query titleQuery = queryParser.parse(QueryParser.escape(title));
            Query descriptionQuery = queryParser.parse(QueryParser.escape(description));
            Query narrativeQuery = null;
            Query irrNarrativeQuery = null;
            if (narrative != null && narrative.length() > 0) {
                List<String> relevanceTexts = splitRelevance(narrative);
                String relevantStr = relevanceTexts.get(0);
                String irrelevantStr = relevanceTexts.get(1);
                if (relevantStr.length() > 0) {
                    narrativeQuery = queryParser.parse(QueryParser.escape(relevantStr));
                }

                if (irrelevantStr.length() > 0) {
                    irrNarrativeQuery = queryParser.parse(QueryParser.escape(irrelevantStr));
                }
            }

            // 0.3415
            booleanQuery.add(new BoostQuery(titleQuery, (float) 7), BooleanClause.Occur.SHOULD);
            booleanQuery.add(new BoostQuery(descriptionQuery, (float) 2.75), BooleanClause.Occur.MUST);

            if (narrativeQuery != null) {
                booleanQuery.add(new BoostQuery(narrativeQuery, (float) 2.0), BooleanClause.Occur.SHOULD);
            }
//            if (irrNarrativeQuery != null) {
//                booleanQuery.add(new BoostQuery(irrNarrativeQuery, (float) 2.0), BooleanClause.Occur.FILTER);
//            }
        }

        return booleanQuery.build();
    }

    /**
     * parse query file and get the query number, title, desc, narr
     */
    private static NumberedQuery parseQuery(Analyzer analyzer, Element element) throws ParseException {
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

    /**
     * read topic file
     */
    public static List<NumberedQuery> readQueries(Analyzer analyzer) throws IOException {
        List<NumberedQuery> queryList = new ArrayList<NumberedQuery>();

        File topicFile = new File(TOPIC_PATH);
        Elements topics = Jsoup
                .parse(topicFile, StandardCharsets.UTF_8.name())
                .getElementsByTag(TOPIC);

        topics.forEach(element ->
                {
                    try {
                        queryList.add(
                                parseQuery(analyzer, element)
                        );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
        );

        return queryList;
    }
}
