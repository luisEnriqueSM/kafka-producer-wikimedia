package com.producer.wikimedia;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;;

public class WikimediaChangesProducer {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello world!");

        String bootstrapServers = "localhost:9092";
        String topic = "wikimedia.recentchange";
        String url = "https://stream.wikimedia.org/v2/stream/recentchange";

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // set sage producer configs(Kafka <= 2.8)
        //properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        //properties.setProperty(ProducerConfig.ACKS_CONFIG, "all"); // same as setting -1
        //properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        EventHandler eventHandler = new WikimediaChangeHandler(producer, topic);
        EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(url));
        EventSource eventSource = builder.build();

        eventSource.start();

        // we produce for 10 seconds and block the program until then
        TimeUnit.MINUTES.sleep(10);
    }
}