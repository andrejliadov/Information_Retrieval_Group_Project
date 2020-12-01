package com.assignment2;

import com.assignment2.parser.FBISParser;
import com.assignment2.parser.FRParser;
import com.assignment2.parser.FTParser;
import com.assignment2.parser.LATParser;

import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        // FTParser ftParser = new FTParser();
        // ftParser.parseDocs();
        // System.out.println("FT Parsing Done");

        System.out.println("FT Parsing ....");
        FTParser ftParser = new FTParser();
        List<Document> ftDocs = ftParser.readDocuments();
        System.out.println("FT size = " + ftDocs.size());
        System.out.println("FT Parsing Done");

        System.out.println("FBIS Parsing .... ");
        List<Document> fbisDocs = FBISParser.getDocuments();
        System.out.println("FBIS size = " + fbisDocs.size());
        System.out.println("FBIS Parsing Done");

        System.out.println("FR Parsing ....");
        FRParser frParser = new FRParser();
        List<Document> frDocs = frParser.readDocuments();
        System.out.println("FR size = " + frDocs.size());
        System.out.println("FR Parsing Done");

        System.out.println("LA Times parsing ....");
        LATParser laTimes = new LATParser();
        List<Document> laDocs = laTimes.readDocuments();
        System.out.println("LA Times size = " + laDocs.size());
	}
}
