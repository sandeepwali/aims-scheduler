package com.solum.service;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.httpcore.HttpClients;
import com.microsoft.graph.models.Attachment;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.FileAttachment;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.Recipient;
import com.microsoft.graph.models.UserSendMailParameterSet;
import com.microsoft.graph.requests.AttachmentCollectionPage;
import com.microsoft.graph.requests.AttachmentCollectionResponse;
import com.microsoft.graph.requests.GraphServiceClient;
import com.solum.config.GraphClientConfig;
import com.solum.entity.EmailList;
import com.solum.entity.EmailMessageWrapper;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;

@Service
@Slf4j
public class MsEmailGraphService {

    @Autowired
    private GraphClientConfig graphClientConfig;



    @Value("${aims.directory.of.reports}")
    private String reportGenerationPath;

    private  ClientSecretCredential clientSecretCredential;

    private  TokenCredentialAuthProvider tokenCredentialAuthProvider ;

    private GraphServiceClient<Request> graphClient ;

    public void processAndSendMail(EmailMessageWrapper payload) throws Exception {

        EmailList emailList = payload.getEmailList();

        String jobId = payload.getJobId();

        Message message = new Message();

        message.subject = "Rma Label Scheduler Job Status of Job" + jobId;

        ItemBody body = new ItemBody();

        body.contentType = BodyType.HTML;
        body.content = "";
        message.body = body;

        assignToAndCC(emailList, message);

        substituteValues(payload, emailList, body);

        linkAttachmentsToMessage(jobId, message);

        sendEmail(message);
    }


    private void linkAttachmentsToMessage(String jobId, Message message) throws IOException {
        LinkedList<Attachment> attachmentsList = new LinkedList<Attachment>();
        FileAttachment attachments = new FileAttachment();
        attachments.name = "Excel.xlsx";
        attachments.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        attachments.oDataType = "#microsoft.graph.fileAttachment";
        File file = new File(reportGenerationPath + "/" + jobId + ".xlsx");
        byte[] encoded = FileUtils.readFileToByteArray(file);
        attachments.contentBytes = encoded;
        attachmentsList.add(attachments);
        AttachmentCollectionResponse attachmentCollectionResponse = new AttachmentCollectionResponse();
        attachmentCollectionResponse.value = attachmentsList;
        AttachmentCollectionPage attachmentCollectionPage = new AttachmentCollectionPage(attachmentCollectionResponse, null);
        message.attachments = attachmentCollectionPage;
    }


    private void substituteValues(EmailMessageWrapper payload, EmailList emailList, ItemBody body) {
        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("jobThreshold", payload.getThreshold()+"");
        valuesMap.put("jobRegion", payload.getRegion());
        String templateString = emailList.getBody();
        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String resolvedString = sub.replace(templateString);

        body.content = body.content.concat(resolvedString);
    }


    private void assignToAndCC(EmailList emailList, Message message) {
        List<String> toList = emailList.getTo();
        List<Recipient> toRecipientList = prepareRecipient(toList);
        message.toRecipients = toRecipientList;
        List<Recipient> ccRecipientList = prepareRecipient(emailList.getCc());
        message.ccRecipients = ccRecipientList;
    }


    private List<Recipient> prepareRecipient(List<String> toList) {
        if(toList == null)return null;
        List<Recipient> data = toList.stream().map(recepient-> {
            Recipient toRecipients = new Recipient();
            EmailAddress emailAddress = new EmailAddress();
            emailAddress.address = recepient;
            toRecipients.emailAddress = emailAddress;
            return toRecipients;
        }).collect(Collectors.toList());
        return data;
    }

    private void sendEmail(Message message) {
        try {
            graphClient
            .users(graphClientConfig.getFromEmailAddress())
            .sendMail(UserSendMailParameterSet
                    .newBuilder()
                    .withMessage(message)
                    .withSaveToSentItems(true)
                    .build()).buildRequest().post();
        } catch (Exception e) {
            log.error("Graph sendEmail ->"+ ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

    @PostConstruct
    private void postConstruct() {
        clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(graphClientConfig.getClientId())
                .clientSecret(graphClientConfig.getClientSecret())
                .tenantId(graphClientConfig.getTenantId())
                .build();

        tokenCredentialAuthProvider = new TokenCredentialAuthProvider(Arrays.asList(graphClientConfig.getScopes().split(",")), clientSecretCredential);
        if(graphClientConfig.getProxyHost().isEmpty()) {
            graphClient = GraphServiceClient
                    .builder()
                    .authenticationProvider(tokenCredentialAuthProvider)
                    .buildClient();
        }else {
            log.info("Using proxy Host{}, Port {}",graphClientConfig.getProxyHost(), graphClientConfig.getProxyPort());
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(graphClientConfig.getProxyHost(), graphClientConfig.getProxyPort()));
            OkHttpClient httpClient = HttpClients.createDefault(tokenCredentialAuthProvider)
                    .newBuilder()
                    .proxy(proxy)
                    .build();
            graphClient = GraphServiceClient
                    .builder()
                    .authenticationProvider(tokenCredentialAuthProvider)
                    .httpClient(httpClient)
                    .buildClient();
        }
    
    }

}
