package com.example.wordcount;

import com.example.wordcount.serialization.MessageSchema;
import com.example.wordcount.serialization.WordCounterSerializer;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.serialization.TypeInformationSerializationSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.kafka.clients.CommonClientConfigs;

import java.util.Properties;

public class KafkaProducerFactory {

    private final String bootstrapServers;

    private final String topicName;

    public KafkaProducerFactory(String bootstrapServers, String topicName) {
        this.bootstrapServers = bootstrapServers;
        this.topicName = topicName;
    }

    public FlinkKafkaProducer<String> createStringProducer() {
        Properties producerProperties = new Properties();
        producerProperties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new FlinkKafkaProducer<>(topicName, new SimpleStringSchema(), producerProperties);
    }

    public FlinkKafkaProducer<Tuple2<String, Integer>> createJsonProducer() {
        Properties producerProperties = new Properties();
        producerProperties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new FlinkKafkaProducer<>(topicName, new WordCounterSerializer(), producerProperties);
    }

    public FlinkKafkaProducer<Message> createPojoProducer() {
        Properties producerProperties = new Properties();
        producerProperties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new FlinkKafkaProducer<>(topicName, new MessageSchema(), producerProperties);
    }
}
