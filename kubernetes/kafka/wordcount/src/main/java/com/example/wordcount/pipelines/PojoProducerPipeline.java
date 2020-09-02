package com.example.wordcount.pipelines;

import com.example.wordcount.KafkaConsumerFactory;
import com.example.wordcount.KafkaProducerFactory;
import com.example.wordcount.Message;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class PojoProducerPipeline {

    private final StreamExecutionEnvironment env;

    private final String bootstrapServers;

    private final String inputTopic;

    private final String outputTopic;

    public PojoProducerPipeline(StreamExecutionEnvironment env, String bootstrapServers, String inputTopic,
                                String outputTopic) {
        this.env = env;
        this.bootstrapServers = bootstrapServers;
        this.inputTopic = inputTopic;
        this.outputTopic = outputTopic;
    }

    public void create() {
        KafkaConsumerFactory consumerFactory = new KafkaConsumerFactory(bootstrapServers, inputTopic);
        KafkaProducerFactory producerFactory = new KafkaProducerFactory(bootstrapServers, outputTopic);

        DataStream<Message> messages = env.addSource(consumerFactory.createStringConsumer())
                .map(text -> {
                    // the expected text format is: "recipient,my message"
                    String[] values = text.split(",");
                    return new Message(values[0], values[1]);
                });
        messages.print("POJO Produced messages");
        messages.addSink(producerFactory.createPojoProducer());
    }
}
