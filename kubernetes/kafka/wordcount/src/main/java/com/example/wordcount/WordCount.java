package com.example.wordcount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaSerializationSchemaWrapper;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;
import org.apache.flink.streaming.util.serialization.TypeInformationKeyValueSerializationSchema;
import org.apache.flink.util.Collector;

import java.util.Properties;

public class WordCount {

    public static void main(String[] args) throws Exception {
        // Checking input parameters
        final ParameterTool params = ParameterTool.fromArgs(args);

        // set up the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // make parameters available in the web interface
        env.getConfig().setGlobalJobParameters(params);

        // create kafka consumer
        Properties consumerProperties = new Properties();
        consumerProperties.setProperty("bootstrap.servers", "kafka:9092");
        consumerProperties.setProperty("group.id", "test");
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(params.getRequired("input-topic"),
                new SimpleStringSchema(), consumerProperties);

        // create the pipeline
        DataStream<WordCounter> counts = env.addSource(consumer)
                // split up the lines in pairs (2-tuples) containing: (word,1)
                .flatMap(new Tokenizer())
                // group by the tuple field "0" and sum up tuple field "1"
                .keyBy(word -> word.f0)
                // sum values
                .sum("f1");

        // create kafka producer
        String topicName = params.getRequired("output-topic");
        KafkaSerializationSchemaWrapper schema = new KafkaSerializationSchemaWrapper(topicName, null, true,
                new SimpleStringSchema());
        Properties producerProperties = new Properties();
        producerProperties.setProperty("bootstrap.servers", "localhost:9092");
        FlinkKafkaProducer<WordCounter> producer = new FlinkKafkaProducer<WordCounter>(
                topicName,
                schema,
                producerProperties,
                FlinkKafkaProducer.Semantic.EXACTLY_ONCE);

        counts.addSink(producer);

        // execute program
        env.execute("Streaming WordCount");
    }

    /**
     * Implements the string tokenizer that splits sentences into words as a user-defined FlatMapFunction. The
     * function takes a line (String) and splits it into multiple pairs in the form of "(word,1)"
     * ({@code Tuple2<String, Integer>}).
     */
    public static final class Tokenizer implements FlatMapFunction<String, WordCounter> {

        @Override
        public void flatMap(String value, Collector<WordCounter> out) {
            // normalize and split line
            String[] tokens = value.toLowerCase().split("\\W+");

            // emit word and counter
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new WordCounter(token, 1));
                }
            }
        }
    }
}
