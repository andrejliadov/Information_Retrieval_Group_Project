package com.assignment2.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

import org.jsoup.Jsoup;
// import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class LATParser
{
    private static final String DATASET_FOLDER = "Assignment Two/Assignment Two/latimes";

    public static void main( String[] args )
    {
        File dataset = new File(DATASET_FOLDER);
        File[] datasetFiles = dataset.listFiles();

        // This will store all articles across all files
        ArrayList<Document> documents = new ArrayList<Document>();

        if (datasetFiles != null) {
            for (File file : datasetFiles) {
                try {
                    // Loop through lines in file
                    BufferedReader bFile = new BufferedReader(new FileReader(file));
                    String fileLine = "";
                    String article = "";

                    while ((fileLine = bFile.readLine()) != null) {

                        if (fileLine.startsWith("<DOC>")) {
                            // If line contains <DOC> start new doc
                        } else if (fileLine.startsWith("</DOC>")) {
                            // If line contains </DOC>
                            // create Lucene document for article
                            Document doc = getElements(article);
                            documents.add(doc);

                            // Reset article to empty string for next doc
                            article = "";
                        } else {
                            // Otherwise add line to string
                            article = article.concat(fileLine);
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    private static Document getElements(String article) {
        // Did not parse list: <LENGTH> <GRAPHIC>
        // Document doc = Jsoup.parse(article);
        org.jsoup.nodes.Document doc = Jsoup.parse(article);

        // These fields appear in every document
        String docNumber = doc.select("DOCNO").first().ownText();
        String docId = doc.select("DOCID").first().ownText();

        // These fields do not appear in every document
        // All have inner <p> tag (e.g. <TYPE><P>Wire</P></TYPE>)
        String headLine = getValue(doc, "HEADLINE");
        String byLine = getValue(doc, "BYLINE");
        String type = getValue(doc, "TYPE");
        String date = getValue(doc, "DATE");
        String section = getValue(doc, "SECTION");

        // Body of article has multiple <p> tags
        String contents = getArticleBody(doc, "TEXT");

        // Create Lucene document for article
        Document document = new Document();
        document.add(new TextField("DOCNO", docNumber, Field.Store.YES));
        document.add(new TextField("HEADLINE", headLine, Field.Store.YES));
        document.add(new TextField("TEXT", contents, Field.Store.YES));

        return document;
    }

    private static String getValue(org.jsoup.nodes.Document doc, String tag) {
        Elements element = doc.select(tag);

        if (element.isEmpty()) {
            // Article does not contain tag
            // Return empty string for value
            return "";
        }

        String value = doc.select(tag).select("P").first().ownText();

        return value;
    }

    private static String getArticleBody(org.jsoup.nodes.Document doc, String tag) {
        // Get article contents - made up of multiple <P> tags
        // Concat tag content without tags
        String value = doc.select(tag + " P").text();

        return value;
    }
}
