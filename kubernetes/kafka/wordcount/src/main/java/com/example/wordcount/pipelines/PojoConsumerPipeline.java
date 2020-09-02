package com.example.wordcount.pipelines;

import com.example.wordcount.KafkaConsumerFactory;
import com.example.wordcount.Message;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;

public class PojoConsumerPipeline {

    private final StreamExecutionEnvironment env;

    private final String bootstrapServers;

    private final String inputTopic;

    public PojoConsumerPipeline(StreamExecutionEnvironment env, String bootstrapServers, String inputTopic) {
        this.env = env;
        this.bootstrapServers = bootstrapServers;
        this.inputTopic = inputTopic;
    }

    public void create() {
        // create kafka consumer and producer factories
        KafkaConsumerFactory consumerFactory = new KafkaConsumerFactory(bootstrapServers, inputTopic);

        DataStream<Message> messages = env.addSource(consumerFactory.createPojoConsumer());
        messages.print("POJO Consumed messages");

        DataStream<Message> counts = messages
                // group by recipient
                .keyBy(message -> message.recipient)
                // create 1 minute window
                .timeWindow(Time.minutes(1))
                // sum up tuple field "1"
                .reduce((message1, message2) -> {
                    String message = message1.message + "\n" + message2.message;
                    return new Message(message1.recipient, message);
                });

        // convert objects to string and print them
        counts.map(Message::toString).print("Print counters");
    }
}
