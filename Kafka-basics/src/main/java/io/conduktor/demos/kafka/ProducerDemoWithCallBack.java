package io.conduktor.demos.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallBack {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemoWithCallBack.class.getSimpleName());

    public static void main(String[] args) {
        log.info("I'm a Kafka Producer");

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the Producer
        KafkaProducer<String,String> producer = new KafkaProducer<>(properties);

        for (int i = 0; i<10; i++){
            // create a producer record
            ProducerRecord<String, String> producerRecord =
                    new ProducerRecord<>("demo_java","hello_world " + i);

            // send the data - asynchronous
            producer.send(producerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception e) {
                    //executes every time a message is sent successfully or an exception is thrown
                    if (e == null) {
                        // the record was successfully sent
                        log.info("Received new metadata / \n" +
                                "Topic: " + metadata.topic() + "\n" +
                                "Partition: " + metadata.partition()  + "\n" +
                                "Offset: " + metadata.offset() + "\n" +
                                "Timestamp: " + metadata.timestamp());
                    } else {
                        log.error("Error while producing", e);
                    }


                }
            });

            // metamos un sleep para que cada mensaje vaya a una partición distinta
            // si los mandas sin un tiempo de espera entre ellos Kafka usa Batch method internamente
            // y los envía todos a la misma partición para un mejor rendimiento
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }


        // flush dad - synchronous
        producer.flush();;

        // flush and close producer
        producer.close();



    }
}
