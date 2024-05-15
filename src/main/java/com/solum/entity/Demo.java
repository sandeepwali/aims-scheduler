//package com.solum.entity;
//
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import javax.activation.DataHandler;
//import javax.activation.DataSource;
//import javax.activation.FileDataSource;
//import javax.annotation.PostConstruct;
//import javax.mail.BodyPart;
//import javax.mail.MessagingException;
//import javax.mail.Multipart;
//import javax.mail.internet.MimeBodyPart;
//import javax.mail.internet.MimeMessage;
//import javax.mail.internet.MimeMultipart;
//
//import org.apache.commons.validator.routines.EmailValidator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Component;
//
//import com.fasterxml.jackson.databind.util.ArrayBuilders.BooleanBuilder;
//
//@Component
//public class Demo {
//	
//	@Autowired
//	private JavaMailSender mailSender;
//	
//	public static void main(String[] args) {
////		String email = "myName@examplecom";
////		boolean valid = EmailValidator.getInstance().isValid(email);
////		System.out.println(valid);
////		String msgStyled = "This is my <b style='color:red;'>bold-red email</b> using JavaMailer";
//		List<String> list = new ArrayList<>();
//		list.add("deepak.samria@diatoz.com");
//		list.add("deepak.samria@diatoz.com");
//		//boolean checkEmails = checkEmails(list);
//		//System.out.println(checkEmails);
//		
//	}
//	
//	@PostConstruct
//	public void checkEmails() {
//		try {
//		MimeMessage msg = mailSender.createMimeMessage();
//		MimeMessageHelper helper = new MimeMessageHelper(msg, StandardCharsets.UTF_8.toString());
//		helper.setSubject("Rma Label Scheduler Job Status");
//		helper.setFrom("redmine@solumesl.com");
//		helper.setText("Hi, Please find the enclosed Job Data Status", true);
//
//		
//		helper.setTo("deepakaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@diatoz.com");
//		helper.setValidateAddresses(true);
//
//		BodyPart messageBodyPartText = new MimeBodyPart();
//		messageBodyPartText.setText("Hi, Please find the enclosed Job Data Status");
//
//		
//		Multipart multipart = new MimeMultipart();
//	
//		multipart.addBodyPart(messageBodyPartText);
//		msg.setContent(multipart);
//		
//		
//		//String msgStyled = "This is my <b style='color:red;'>bold-red email</b> using JavaMailer";
//		Date sentDate = msg.getSentDate();
//		System.out.println("SendDate from Demo"+sentDate);
//		}catch (Exception e) {
//			System.out.println("Error in mail sending");
//		}
//		
//	} 
//			
//		
//}
//
