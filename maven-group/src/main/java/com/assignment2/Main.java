package com.assignment2;

import com.assignment2.parser.FBISParser;
import com.assignment2.parser.FRParser;

import org.apache.lucene.document.Document;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class Main {

    public static final String FT_PATH = "Assignment Two/Assignment Two/ft/";
    public static final String FBIS_PATH = "Assignment Two/Assignment Two/fbis/";
    public static final String FR_PATH = "Assignment Two/Assignment Two/fr94/";
    
    public static URL getResource(String path) {
        return Thread.currentThread().getContextClassLoader().getResource(path);
    }
    
	public static void main(String[] args) throws IOException, URISyntaxException {
        URL ftPath = getResource(FT_PATH);
        URL fbisPath = getResource(FBIS_PATH);
        URL frPath = getResource(FR_PATH);
	    
		FTParser ftParser = new FTParser(ftPath);
		ftParser.parseDocs();
		System.out.println("FT Parsing Done");

		System.out.println("FBIS Parsing .... ");
		List<Document> fbisDocs = FBISParser.getDocuments(fbisPath);
		System.out.println("FBIS size = " + fbisDocs.size());
		System.out.println("FBIS Parsing Done");
		
		System.out.println("FR Parsing ....");
		FRParser frParser = new FRParser();
		List<Document> frDocs = frParser.readDocuments(frPath);
		System.out.println("FR size = " + frDocs.size());
        System.out.println("FR Parsing Done");
	}
}
