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

/**
 * Parser class for parsing fbis documents
 */
public class FBISParser {
    // the path of fbis documents
    public static final String FBIS_PATH = "Assignment Two/Assignment Two/fbis";

    /**
     * get all the lucene documents from raw data
     */
    public static List<Document> getDocuments() {
        List<Document> documentList = new ArrayList<Document>();
        try {
            File dir = new File(FBIS_PATH);
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                List<Document> documents = parseSingleFBIS(files[i]);
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
    public static List<Document> parseSingleFBIS(File file) {

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

                Document luceneDoc = new Document();
                luceneDoc.add(new StringField("DOCNO", docno, Field.Store.YES));
                luceneDoc.add(new TextField("date", date, Field.Store.YES));
                luceneDoc.add(new TextField("title", title, Field.Store.YES));
                luceneDoc.add(new TextField("text", text, Field.Store.YES));

                documentList.add(luceneDoc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentList;
    }
}
