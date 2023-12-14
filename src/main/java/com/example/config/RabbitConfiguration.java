package com.example.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/30 10:38
 */

/**
 * RabbitMQ配置类
 */
@Configuration
public class RabbitConfiguration {

	/**
	 * 创建一个名为emailQueue的队列，并设置为@Bean属性
	 *
	 * @return 队列对象
	 */
	@Bean("emailQueue")
	public Queue emailQueue() {
		return QueueBuilder
				.durable("mail")
				.build();
	}

}
