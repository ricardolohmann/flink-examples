package com.example.wordcount.serialization;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.java.tuple.Tuple2;

public class WordCounterSerializer implements SerializationSchema<Tuple2<String, Integer>> {

    @Override
    public byte[] serialize(Tuple2<String, Integer> element) {
        String json = String.format("{\"word\": \"%s\", \"count\": %s}", element.f0, element.f1);
        return json.getBytes();
    }
}
