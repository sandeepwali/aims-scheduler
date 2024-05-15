package com.solum.job;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import com.rabbitmq.client.Channel;
import com.solum.entity.FeignResponse;
import com.solum.entity.label.RmaLabels;
import com.solum.service.AimsCoreDbService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@SuppressWarnings("unused")
public class JobSupportDeleteLabel {

//	@Autowired
//	private StreamBridge streamBridge;

	@Value("${aims.db.flag}")
	private boolean dbFlag;

	@Autowired
	private AimsCoreDbService aimsCoreDbService;

	@Bean
	public Consumer<Message<RmaLabels>> deleteLabel() {
		return (consumedMessage) -> {
			log.info("Messgae consumed for delete label process :{}", consumedMessage);
			MessageHeaders headers = consumedMessage.getHeaders();
			Channel channel = (Channel) headers.get("amqp_channel");
			Long deliveryTag = (Long) headers.get("amqp_deliveryTag");

			RmaLabels rmaLabel = consumedMessage.getPayload();
			boolean deleteStatus = false;
			if (dbFlag) {
				int counter = 0;
				while (!deleteStatus && counter < 3) {
					log.info("Deleteing label process is started for the label having id :{}", rmaLabel.getCode());
					FeignResponse deleteLabel = aimsCoreDbService.deleteLable(UUID.randomUUID(), rmaLabel.getCode());
					if (deleteLabel.getStatusCode() != 200) {
						log.warn("Error while deleting the Label :{}, will try after sometime", rmaLabel.getCode());
						counter++;
						if (counter == 2) {
							log.error("Error in deleting the label from DB having id :{}", rmaLabel.getCode());
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							log.error("Delete label thread interrupted while sleeping having LabelCode :{}",
									rmaLabel.getCode());
						}
					} else {
						deleteStatus = true;
					}
				}
			}
			if (!deleteStatus && dbFlag) {
				channel.getConnection();
				try {
					channel.basicNack(deliveryTag, false, false);
				} catch (IOException e1) {
					log.error("Error while rejecting a delete label message");
				}
			}
			if(deleteStatus && dbFlag) {
			channel.getConnection();
			try {
				channel.basicAck(deliveryTag, false);
				log.info("Email message is acknowldged sucessfully :{}", consumedMessage);
			} catch (IOException e1) {
				log.error("Error while rejecting a Email message");
			}
			}
			if(!dbFlag) {
				channel.getConnection();
				try {
					channel.basicAck(deliveryTag, false);
					log.info("Email message is acknowldged sucessfully :{}", consumedMessage);
				} catch (IOException e1) {
					log.error("Error while rejecting a Email message");
				}
				}
			
		};
	}

//	public boolean sendErrorDeleteLabel(RmaLabels message) {
//		boolean send = streamBridge.send("errordeletelabel-out-0", message);
//		return send;
//	}

}
