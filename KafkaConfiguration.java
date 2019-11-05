package com.example.demo.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.example.demo.model.User;

@EnableKafka
@Configuration
public class KafkaConfiguration {

	/*
	 * @Bean //@ConditionalOnMissingBean(value=org.springframework.kafka.core.
	 * ConsumerFactory.class) public ConsumerFactory<String, String>
	 * consumerFactory(){ Map<String,Object> config = new HashMap<>();
	 * config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
	 * config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id");
	 * config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
	 * StringDeserializer.class);
	 * config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
	 * StringDeserializer.class);
	 * 
	 * return new DefaultKafkaConsumerFactory<>(config); }
	 */
	@Bean
	public ConsumerFactory<String, String> consumerFactory() {
		Map<String, Object> config = new HashMap<>();

		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id");
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

		return new DefaultKafkaConsumerFactory<>(config);
	}
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}
	/*
	 * @Bean public ConcurrentKafkaListenerContainerFactory<String, String>
	 * kafkaListnerContainerFactory(){
	 * ConcurrentKafkaListenerContainerFactory<String, String> factory = new
	 * ConcurrentKafkaListenerContainerFactory<>();
	 * factory.setConsumerFactory(consumerFactory()); return factory; }
	 */
	@Bean
	public ConsumerFactory<String, User> userConsumerFactory(){
		Map<String, Object> config = new HashMap<>();

		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_json");
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

		return new DefaultKafkaConsumerFactory<String,User>(config,new StringDeserializer(),
				new JsonDeserializer<>(User.class) );
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, User> userKafkaListenerFactory(){
		ConcurrentKafkaListenerContainerFactory<String, User> factory = new ConcurrentKafkaListenerContainerFactory<>();

		factory.setErrorHandler(new ErrorHandler() {
			@Override
			public void handle(Exception thrownException, List<ConsumerRecord<?, ?>> records, Consumer<?, ?> consumer, MessageListenerContainer container) {
				String s = thrownException.getMessage().split("Error deserializing key/value for partition ")[1].split(". If needed, please seek past the record to continue consumption.")[0];
				String topics = s.split("-")[0];
				int offset = Integer.valueOf(s.split("offset ")[1]);
				int partition = Integer.valueOf(s.split("-")[1].split(" at")[0]);

				TopicPartition topicPartition = new TopicPartition(topics, partition);
				//log.info("Skipping " + topic + "-" + partition + " offset " + offset);
				consumer.seek(topicPartition, offset + 1);
				System.out.println("OK");
			}

			@Override
			public void handle(Exception e, ConsumerRecord<?, ?> consumerRecord) {

			}

			@Override
			public void handle(Exception e, ConsumerRecord<?, ?> consumerRecord, Consumer<?,?> consumer) {
				String s = e.getMessage().split("Error deserializing key/value for partition ")[1].split(". If needed, please seek past the record to continue consumption.")[0];
				String topics = s.split("-")[0];
				int offset = Integer.valueOf(s.split("offset ")[1]);
				int partition = Integer.valueOf(s.split("-")[1].split(" at")[0]);

				TopicPartition topicPartition = new TopicPartition(topics, partition);
				//log.info("Skipping " + topic + "-" + partition + " offset " + offset);
				consumer.seek(topicPartition, offset + 1);
				System.out.println("OKKKKK");


			}
		});

		factory.setConsumerFactory(userConsumerFactory());
		return factory;
	}
}
