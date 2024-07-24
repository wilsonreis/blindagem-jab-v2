package com.santander.kpv.services.sender;

import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;

import com.santander.kpv.exceptions.MyRuntimeException;

import com.santander.kpv.utils.Generated;
import jakarta.jms.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@Generated
@Slf4j
public class SendReplyService {
    @Autowired
    private JmsTemplate myTemplate;
    @Autowired
    private MQQueueConnectionFactory mqQueueConnectionFactory;
    private JMSContext myJmsContext;

    @Value("${ibm.mq.queueRequest}")
    private String queueRequestNome;

    @Value("${ibm.mq.queueResponse}")
    private String queueResponseNome;

    @Value("${ibm.mq.jmsExpiration}")
    private long jmsExpiration;

    public SendReplyService(JmsTemplate myTemplate, MQQueueConnectionFactory mqQueueConnectionFactory) {
        this.myTemplate = myTemplate;
        this.mqQueueConnectionFactory = mqQueueConnectionFactory;
        log.info("myTemplate.getClass().getName() [{}]", myTemplate.getClass().getName());
        log.info("myTemplate.getClass().getPackage() [{}]", myTemplate.getClass().getPackage());

    }

    @Transactional
    public String sendSyncReply(String messageContent) {
        String retornoErro = "Erro de envio/retorno";
        try {
            myJmsContext = mqQueueConnectionFactory.createContext();
            String correlation = UUID.randomUUID().toString();
            String messageSelector = "JMSCorrelationID = '" + correlation + "'";
            Queue requestQueue = myJmsContext.createQueue(queueRequestNome);
            Queue responseQueue = myJmsContext.createQueue(queueResponseNome);
            myTemplate.send(requestQueue, session -> {
                TextMessage message = session.createTextMessage(messageContent);
                message.setJMSCorrelationID(correlation);
                log.info(message.getJMSCorrelationID());
                message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
                message.setJMSReplyTo(responseQueue);
                message.setJMSTimestamp(jmsExpiration);
                message.setIntProperty(
                        WMQConstants.WMQ_TARGET_CLIENT, WMQConstants.WMQ_CLIENT_NONJMS_MQ);
                return message;
            });

            Message responseMessage = myTemplate.receiveSelected(responseQueue, messageSelector);

            if (responseMessage instanceof TextMessage textMessage) {
                return textMessage.getText();
            } else {
                return retornoErro;
            }
        } catch (Exception e) {
            log.info("sendSyncReply : [{}]", e.getMessage());
            throw new MyRuntimeException(e.getMessage(), e);
        } finally {
            if (null != myJmsContext) {
                myJmsContext.close();
            }
        }
    }
}
