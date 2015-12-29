package com.vinkos.visitas.io;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import org.apache.log4j.Logger;

public class Notifier {

	final static Logger logger = Logger.getLogger(Notifier.class);

	public static void sendSimpleEmail(String to, String from, String host, 
			String subject, String content) {
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		Session session = Session.getDefaultInstance(properties);

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);
			message.setText(content);
			Transport.send(message);
			logger.info("Succesfully sending email");
		} catch (MessagingException e) {
			logger.error("Error sending simple email", e);
		}
	}
}
