package com.example.wordcount;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.api.java.tuple.Tuple2;

public class WordCounter extends Tuple2<String, Integer> {

    @JsonProperty
    public String word;

    @JsonProperty
    public int count;

    public WordCounter() { }

    public WordCounter(String word, int count) {
        super(word, count);
        this.word = word;
        this.count = count;
    }
}
