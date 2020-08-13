package com.example.wordcount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class WordCount {

    public static void main(String[] args) throws Exception {
        // Checking input parameters
        final ParameterTool params = ParameterTool.fromArgs(args);

        // set up the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // make parameters available in the web interface
        env.getConfig().setGlobalJobParameters(params);

        // get input data
        String input = params.getRequired("input");

        DataStream<Tuple2<String, Integer>> counts = env.readTextFile(input)
                // split up the lines in pairs (2-tuples) containing: (word,1)
                .flatMap(new Tokenizer())
                // group by the tuple field "0" and sum up tuple field "1"
                .keyBy(word -> word.f0)
                // sum values
                .sum("f1");

        // emit result
        counts.writeAsCsv(params.getRequired("output"));

        // execute program
        env.execute("Streaming WordCount");
    }

    /**
     * Implements the string tokenizer that splits sentences into words as a user-defined FlatMapFunction. The
     * function takes a line (String) and splits it into multiple pairs in the form of "(word,1)"
     * ({@code Tuple2<String, Integer>}).
     */
    public static final class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            // normalize and split line
            String[] tokens = value.toLowerCase().split("\\W+");

            // emit word and counter
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<>(token, 1));
                }
            }
        }
    }
}
