package io.conduktor.demos.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoKeys {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemoKeys.class.getSimpleName());

    public static void main(String[] args) {
        log.info("I'm a Kafka Producer");

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the Producer
        KafkaProducer<String,String> producer = new KafkaProducer<>(properties);

        int min = 1;
        int max = 15;

        for (int i = 0; i<10; i++){

            int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);

            String topic = "demo_java";
            String value = "Hello World again " + i*i*random_int;
            String key = "id_" + String.valueOf(random_int) +i;

            // create a producer record
            ProducerRecord<String, String> producerRecord =
                    new ProducerRecord<>(topic, key, value);

            // send the data - asynchronous
            producer.send(producerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception e) {
                    //executes every time a message is sent successfully or an exception is thrown
                    if (e == null) {
                        // the record was successfully sent
                        log.info("Received new metadata / \n" +
                                "Topic: " + metadata.topic() + "\n" +
                                "Key: " + producerRecord.key() + "\n" +
                                "Partition: " + metadata.partition()  + "\n" +
                                "Offset: " + metadata.offset() + "\n" +
                                "Timestamp: " + metadata.timestamp());
                    } else {
                        log.error("Error while producing", e);
                    }


                }
            });

        }


        // flush dad - synchronous
        producer.flush();;

        // flush and close producer
        producer.close();



    }
}
