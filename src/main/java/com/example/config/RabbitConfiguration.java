package com.example.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/30 10:38
 */
@Configuration
public class RabbitConfiguration {
	@Bean("emailQueue")
	public Queue emailQueue(){
		return QueueBuilder
				.durable("mail")
				.build();
	}
}
