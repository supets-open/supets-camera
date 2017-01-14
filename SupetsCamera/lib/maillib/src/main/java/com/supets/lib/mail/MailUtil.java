package com.supets.lib.mail;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtil {

    static int port = 25;
    static String server = "smtp.163.com";
    static String from = "疯狂桔子安卓团队";
    static String user = "lihongjiang421630@163.com";
    static String password = "lhj628315abc";
    public static String to = "lihongjiang@supets.com";

    public static void setPort(int port) {
        MailUtil.port = port;
    }

    public static void setServer(String server) {
        MailUtil.server = server;
    }

    public static void setFrom(String from) {
        MailUtil.from = from;
    }

    public static void setUser(String user) {
        MailUtil.user = user;
    }

    public static void setPassword(String password) {
        MailUtil.password = password;
    }

    public static void setTo(String to) {
        MailUtil.to = to;
    }


    public static void sendEmail(final String email, final String subject, final String body) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Properties props = new Properties();
                    props.put("mail.smtp.host", server);
                    props.put("mail.smtp.port", String.valueOf(port));
                    props.put("mail.smtp.auth", "true");
                    Transport transport = null;
                    Session session = Session.getDefaultInstance(props, null);
                    transport = session.getTransport("smtp");
                    transport.connect(server, user, password);
                    MimeMessage msg = new MimeMessage(session);
                    msg.setSentDate(new Date());
                    InternetAddress fromAddress = new InternetAddress(user, from,
                            "UTF-8");
                    msg.setFrom(fromAddress);
                    InternetAddress[] toAddress = new InternetAddress[1];
                    toAddress[0] = new InternetAddress(email);
                    msg.setRecipients(Message.RecipientType.TO, toAddress);
                    msg.setSubject(subject, "UTF-8");
                    msg.setText(body, "UTF-8");
                    msg.saveChanges();
                    transport.sendMessage(msg, msg.getAllRecipients());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}