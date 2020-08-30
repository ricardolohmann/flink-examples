package com.example.wordcount;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;

public class KafkaConsumerFactory {

    private final String bootstrapServers;

    private final String topicName;

    public KafkaConsumerFactory(String bootstrapServers, String topicName) {
        this.bootstrapServers = bootstrapServers;
        this.topicName = topicName;
    }

    public FlinkKafkaConsumer<String> createStringConsumer() {
        Properties consumerProperties = new Properties();
        consumerProperties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "word_count_string");
        return new FlinkKafkaConsumer<>(topicName, new SimpleStringSchema(), consumerProperties);
    }

    public FlinkKafkaConsumer<ObjectNode> createJsonConsumer() {
        Properties consumerProperties = new Properties();
        consumerProperties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "word_count_json");
        return new FlinkKafkaConsumer<>(topicName, new JSONKeyValueDeserializationSchema(false), consumerProperties);
    }
}
