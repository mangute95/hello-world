package com.example.demo.liistner;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;

@Service
public class KafkaConsumer {

	@KafkaListener(topics = "java-kafaka-demo-topic", groupId = "group_id" )
	public void consume(String message) {
		System.out.println("Consumed Message : = " + message);
	}
	
	@KafkaListener(topics = "Kafka_Example_json", groupId = "group_json",
			containerFactory = "userKafkaListenerFactory")
	public void consumeJson(User user) {
		try {
			System.out.println("Consumed JSON Message: " + user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
