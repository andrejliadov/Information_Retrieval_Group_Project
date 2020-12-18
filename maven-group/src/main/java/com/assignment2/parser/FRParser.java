package com.assignment2.parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

public class FRParser {

    /**
     * DOCS ARE HIERARCHICAL
     * Every document contains: document no., document parent, text
     *
     * the following tags can be present 0 or more times within the text field:
     *
     * USDEPT          The name of the department within the federal government
                           that released the document.
       AGENCY          The name of the government agency within the department.
       USBUREAU        The name of the government service or bureau contributing
                           the document.
       DOCTITLE        The title text of the contribution.
       ADDRESS         The address(es) of the contributing agency.
       FURTHER         The text detailing availability of further information.
       SUMMARY         A precis of the text of the full document. This text is
                           contributed by the agency and appears before the main
                           body of the text.
       ACTION          The function of the article; the reason it is in the
                           federal register. Usually just a few words.
       SIGNER          The signatory of the document.
       SIGNJOB         The title of the signatory.
       SUPPLEM         The supplementary information; the bulk of the document
                           text.
       BILLING         The Federal Register billing code for that section.
       FRFILING        The document's filing details.
       DATE            The effective date and/or time applicable to the text.
       CFRNO           The relevant section of the United States Code.
       RINDOCK         The docket or RIN number of the entry.
     */
    public static final String FR_PATH = "Assignment Two/Assignment Two/fr94/";

    private static final String DOCNO = "docno";
    private static final String PARENT = "parent";
    private static final String TEXT = "text";

    private Elements readDocumentsFromFile(File file) throws IOException {
        Elements docElements = Jsoup
            .parse(file, StandardCharsets.UTF_8.name())
            .getElementsByTag("DOC");
        return docElements;
    }

    private Document processDocument(Analyzer analyzer, Element element) {
        Document document = new Document();

        String docNumber = element.getElementsByTag(DOCNO).text();
        String docParent = element.getElementsByTag(PARENT).text();
        String text = element.getElementsByTag(TEXT).text();

        String text2 = applyMorphology(analyzer, text);

        document.add(new StringField(DOCNO, docNumber, Field.Store.YES));
        document.add(new TextField(TEXT, text2 + " " + docParent, Field.Store.YES));

        return document;
    }

    public List<Document> readDocuments(Analyzer analyzer) throws IOException {
        final File dir = new File(FR_PATH);
        final List<Document> documentList = new ArrayList<Document>();

        File[] dirFiles = dir.listFiles();
        Arrays.sort(dirFiles);

        for (final File subDir : dirFiles) {

            if (subDir.isDirectory()) {
                File[] subDirFiles = subDir.listFiles();
                Arrays.sort(subDirFiles);

                for (final File dataFile : subDirFiles) {
                    Elements docs = readDocumentsFromFile(dataFile);
                    docs.forEach(doc ->
                        documentList.add(processDocument(analyzer, doc))
                    );
                }
            }
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
        }
    }
}
