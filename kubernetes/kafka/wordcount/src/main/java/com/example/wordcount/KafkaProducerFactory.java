package com.example.wordcount;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.serialization.TypeInformationSerializationSchema;
import org.apache.flink.api.java.tuple.Tuple2;
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

    public FlinkKafkaProducer<Tuple2<String, Integer>> createJsonProducer(
            SerializationSchema<Tuple2<String, Integer>> serializer) {
        Properties producerProperties = new Properties();
        producerProperties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new FlinkKafkaProducer<>(topicName, serializer, producerProperties);
    }
}
