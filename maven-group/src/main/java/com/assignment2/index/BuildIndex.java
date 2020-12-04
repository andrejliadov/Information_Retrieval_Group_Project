package com.assignment2.index;

import com.assignment2.parser.FBISParser;
import com.assignment2.parser.FRParser;
import com.assignment2.parser.FTParser;
import com.assignment2.parser.LATParser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BuildIndex {
    public static final String INDEX_PATH = "index";

    public static Directory startBuildIndex(Analyzer analyzer) {
        if (analyzer == null) {
            return null;
        }
        System.out.println("------StartBuildIndex------");
        try {
            // init Directory
            String indexPath = analyzer.getClass().getSimpleName() + "_" + INDEX_PATH;

            if (new File(indexPath).exists()) {
               System.out.println("------You already built the index------");
               return FSDirectory.open(Paths.get(indexPath));
            }
            
            Directory directory = FSDirectory.open(Paths.get(indexPath));
            
            // init IndexWriterConfig
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            // init IndexWriter
            IndexWriter indexWriter = new IndexWriter(directory, config);

            // get documents
            List<Document> documentList = getDocuments();

            if (documentList == null || documentList.size() == 0) {
                System.out.println("Fail to get documents");
                return null;
            }
            // write documents
            System.out.println("adding documents");
            for (Document document : documentList) {
                indexWriter.addDocument(document);
            }
            // close closeable objects
            indexWriter.close();
            return directory;
        } catch (Exception e) {
            System.out.println("Fail to build com.task.lucene.index");
            e.printStackTrace();
        }
        
        System.out.println("------EndBuildIndex------");
        return null;
    }

    private static List<Document> getDocuments() {
        List<Document> results = new ArrayList<>();
        try {
            System.out.println("FT Parsing ....");
            FTParser ftParser = new FTParser();
            List<Document> ftDocs = ftParser.readDocuments();
            System.out.println("FT size = " + ftDocs.size());
            System.out.println("FT Parsing Done");
            results.addAll(ftDocs);

            System.out.println("FBIS Parsing .... ");
            List<Document> fbisDocs = FBISParser.getDocuments();
            System.out.println("FBIS size = " + fbisDocs.size());
            System.out.println("FBIS Parsing Done");
            results.addAll(fbisDocs);

            System.out.println("FR Parsing ....");
            FRParser frParser = new FRParser();
            List<Document> frDocs = frParser.readDocuments();
            System.out.println("FR size = " + frDocs.size());
            System.out.println("FR Parsing Done");
            results.addAll(frDocs);

            System.out.println("LA Times parsing ....");
            LATParser laTimes = new LATParser();
            List<Document> laDocs = laTimes.readDocuments();
            System.out.println("LA Times size = " + laDocs.size());
            results.addAll(laDocs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}
