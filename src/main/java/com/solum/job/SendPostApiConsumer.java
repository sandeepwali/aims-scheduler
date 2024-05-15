package com.solum.job;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.solum.entity.PostApiWrapper;
import com.solum.entity.label.RmaLabels;
import com.solum.entity.scheduler.ApiHistory;
import com.solum.entity.scheduler.SchedulerJobInfo;
import com.solum.repository.scheduler.ApiHistoryRepository;
import com.solum.service.JobExecutionService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class SendPostApiConsumer {
	
	@Autowired
	private JobExecutionService jobExecutionService;
	
	@Autowired
	private ApiHistoryRepository apiHistoryRepository;

	@Bean
	public Consumer<Message<PostApiWrapper>> postapi() {
		return (consumedMessage) -> {
			log.info("Messgae consumed for PostApi process :{}", consumedMessage);
			MessageHeaders headers = consumedMessage.getHeaders();
			Channel channel = (Channel) headers.get("amqp_channel");
			Long deliveryTag = (Long) headers.get("amqp_deliveryTag");
			PostApiWrapper payload = consumedMessage.getPayload();
			SchedulerJobInfo schedulerJobInfo = payload.getSchedulerJobInfo();
			ApiHistory apiHistory = new ApiHistory();
			apiHistory.setJobId(payload.getApiJobId());
			apiHistory.setJobHistoryId(payload.getJobHistoryId());
			apiHistory.setParentJobId(payload.getParentJobId());
			apiHistory.setJobName(schedulerJobInfo.getJobName());
			apiHistory.setJobGroup(schedulerJobInfo.getJobGroup());
			apiHistory.setJobClass("com.solum.job.SendPostApiConsumer");
			apiHistory.setAmqpDeliveryTag(deliveryTag);
			String api = "";
			ObjectMapper mapper = new ObjectMapper();
			try {
				api = mapper.writeValueAsString(payload.getApiList());
			} catch (JsonProcessingException e2) {
				e2.printStackTrace();
			}
			
			apiHistory.setApi(api);

			try {
				String url = payload.getApiList().getUrl();
				URI uri = URI.create(url);
				List<RmaLabels> rmaLabels = payload.getRmaLabels();
				Map<String, String> headersMap = payload.getApiList().getHeaders();
				
				
				jobExecutionService.postUpdate(UUID.randomUUID(), rmaLabels, uri,headersMap);
			} catch (Exception e) {
				log.error("Error while posting update to API pof Job Id :{} with root cause :{} ", payload.getJobId(), e.getMessage());
				apiHistory.setJobStatus("FAILED");
				apiHistory.setMessage("Unable to reach API Server.");
				apiHistoryRepository.save(apiHistory);
				channel.getConnection();
				try {
					channel.basicNack(deliveryTag, false, false);
					log.info("Message is rejected sucessfully for jobId :{}", payload.getJobId());
				} catch (IOException e1) {
					log.error("Error while rejecting message for job id :{}", payload.getJobId());
				}
				return;
			}
			apiHistory.setJobStatus("COMPLETED");
			apiHistory.setMessage("Api sent successfully");
			apiHistoryRepository.save(apiHistory);
			
			try {
				channel.basicAck(deliveryTag, false);
				log.info("PostApi message is acknowldged sucessfully :{}", consumedMessage);
			} catch (IOException e1) {
				log.error("Error while accpeting a PostApi message");
			}
						
		};
	}

	
}
