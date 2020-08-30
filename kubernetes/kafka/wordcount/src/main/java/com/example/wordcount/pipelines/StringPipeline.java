package com.example.wordcount.pipelines;

import com.example.wordcount.KafkaConsumerFactory;
import com.example.wordcount.KafkaProducerFactory;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

public class StringPipeline {

    private final StreamExecutionEnvironment env;

    private final String bootstrapServers;

    private final String inputTopic;

    private final String outputTopic;

    public StringPipeline(StreamExecutionEnvironment env, String bootstrapServers, String inputTopic,
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

        DataStream<String> words = env.addSource(consumerFactory.createStringConsumer());
        words.print("Print words");

        DataStream<String> counts = words
                // split up the lines in pairs (2-tuples) containing: (word,1)
                .flatMap(new Tokenizer())
                // group by the tuple field "0"
                .keyBy(word -> word.f0)
                // create 1 minute window
                .timeWindow(Time.minutes(1))
                // sum up tuple field "1"
                .sum(1)
                // convert tuples to string
                .map(Tuple2::toString);

        counts.print("Print counters");

        // send counters to Kafka
        counts.addSink(producerFactory.createStringProducer());
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
                    out.collect(Tuple2.of(word, 1));
                }
            }
        }
    }
}
