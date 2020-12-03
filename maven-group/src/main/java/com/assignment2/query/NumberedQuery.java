package com.assignment2.query;

import org.apache.lucene.search.Query;

public class NumberedQuery {

    private int number;
    private Query query;
    
    public NumberedQuery(int number, Query query) {
        this.number = number;
        this.query = query;
    }
    
    public int getNumber() {
        return number;
    }
    
    public Query getQuery() {
        return query;
    }
}
