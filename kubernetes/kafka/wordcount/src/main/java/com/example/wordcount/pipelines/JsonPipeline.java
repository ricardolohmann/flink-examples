package com.example.wordcount.pipelines;

import com.example.wordcount.KafkaConsumerFactory;
import com.example.wordcount.KafkaProducerFactory;
import com.example.wordcount.serialization.WordCounterSerializer;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

public class JsonPipeline {

    private final StreamExecutionEnvironment env;

    private final String bootstrapServers;

    private final String inputTopic;

    private final String outputTopic;

    public JsonPipeline(StreamExecutionEnvironment env, String bootstrapServers, String inputTopic,
                        String outputTopic) {
        this.env = env;
        this.bootstrapServers = bootstrapServers;
        this.inputTopic = inputTopic;
        this.outputTopic = outputTopic;
    }

    public void create() {
        // create kafka consumer and producer factories
        KafkaConsumerFactory consumerFactory = new KafkaConsumerFactory(bootstrapServers, inputTopic);
        KafkaProducerFactory producerFactory = new KafkaProducerFactory(bootstrapServers, outputTopic);

        DataStream<ObjectNode> words = env.addSource(consumerFactory.createJsonConsumer());
        words.print("Print words");

        DataStream<Tuple2<String, Integer>> counts = words
                // split up the lines in pairs (2-tuples) containing: (word,1)
                .flatMap(new Tokenizer())
                // group by the tuple field "0"
                .keyBy(word -> word.f0)
                // create 1 minute window
                .timeWindow(Time.minutes(1))
                // sum up tuple field "1"
                .sum(1);

        counts.print("Print counters");
        // send counters to Kafka
        counts.addSink(producerFactory.createJsonProducer(new WordCounterSerializer()));
    }

    public static final class Tokenizer implements FlatMapFunction<ObjectNode, Tuple2<String, Integer>> {

        @Override
        public void flatMap(ObjectNode value, Collector<Tuple2<String, Integer>> out) {
            // `value` will be something like the following
            // { "value": { "word":"second third" } }
            // normalize and split line
            String[] words = value.get("value").get("word").asText().toLowerCase().split("\\W+");

            // emit word and counter
            for (String word : words) {
                if (word.length() > 0) {
                    out.collect(Tuple2.of(word, 1));
                }
            }
        }
    }
}
