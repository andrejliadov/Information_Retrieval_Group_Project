package com.assignment2.search;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;

import com.assignment2.query.NumberedQuery;

public class Search {

    public static final String QUERY_FILE = "queries";
    public static final String RESULTS_FILE = "qresults";
    public static final int MAX_RESULTS = 1000;

    private static final String DOCNO = "docno";

    private static void writeResultToFile(FileWriter writer, NumberedQuery query,
            ScoreDoc result, Document doc, int rank) throws IOException {
        // query-id Q0 document-id rank score STANDARD
        StringJoiner resultLine = new StringJoiner(" ");
        resultLine.add( Integer.toString(query.getNumber()) );  // query-id
        resultLine.add( "0" );                                  // Q0
        resultLine.add( doc.get(DOCNO) );                       // document-id
        resultLine.add( Integer.toString(rank) );               // rank
        resultLine.add( Float.toString(result.score) );         // score
        resultLine.add( "STANDARD\n" );                         // STANDARD
        writer.write(resultLine.toString());
    }

    public static void search(Directory directory, List<NumberedQuery> queryList,
            Similarity similarityFunction) throws IOException {

        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        isearcher.setSimilarity(similarityFunction);

        System.out.println("Searching index..");

        File resultsFile = new File(RESULTS_FILE);
        resultsFile.createNewFile();
        FileWriter writer = new FileWriter(RESULTS_FILE);

        File queryFile = new File(QUERY_FILE);
        queryFile.createNewFile();
        FileWriter qWriter = new FileWriter(QUERY_FILE);

        for (NumberedQuery query : queryList) {
            
            System.out.println(query.getQuery().toString());
            
            ScoreDoc[] hits = isearcher.search(query.getQuery(), MAX_RESULTS).scoreDocs;

            System.out.print("Query no. " + query.getNumber());
            System.out.println(" - Results: " + hits.length);

            qWriter.write("[" + query.getNumber() + "] " + query.getQuery() + "\n");

            for (int i = 0; i < hits.length; i++) {
                Document doc = isearcher.doc(hits[i].doc);
                writeResultToFile(writer, query, hits[i], doc, i+1);
            }
        }
        writer.close();
        qWriter.close();
        ireader.close();

        System.out.println("Search complete.\n"
            + "Results have been placed in file: " + RESULTS_FILE);
    }
}
