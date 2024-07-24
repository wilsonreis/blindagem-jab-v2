package com.santander.kpv.utils;

import com.ibm.msg.client.wmq.WMQConstants;
import jakarta.jms.*;

import java.util.UUID;

public class SuccessMessageCreator extends ExtendedMessageCreator<TextMessage> {

    private String messageContent;
    private long jmsExpiration;
    private Queue queueResponse;

    public SuccessMessageCreator(String messageContent, long jmsExpiration, Queue queueResponse) {
        this.messageContent = messageContent;
        this.jmsExpiration = jmsExpiration;
        this.queueResponse = queueResponse;
    }

    @Override
    public void setParams(Message message) throws JMSException {
        TextMessage textMessage = (TextMessage) message;
        textMessage.setText(messageContent);
        message.setJMSExpiration(jmsExpiration);
        message.setJMSCorrelationID(UUID.randomUUID().toString());
        message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
        message.setJMSReplyTo(queueResponse);
        message.setIntProperty(WMQConstants.WMQ_TARGET_CLIENT, WMQConstants.WMQ_CLIENT_NONJMS_MQ);
    }
}