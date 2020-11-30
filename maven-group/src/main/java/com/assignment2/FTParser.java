package com.assignment2;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FTParser {

    public static final String FT_PATH = "Assignment Two/Assignment Two/ft/";
	private static ArrayList<File> fileList;

	//All of the different fields are stored here
	public static ArrayList<String> DocNums;
	public static ArrayList<String> Dates;
	public static ArrayList<String> Profiles;
	public static ArrayList<String> Headlines;
	public static ArrayList<String> Texts;
	public static ArrayList<String> Publications;
	public static ArrayList<String> PageNumbers;
	
	FTParser() {
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		fileList = walkFileTreeExample(FT_PATH);
		
		DocNums = new ArrayList<String>();
		Dates = new ArrayList<String>();
		Profiles = new ArrayList<String>();
		Headlines = new ArrayList<String>();
		Texts = new ArrayList<String>();
		Publications = new ArrayList<String>();
		PageNumbers = new ArrayList<String>();
	}
	
	public void parseDocs() throws IOException {
		for(File file : fileList) {
			Document doc = Jsoup.parse(file, "UTF-8");
			
			parseDocNums(doc);
			parseDates(doc);
			parseProfiles(doc);
			parseHeadlines(doc);
			parseTexts(doc);
			parsePublications(doc);
			parsePages(doc);
		}
	}
	
	private ArrayList<File> walkFileTreeExample(String dirName) {
		ArrayList<File> fileList = new ArrayList<File>();
		File[] files = new File(dirName).listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				System.out.println("Directory: " + file.getName());
				fileList = walkFileTreeExample(dirName+"/"+file.getName()); // Calls same method again.
			} else {
				fileList.add(file);
			}
		}
		
		return fileList;
	}

	private static void parseDocNums(Document doc){
		Elements docIDs = doc.getElementsByTag("DOCNO");
		for(Element docID : docIDs) {
			DocNums.add(docID.text());
		}
	}

	private static void parseDates(Document doc){
		Elements docIDs = doc.getElementsByTag("DATE");
		for(Element docID : docIDs) {
			Dates.add(docID.text());
		}
	}

	private static void parseProfiles(Document doc){
		Elements docIDs = doc.getElementsByTag("PROFILE");
		for(Element docID : docIDs) {
			Profiles.add(docID.text());
		}
	}

	private static void parseHeadlines(Document doc){
		Elements docIDs = doc.getElementsByTag("HEADLINE");
		for(Element docID : docIDs) {
			Headlines.add(docID.text());
		}
	}

	private static void parseTexts(Document doc){
		Elements docIDs = doc.getElementsByTag("TEXT");
		for(Element docID : docIDs) {
			Texts.add(docID.text());
		}
	}

	private static void parsePublications(Document doc){
		Elements docIDs = doc.getElementsByTag("PUB");
		for(Element docID : docIDs) {
			Publications.add(docID.text());
		}
	}

	private static void parsePages(Document doc){
		Elements docIDs = doc.getElementsByTag("PAGE");
		for(Element docID : docIDs) {
			PageNumbers.add(docID.text());
		}
	}
}
