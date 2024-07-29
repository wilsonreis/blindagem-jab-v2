package com.santander.kpv.utils;

import com.ibm.msg.client.wmq.WMQConstants;
import jakarta.jms.*;

public class SuccessMessageV2Creator extends ExtendedMessageCreator<TextMessage> {

    private String messageContent;
    private long jmsExpiration;
    private Queue queueResponse;

    private String uuid;

    public SuccessMessageV2Creator(String messageContent, long jmsExpiration, Queue queueResponse, String uuid) {
        this.messageContent = messageContent;
        this.jmsExpiration = jmsExpiration;
        this.queueResponse = queueResponse;
        this.uuid = uuid;
    }

    @Override
    public void setParams(Message message) throws JMSException {
        TextMessage textMessage = (TextMessage) message;
        textMessage.setText(messageContent);
        message.setJMSCorrelationID(uuid);
        message.setJMSReplyTo(queueResponse);
        message.setIntProperty(WMQConstants.WMQ_TARGET_CLIENT, WMQConstants.WMQ_CLIENT_NONJMS_MQ);
        System.out.println("message.setJMSCorrelationID(uuid); :" + message.getJMSCorrelationID());
        this.setMessage(message);
    }
}