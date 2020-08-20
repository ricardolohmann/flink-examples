package com.example.wordcount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.util.Properties;

public class WordCount {

    public static void main(String[] args) throws Exception {
        // Checking input parameters
        final ParameterTool params = ParameterTool.fromArgs(args);
        String inputTopic = params.getRequired("input-topic");
        String outputTopic = params.getRequired("output-topic");
        String kafkaEndpoint = params.getRequired("kafka-endpoint");

        // set up the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // make parameters available in the web interface
        env.getConfig().setGlobalJobParameters(params);

        // create kafka consumer
        Properties consumerProperties = new Properties();
        consumerProperties.setProperty("bootstrap.servers", kafkaEndpoint);
        consumerProperties.setProperty("group.id", "test");
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(inputTopic, new SimpleStringSchema(),
                consumerProperties);

        // create the pipeline
        DataStream<String> counts = env.addSource(consumer)
                // split up the lines in pairs (2-tuples) containing: (word,1)
                .flatMap(new Tokenizer())
                // group by the tuple field "0"
                .keyBy(word -> word.f0)
                // create 1 minute window
                .timeWindow(Time.minutes(1))
                // sum up tuple field "1"
                .sum(1)
                // convert to a simple string
                .map(Tuple2::toString);

        // create kafka producer
        Properties producerProperties = new Properties();
        producerProperties.setProperty("bootstrap.servers", kafkaEndpoint);
        FlinkKafkaProducer<String> producer = new FlinkKafkaProducer<>(outputTopic, new SimpleStringSchema(),
                producerProperties);

        counts.addSink(producer);

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
            String[] words = value.toLowerCase().split("\\W+");

            // emit word and counter
            for (String word : words) {
                if (word.length() > 0) {
                    out.collect(new Tuple2<>(word, 1));
                }
            }
        }
    }
}
