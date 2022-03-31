package io.conduktor.demos.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemo {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

    public static void main(String[] args) {
        log.info("Hello World");

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the Producer
        KafkaProducer<String,String> producer = new KafkaProducer<>(properties);

        // create a producer record
        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>("demo_java","hello_world");

        // send the data - asynchronous
        producer.send(producerRecord);

        // flush dad - synchronous
        producer.flush();;

        // flush and close producer
      //  producer.close();

        // just for fun
        int min = 10;
        int max = 100;

        for (int i = 1; i <11; i++){
            log.info("Random value in int from "+min+" to "+max+ ":");
            int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);
            log.info("sending msg" + random_int);
            producerRecord = new ProducerRecord<>("demo_java",String.valueOf(random_int));
            producer.send(producerRecord);
            producer.flush();

            try {
                Thread.sleep(500);
            } catch (Exception exception){}

        }

        producer.close();

    }
}
