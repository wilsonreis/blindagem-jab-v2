package com.santander.kpv.services.sender;

import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import com.santander.kpv.exceptions.MyRuntimeException;
import com.santander.kpv.utils.Generated;
import jakarta.jms.*;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Generated
public class JabService {

    private MQQueueConnectionFactory mqQueueConnectionFactory;
    private JMSContext jmsContext;
    private Destination queueRequest;
    private Destination queueResponse;

    @Value("${ibm.mq.queueRequest}")
    private String queueRequestNome;

    @Value("${ibm.mq.queueResponse}")
    private String queueResponseNome;

    @Value("${ibm.mq.jmsExpiration}")
    private long jmsExpiration;

    public void setJmsExpiration(long jmsExpiration) {
        this.jmsExpiration = jmsExpiration;
    }

    public JabService(MQQueueConnectionFactory mqQueueConnectionFactory) {
        this.mqQueueConnectionFactory = mqQueueConnectionFactory;
    }

    @Transactional
    public String enviaRecebeMensagens(String mensagem) {
        try {
            if (null == jmsContext) {
                this.jmsContext = mqQueueConnectionFactory.createContext();
                log.info("Instanciou jmsContext");
            }
            if (null == queueRequest) {
                this.queueRequest = jmsContext.createQueue(queueRequestNome);
                this.queueResponse = jmsContext.createQueue(queueResponseNome);
                log.info("Instanciou filas");
            }

        } catch (Exception e) {
            log.info("enviaRecebeMensagens 1 : [{}]", e.getMessage());
            throw new MyRuntimeException(e.getMessage(), e);
        }
        TextMessage message = jmsContext.createTextMessage(mensagem);
        String receivedMessage = "Erro ao receber mensagem";
        try {
            String messageSelector =  configureMessage(message);
            sendMessage(message);
            receivedMessage = receiveMessage(message, messageSelector);
        } catch (Exception e) {
            log.info("enviaRecebeMensagens 2 - TextMessage: [{}]", e.getMessage());
            log.error("Erro inesperado", e.getMessage());
            throw new MyRuntimeException(e.getMessage(), e);
        }
        return receivedMessage;
    }

    private String configureMessage(TextMessage message)  {
        try {
            String uuid = UUID.randomUUID().toString();
            Message msg = (message);
            msg.setJMSExpiration(jmsExpiration);
            msg.setJMSCorrelationID(uuid);
            log.info("setJMSCorrelationID(UUID.randomUUID().toString()) {}", uuid);
            msg.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
            msg.setJMSReplyTo(queueResponse);
            msg.setIntProperty(WMQConstants.WMQ_TARGET_CLIENT, WMQConstants.WMQ_CLIENT_NONJMS_MQ);
            return "JMSCorrelationID = '" + uuid + "'";
        } catch (Exception e) {
            log.info("configureMessage : [{}]", e.getMessage());
            log.error("Erro configureMessage", e.getMessage());
            throw new MyRuntimeException(e.getMessage(), e);
        }

    }

    private void sendMessage(TextMessage message) {
        jakarta.jms.JMSProducer producer = jmsContext.createProducer();
        producer.send(queueRequest, message);
        log.info("Mensagem enviada para a fila de requisição");
    }

    private String receiveMessage(TextMessage message, String messageSelector)  {
        String receivedMessage = "";
        log.info("messageSelector [{}]", messageSelector);
        JMSConsumer consumer = jmsContext.createConsumer(queueResponse, messageSelector);
        try {
            receivedMessage = consumer.receiveBody(String.class, 15000);
            if (receivedMessage == null) {
                log.info("Tempo de espera expirou sem receber mensagem");
                throw new MyRuntimeException("Tempo de espera expirado");
            } else {
                log.info("Mensagem recebida: {}", receivedMessage);
            }
        } catch (Exception e ) {
            log.info("receiveMessage : [{}]", e.getMessage());
            log.error("Erro receiveMessage", e.getMessage());
            throw new MyRuntimeException(e.getMessage(), e);
        } finally {
            consumer.close();
        }
        return receivedMessage;
    }
}
