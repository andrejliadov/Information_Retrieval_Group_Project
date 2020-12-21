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

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class BuildIndex {
    public static final String INDEX_PATH = "index_morph";

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
            List<Document> documentList = getDocuments(analyzer);

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

    private static List<Document> getDocumentsOld(Analyzer analyzer) {
        List<Document> results = new ArrayList<>();
        try {
            System.out.println("FT Parsing ....");
            FTParser ftParser = new FTParser();
            List<Document> ftDocs = ftParser.readDocuments(analyzer);
            System.out.println("FT size = " + ftDocs.size());
            System.out.println("FT Parsing Done");
            results.addAll(ftDocs);

            System.out.println("FBIS Parsing .... ");
            List<Document> fbisDocs = FBISParser.getDocuments(analyzer);
            System.out.println("FBIS size = " + fbisDocs.size());
            System.out.println("FBIS Parsing Done");
            results.addAll(fbisDocs);

            System.out.println("FR Parsing ....");
            FRParser frParser = new FRParser();
            List<Document> frDocs = frParser.readDocuments(analyzer);
            System.out.println("FR size = " + frDocs.size());
            System.out.println("FR Parsing Done");
            results.addAll(frDocs);

            System.out.println("LA Times parsing ....");
            LATParser laTimes = new LATParser();
            List<Document> laDocs = laTimes.readDocuments(analyzer);
            System.out.println("LA Times size = " + laDocs.size());
            System.out.println("LA Parsing Done");
            results.addAll(laDocs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private static List<Document> getDocuments(Analyzer analyzer) {
        List<Document> results = Collections.synchronizedList(new ArrayList<>());
        try {
            CountDownLatch countDownLatch = new CountDownLatch(4);
            ExecutorService executor = Executors.newFixedThreadPool(4);
            executor.execute(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + "-FT Parsing ....");
                    FTParser ftParser = new FTParser();
                    List<Document> ftDocs = ftParser.readDocuments(analyzer);
                    System.out.println("FT size = " + ftDocs.size());
                    System.out.println("FT Parsing Done");
                    results.addAll(ftDocs);
                    countDownLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            executor.execute(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + "-FBIS Parsing .... ");
                    List<Document> fbisDocs = FBISParser.getDocuments(analyzer);
                    System.out.println("FBIS size = " + fbisDocs.size());
                    System.out.println("FBIS Parsing Done");
                    results.addAll(fbisDocs);
                    countDownLatch.countDown();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            executor.execute(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + "-FR Parsing ....");
                    FRParser frParser = new FRParser();
                    List<Document> frDocs = frParser.readDocuments(analyzer);
                    System.out.println("FR size = " + frDocs.size());
                    System.out.println("FR Parsing Done");
                    results.addAll(frDocs);
                    countDownLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            executor.execute(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + "-LA Times parsing ....");
                    LATParser laTimes = new LATParser();
                    List<Document> laDocs = laTimes.readDocuments(analyzer);
                    System.out.println("LA Times size = " + laDocs.size());
                    System.out.println("LA Parsing Done");
                    results.addAll(laDocs);
                    countDownLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}
