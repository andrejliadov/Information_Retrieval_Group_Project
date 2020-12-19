package com.assignment2.parser;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.analyzer.MorphologyFilter;

/**
 * Parser class for parsing fbis documents
 */
public class FBISParser {
    // the path of fbis documents
    public static final String FBIS_PATH = "Assignment Two/Assignment Two/fbis";

    /**
     * get all the lucene documents from raw data
     */
    public static List<Document> getDocuments(Analyzer analyzer) {
        List<Document> documentList = new ArrayList<Document>();
        try {
            File dir = new File(FBIS_PATH);
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                List<Document> documents = parseSingleFBIS(analyzer, files[i]);
                if (documents != null) {
                    documentList.addAll(documents);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentList;
    }

    /**
     * get the lucene documents from a single file
     */
    public static List<Document> parseSingleFBIS(Analyzer analyzer, File file) {

        // skip readchg.txt and readmefb.txt
        if (file == null || file.getName().startsWith("read")) {
            return null;
        }
        List<Document> documentList = new ArrayList<Document>();

        // using Jsoup to parse HTML format
        try {
            org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(file, "UTF-8");
            Elements docTags = jsoupDoc.getElementsByTag("DOC");

            for (Element docTag : docTags) {
                String text = docTag.getElementsByTag("TEXT").text();
                String title = docTag.getElementsByTag("TI").text();
                String docno = docTag.getElementsByTag("DOCNO").text();
                String date = docTag.getElementsByTag("DATE1").text();

                String text2 = applyMorphology(analyzer, text);

                Document luceneDoc = new Document();
                luceneDoc.add(new StringField("docno", docno, Field.Store.YES));
                luceneDoc.add(new TextField("text", text2 + " " + date + " " + title, Field.Store.YES));

                documentList.add(luceneDoc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentList;
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

            return "";
        }


    }
}
