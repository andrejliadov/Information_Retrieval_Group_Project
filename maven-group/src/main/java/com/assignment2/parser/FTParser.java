package com.assignment2.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.analyzer.MorphologyFilter;

public class FTParser
{
    private static final String DATASET_FOLDER = "Assignment Two/Assignment Two/ft";

    public List<Document> readDocuments(Analyzer analyzer) throws IOException
    {
        final File dir = new File(DATASET_FOLDER);

        File[] dirFiles = dir.listFiles();
        Arrays.sort(dirFiles);

        // This will store all articles across all files
        ArrayList<Document> documents = new ArrayList<Document>();

        for (final File subDir : dirFiles) {

            if (subDir.isDirectory()) {
                File[] subDirFiles = subDir.listFiles();
                Arrays.sort(subDirFiles);

                if (subDirFiles != null) {
                    for (File file : subDirFiles) {
                        try {
                            if(file.getName().contains(".txt")) {
                                // Ignore readchg.txt and readmela.txt
                                continue;
                            }

                            org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(file, "UTF-8");

                            Elements docTags = jsoupDoc.getElementsByTag("DOC");

                            for (Element docTag : docTags) {
                                Document doc = getElements(analyzer, docTag);
                                documents.add(doc);
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }
            }
        }

        return documents;
    }

    private static Document getElements(Analyzer analyzer, org.jsoup.nodes.Element doc) {
        try {
        // These fields appear in every document
        String docNumber = doc.select("DOCNO").first().ownText();

        // These fields do not appear in every document
        String headLine = getValue(doc, "HEADLINE");

        // Body of article has multiple <p> tags
        String contents = getArticleBody(doc, "TEXT");

        String content2 = applyMorphology(analyzer, contents);

        // Create Lucene document for article
        Document document = new Document();
        document.add(new StringField("docno", docNumber, Field.Store.YES));
        document.add(new TextField("text", content2 + " " + headLine, Field.Store.YES));

        return document;
        } catch (Exception e) {
            System.out.println(e);
            Document document = new Document();
            return document;
        }

    }

    private static String applyMorphology(Analyzer analyzer, String contents) {
        try {
            LuceneMorphology luceneMorph = new EnglishLuceneMorphology();

            TokenStream tokenStream = analyzer.tokenStream("TEST", contents);
            TokenStream new_contents = new MorphologyFilter(tokenStream, luceneMorph);

            String result = "";

            CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while(tokenStream.incrementToken()) {
               result = result + " " + attr.toString();
            }

            tokenStream.end();
            tokenStream.close();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getValue(Element doc, String tag) {
        Elements element = doc.select(tag);

        if (element.isEmpty()) {
            // Article does not contain tag
            // Return empty string for value
            return "";
        }

        String value = doc.select(tag).first().ownText();

        return value;
    }

    private static String getArticleBody(Element doc, String tag) {
        // Get article contents - made up of multiple <P> tags
        // Concat tag content without tags
        String value = doc.select(tag).text();

        return value;
    }
}
