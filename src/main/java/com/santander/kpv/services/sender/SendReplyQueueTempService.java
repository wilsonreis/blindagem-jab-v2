package com.santander.kpv.services.sender;

import com.ibm.jakarta.jms.JMSTextMessage;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import com.santander.kpv.exceptions.MyRuntimeException;
import com.santander.kpv.utils.Generated;
import jakarta.jms.DeliveryMode;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@Generated
@Slf4j
public class SendReplyQueueTempService {
    JmsTemplate myTemplate;

    @Value("${ibm.mq.queueRequest}")
    private String queueRequest;

    @Value("${ibm.mq.queueResponse}")
    private String queueResponse;

    public SendReplyQueueTempService(JmsTemplate myTemplate) {
        this.myTemplate = myTemplate;
        log.info("myTemplate.getClass().getName() [{}]", myTemplate.getClass().getName());
        log.info("myTemplate.getClass().getPackage() [{}]", myTemplate.getClass().getPackage());

    }

    @Transactional
    public String sendSyncReply(String msg) {
        Object reply =
                myTemplate.sendAndReceive(
                        queueRequest,
                        new MessageCreator() {
                            @Override
                            public Message createMessage(Session session) throws JMSException {
                                Message jmsmsg = session.createTextMessage(msg);
                                jmsmsg.setJMSCorrelationID(UUID.randomUUID().toString());
                                jmsmsg.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
                                jmsmsg.setJMSReplyTo(session.createQueue(queueResponse));
                                jmsmsg.setIntProperty(
                                        WMQConstants.WMQ_TARGET_CLIENT, WMQConstants.WMQ_CLIENT_NONJMS_MQ);
                                return jmsmsg;
                            }
                        });
        try {
            if (null == reply) {
                log.info("Sem resposta do IBM MQ");
                throw new MyRuntimeException("Transaction failed, rolling back", null);
            } else if (reply instanceof JMSTextMessage jmsTextMessage) {
                String texto = jmsTextMessage.getText();
                log.info("Texto retornado : {} ", texto);
                return texto;
            } else {
                log.info("Reply is not null, but of unexpected type : {} ", reply.getClass());
                throw new MyRuntimeException("Reply is not null, but of unexpected type : ", null);
            }
        } catch (Exception e) {
            throw new MyRuntimeException(e.getMessage(), e);
        }
    }
}
