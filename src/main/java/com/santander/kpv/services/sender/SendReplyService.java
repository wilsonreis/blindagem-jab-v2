package com.santander.kpv.services.sender;


import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import com.santander.kpv.utils.Generated;
import jakarta.jms.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@Generated
@Slf4j
public class SendReplyService {
    @Autowired
    JmsTemplate myTemplate;
    private JMSContext jmsContext;
    private Destination queueRequest;
    private Destination queueResponse;


    public SendReplyService(JmsTemplate myTemplate) {
        this.myTemplate = myTemplate;
        log.info("myTemplate.getClass().getName() [{}]", myTemplate.getClass().getName());
        log.info("myTemplate.getClass().getPackage() [{}]", myTemplate.getClass().getPackage());

    }

    @Transactional
    public String sendSyncReply(String messageContent) {
        try {
            String correlation = UUID.randomUUID().toString();
            String messageSelector = "JMSCorrelationID = '" + correlation + "'";
            Queue requestQueue = myTemplate.getConnectionFactory().createContext().createQueue("DEV.QUEUE.1");
            Queue responseQueue = myTemplate.getConnectionFactory().createContext().createQueue("DEV.QUEUE.2");
            myTemplate.send(requestQueue, session -> {
                TextMessage message = session.createTextMessage(messageContent);
                message.setJMSCorrelationID(correlation);
                log.info(message.getJMSCorrelationID());
                message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
                message.setJMSReplyTo(responseQueue);
                message.setJMSTimestamp(10000);
                message.setIntProperty(
                        WMQConstants.WMQ_TARGET_CLIENT, WMQConstants.WMQ_CLIENT_NONJMS_MQ);
                return message;
            });

            Message responseMessage = myTemplate.receiveSelected(responseQueue, messageSelector);

            if (responseMessage instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) responseMessage;
                return textMessage.getText();
            } else {
                return "Ferrou";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Ferrou";
    }
}
