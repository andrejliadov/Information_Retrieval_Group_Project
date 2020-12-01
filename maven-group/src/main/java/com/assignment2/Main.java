package com.assignment2;

import com.assignment2.index.BuildIndex;
import com.assignment2.parser.FBISParser;
import com.assignment2.parser.FRParser;
import com.assignment2.parser.FTParser;
import com.assignment2.parser.LATParser;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        // FTParser ftParser = new FTParser();
        // ftParser.parseDocs();
        // System.out.println("FT Parsing Done");
        BuildIndex.startBuildIndex(new StandardAnalyzer());
	}
}
