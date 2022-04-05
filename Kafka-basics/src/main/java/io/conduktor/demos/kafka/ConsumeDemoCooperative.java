package io.conduktor.demos.kafka;


import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumeDemoCooperative {

    private static final Logger log = LoggerFactory.getLogger(ConsumeDemoCooperative.class.getSimpleName());

    public static void main(String[] args) {
        log.info("I'm a Kafka Consumer");

        String bootStrapServers = "127.0.0.1:9092";
        String groupId = "my-third-application";
        String topic = "demo_java";


        // create Consumer properties
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // none/earliest/latest
        properties.setProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());

        // create Kafka Consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // get a reference of the current thread
        final Thread mainThread = Thread.currentThread();

        // adding the shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run() {
                log.info("Detected a shutdown, let's exit by calling consumer.wakeup...");
                consumer.wakeup();  //genera una excepción WakeUpException que en el While la controlamos para salir

                // join the main thread to allow the execution in the main thread
                try {
                    mainThread.join();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //subscribe consumer to our topics
        consumer.subscribe(Arrays.asList(topic));  //Collections.singletonList(topic)

        try {
            // poll for new data
            while(true){

               // log.info("Pooling...");

                ConsumerRecords<String, String> records =
                        consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, String> record : records){
                    log.info("Key: " + record.key() + ", Value: " + record.value());
                    log.info("Partition: " + record.partition() + ", Offset: " + record.offset());
                }

            }
        } catch (WakeupException e){
            log.info("WhakeupException!!");
            // ignoramos esta excepción pq la contemplábamos como posible
        } catch (Exception e){
            log.error("Unexpected Exception");
        } finally {
            consumer.close(); // this will also commit the offsets if needed be
            log.info("the consumer is now gracefully closed");

        }


    }

}
