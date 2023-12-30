package com.example.listener;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/30 10:57
 */
@Component
@RabbitListener(queues = "mail")
// 使用消息队列解决验证码发送问题
public class MailQueueListener {
	@Resource
	JavaMailSender sender;  // 邮件发送器

	@Value("${spring.mail.username}")
	String username;  // 邮箱用户名

	@RabbitHandler
	public void sendMailMessage(JSONObject data) {
		String email = (String) data.get("email");  // 接收邮件地址
		Integer code = (Integer) data.get("code");  // 接收验证码
		String type = (String) data.get("type");  // 接收消息类型
		SimpleMailMessage message = switch (type) {
			case "register" -> createMessage("欢迎您注册网站" , String.valueOf(code) , email);  // 注册验证
			case "reset" -> createMessage("您正在重置密码" , String.valueOf(code) , email);  // 密码重置验证
			default -> null;
		};
		if (message == null)
			return;
		try {
			sender.send(message);  // 发送邮件
		} catch ( MailSendException exception ) {
			System.out.println(exception.getMessage());
		}
	}

	public SimpleMailMessage createMessage(String title , String content , String email) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(username);  // 设置发件人
		message.setTo(email);  // 设置收件人
		message.setSubject(title);  // 设置邮件主题
		Date date = new Date();
		String dateTime = DateFormatUtils.format(date , "yyyy-MM-dd HH:mm:SS");
		message.setText("欢迎您！！！\n" +
				"\n您的邮箱是：" + email + "\n" +
				"\n您的验证码为：\n\n\n" +
				"<h1 style=\"red\">" + content + "</h1>\n" +
				"\n该验证码于  \n\n" + dateTime + "  发送\n" +
				"\n您的验证码使用有效时间为3分钟！为了保障您的安全，请勿向他人泄露你的验证码！");
		return message;  // 返回生成的邮件对象
	}
}
