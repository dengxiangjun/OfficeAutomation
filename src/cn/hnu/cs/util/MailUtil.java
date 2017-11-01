package cn.hnu.cs.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class MailUtil {
	public static ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

	public static void send(String to,String subject,String text) {
		// 获取JavaMailSender bean
		JavaMailSenderImpl sender = (JavaMailSenderImpl) ctx.getBean("mailSender");
		SimpleMailMessage mail = new SimpleMailMessage(); // <span															// style="color: #ff0000;">注意SimpleMailMessage只能用来发送text格式的邮件</span>
		try {
			mail.setFrom(sender.getUsername());
			mail.setTo(to);// 接受者
			mail.setSubject(subject);// 主题
			mail.setText(text);// 邮件内容
			sender.send(mail);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}