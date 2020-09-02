package com.example.wordcount;

import com.example.wordcount.pipelines.JsonPipeline;
import com.example.wordcount.pipelines.PojoConsumerPipeline;
import com.example.wordcount.pipelines.PojoProducerPipeline;
import com.example.wordcount.pipelines.StringPipeline;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class WordCount {

    public static String JSON_PIPELINE_TYPE = "json";

    public static String STRING_PIPELINE_TYPE = "string";

    public static String POJO_PIPELINE_TYPE = "pojo";

    public static void main(String[] args) throws Exception {
        // Checking input parameters
        final ParameterTool params = ParameterTool.fromArgs(args);
        String inputTopic = params.getRequired("input-topic");
        String outputTopic = params.getRequired("output-topic");
        String bootstrapServers = params.getRequired("kafka-endpoint");
        String pipelineType = params.getRequired("pipeline-type");

        // set up the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // create pipeline
        if (pipelineType.equals(STRING_PIPELINE_TYPE)) {
            StringPipeline pipeline = new StringPipeline(env, bootstrapServers, inputTopic, outputTopic);
            pipeline.create();
        } else if (pipelineType.equals(JSON_PIPELINE_TYPE)) {
            JsonPipeline pipeline = new JsonPipeline(env, bootstrapServers, inputTopic, outputTopic);
            pipeline.create();
        } else if (pipelineType.equals(POJO_PIPELINE_TYPE)) {
            // use `PojoProducerPipeline` to produce data and `PojoConsumerPipeline` to consume
            PojoProducerPipeline producerPipeline = new PojoProducerPipeline(env, bootstrapServers, inputTopic,
                    outputTopic);
            producerPipeline.create();

            PojoConsumerPipeline consumerPipeline = new PojoConsumerPipeline(env, bootstrapServers, outputTopic);
            consumerPipeline.create();
        }

        // execute program
        env.execute(String.format("Streaming Word Count %s", pipelineType));
    }
}
