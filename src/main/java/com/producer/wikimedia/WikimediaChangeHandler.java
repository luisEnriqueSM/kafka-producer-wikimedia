package com.producer.wikimedia;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;

public class WikimediaChangeHandler implements EventHandler{

    private KafkaProducer<String, String> kafkaProducer;
    private String topic;
    private final Logger log = LoggerFactory.getLogger(WikimediaChangeHandler.class.getName());

    public WikimediaChangeHandler(KafkaProducer<String, String> kafkaProducer, String topic){
        this.kafkaProducer = kafkaProducer;
        this.topic = topic;
    }

    @Override
    public void onClosed(){
        kafkaProducer.close();
    }

    @Override
    public void onComment(String comment) {
        // nothing here
    }

    @Override
    public void onError(Throwable t) {
       log.error("Error in Strem reading", t);
    }

    @Override
    public void onMessage(String event, MessageEvent messageEvent) throws Exception {
        log.info(messageEvent.getData());
        // asynchronus clode
        kafkaProducer.send(new ProducerRecord<String,String>(topic, messageEvent.getData()));
    }

    @Override
    public void onOpen() throws Exception {
        // nothing here
    }

}
