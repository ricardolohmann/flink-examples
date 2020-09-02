package com.example.wordcount.serialization;

import com.example.wordcount.Message;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MessageSchema implements SerializationSchema<Message>, DeserializationSchema<Message> {

    public static ObjectMapper objectMapper = new ObjectMapper();

    Logger logger = LoggerFactory.getLogger(MessageSchema.class);

    @Override
    public byte[] serialize(Message message) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        try {
            return objectMapper.writeValueAsString(message).getBytes();
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON", e);
        }
        return new byte[0];
    }

    @Override
    public Message deserialize(byte[] bytes) throws IOException {
        return objectMapper.readValue(bytes, Message.class);
    }

    @Override
    public boolean isEndOfStream(Message inputMessage) {
        return false;
    }

    @Override
    public TypeInformation<Message> getProducedType() {
        return TypeInformation.of(Message.class);
    }
}