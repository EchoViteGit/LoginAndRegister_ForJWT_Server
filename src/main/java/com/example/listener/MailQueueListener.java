package com.example.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.PrintStream;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/30 10:57
 */
@Component
@RabbitListener(queues = "mail")
//使用消息队列解决验证码发送问题
public class MailQueueListener {
	@Resource
	JavaMailSender sender;

	@Value("${spring.mail.username}")
	String username;

	@RabbitHandler
	public void sendMailMessage(Map<String, Object> data) {
		String email = (String) data.get("email");
		Integer code = (Integer) data.get("code");
		String type = (String) data.get("type");
		SimpleMailMessage message = switch (type){
			case "register" ->
					createMessage("欢迎您注册网站", String.valueOf(code),email);
			case "reset" ->
					createMessage("您正在重置密码", String.valueOf(code), email);
			default -> null;
		};
		if (message == null)
			return;
		try {
			sender.send(message);
		}catch (MailSendException exception){
			System.out.println(exception.getMessage());
		}
	}

	public SimpleMailMessage createMessage(String title, String content, String email) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(username);
		message.setTo(email);
		message.setSubject(title);
		Date date = new Date();
		String dateTime = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:SS");
		message.setText("欢迎您！！！\n"+
				"\n您的邮箱是："+email +
				"\n\n您的验证码为：\n\n\n"+
				"<h1 style=\"red\">"+content+"</h1>\n" +
				"\n\n该验证码于  \n\n" + dateTime +
				"  发送\n"+
				"\n您的验证码使用有效时间为3分钟!为了保障您的安全，请勿向他人泄露你的验证码！");
		return message;
	}

}
