package com.assignment2;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		FTParser ftParser = new FTParser();
		ftParser.parseDocs();
		System.out.println("FT Parsing Done");
	}

}
