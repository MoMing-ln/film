package com.ln.film.utils;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;

public class EmailUtils {

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    @Value("#{environment['smtp.host']}")
    private String SMTP_HOST = "smtp.example.com";

    @Value("#{environment['smtp.user']}")
    private String SMTP_USER = "your_mail@example.com";

    @Value("#{environment['smtp.password']}")
    private String SMTP_PSWD = "your_mail_password_or_app_password";

    public boolean isValidEmailAddr(String emailAddr) {
        Matcher matcher = pattern.matcher(emailAddr);
        return matcher.matches();
    }

    public boolean sendEmail(String title, String content, String mailTo) {

        // Sender's email ID needs to be mentioned
        String from = SMTP_USER;

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", SMTP_HOST);
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.user", SMTP_USER);
        properties.setProperty("mail.password", SMTP_PSWD);

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SMTP_USER,
                                SMTP_PSWD);
                    }
                });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                    mailTo));

            // Set Subject: header field
            message.setSubject(title);

            // Now set the actual message
            message.setText(content);

            // Set HTML content
            message.setContent(content, "text/html; charset=utf-8");

            // Set sent date
            message.setSentDate(new java.util.Date());
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
            return true;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        }
    }
}
