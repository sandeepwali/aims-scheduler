package com.solum.job;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import com.rabbitmq.client.Channel;
import com.solum.entity.EmailList;
import com.solum.entity.EmailMessageWrapper;
import com.solum.entity.label.RmaLabels;
import com.solum.entity.scheduler.EmailHistory;
import com.solum.entity.scheduler.SchedulerJobInfo;
import com.solum.repository.scheduler.EmailHistoryRepository;
import com.solum.service.MsEmailGraphService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@SuppressWarnings("unused")
public class SendEmailConsumer {

	@Autowired
	private MsEmailGraphService graphService;

	@Value("${aims.directory.of.reports}")
	private String reportGenerationPath;
	
	@Autowired
	private EmailHistoryRepository emailHistoryRepository;

	@Bean
	public Consumer<Message<EmailMessageWrapper>> email() {
		return (consumedMessage) -> {
			log.info("Messgae consumed for email process :{}", consumedMessage);
			MessageHeaders headers = consumedMessage.getHeaders();
			Channel channel = (Channel) headers.get("amqp_channel");
			Long deliveryTag = (Long) headers.get("amqp_deliveryTag");
					
			EmailMessageWrapper payload = consumedMessage.getPayload();
			SchedulerJobInfo schedulerJobInfo = payload.getSchedulerJobInfo();
			EmailHistory emailHistory = new EmailHistory();
			emailHistory.setJobId(payload.getEmailJobId());
			emailHistory.setJobHistoryId(payload.getJobHistoryId());
			emailHistory.setParentJobId(payload.getParentJobId());
			emailHistory.setJobName(schedulerJobInfo.getJobName());
			emailHistory.setJobGroup(schedulerJobInfo.getJobGroup());
			emailHistory.setJobClass("com.solum.job.SendEmailConsumer");
			emailHistory.setEmail(payload.getEmailList().toString());
			emailHistory.setAmqpDeliveryTag(deliveryTag);
			
			try {
				graphService.processAndSendMail(payload);
			} catch (Exception e) {
				log.error("Error while sending Email message of Job Id :{} with root cause :{} ", payload.getJobId(), e.getMessage());
				emailHistory.setJobStatus("FAILED");
				emailHistory.setMessage("Unable to reach smtp server.");
				emailHistoryRepository.save(emailHistory);
				channel.getConnection();
				try {
					channel.basicNack(deliveryTag, false, false);
					log.info("Message is rejected sucessfully for jobId :{}", payload.getJobId());
				} catch (IOException e1) {
					log.error("Error while rejecting message for job id :{}", payload.getJobId());
				}
				return;
			}
			
			emailHistory.setJobStatus("COMPLETED");
			emailHistory.setMessage("Email sent succesfully");
			emailHistoryRepository.save(emailHistory);
			channel.getConnection();
			try {
				channel.basicAck(deliveryTag, false);
				log.info("Email message is acknowldged sucessfully :{}", consumedMessage);
			} catch (IOException e1) {
				log.error("Error while accepting a Email message of JobId :{}", payload.getJobId());
			}
		};
	}


//	public boolean sendErrorEmail(EmailMessageWrapper message) {
//		boolean send = streamBridge.send("erroremail-out-0", message);
//
//		return send;
//	}

}
