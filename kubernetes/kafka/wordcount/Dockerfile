FROM flink:1.11.0-scala_2.12

# Copy jar file
COPY build/libs/wordcount.jar $FLINK_HOME/usrlib/wordcount.jar

# Copy log4j config
COPY log4j.properties $FLINK_HOME/conf/log4j.properties