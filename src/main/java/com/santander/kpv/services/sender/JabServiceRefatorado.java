package com.santander.kpv.services.sender;
import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import com.santander.kpv.exceptions.MyRuntimeException;
import com.santander.kpv.utils.ExtendedMessageCreator;
import com.santander.kpv.utils.Generated;
import com.santander.kpv.utils.SuccessMessageCreator;
import jakarta.jms.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@Generated
public class JabServiceRefatorado {

    private MQQueueConnectionFactory mqQueueConnectionFactory;
    private JMSContext jmsContext;
    private Destination queueRequest;
    private Destination queueResponse;
    private JmsTemplate myTemplate;

    @Value("${ibm.mq.queueRequest}")
    private String queueRequestNome;

    @Value("${ibm.mq.queueResponse}")
    private String queueResponseNome;

    @Value("${ibm.mq.jmsExpiration}")
    private long jmsExpiration;

    public void setJmsExpiration(long jmsExpiration) {
        this.jmsExpiration = jmsExpiration;
    }

    public JabServiceRefatorado(MQQueueConnectionFactory mqQueueConnectionFactory, JmsTemplate myTemplate) {
        this.mqQueueConnectionFactory = mqQueueConnectionFactory;
        this.myTemplate =  myTemplate;
    }

    @Transactional
    public String enviaRecebeMensagens(String mensagem) {
        try {
            if (null == jmsContext) {
                this.jmsContext = mqQueueConnectionFactory.createContext();
                log.info("Instanciou jmsContext");
            }
        } catch (Exception e) {
            log.info("enviaRecebeMensagens 1 : [{}]", e.getMessage());
            throw new MyRuntimeException(e.getMessage(), e);
        }
        //nova implementação
        Message message = this.enviaMensagemRefatorada(mensagem);

        String receivedMessage = "Erro ao receber mensagem";
        try {
            receivedMessage = receiveMessage(message);
        } catch (Exception e) {
            log.info("enviaRecebeMensagens 2 - TextMessage: [{}]", e.getMessage());
            log.error("Erro inesperado", e.getMessage());
            throw new MyRuntimeException(e.getMessage(), e);
        }
        return receivedMessage;
    }

    private void configureMessage(Message msg)  {
        try {
            msg.setJMSExpiration(jmsExpiration);
            msg.setJMSCorrelationID(UUID.randomUUID().toString());
            log.info("setJMSCorrelationID(UUID.randomUUID().toString()) {}", msg.getJMSCorrelationID());
            msg.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
            msg.setJMSReplyTo(queueResponse);
            msg.setIntProperty(WMQConstants.WMQ_TARGET_CLIENT, WMQConstants.WMQ_CLIENT_NONJMS_MQ);
        } catch (Exception e) {
            log.info("configureMessage : [{}]", e.getMessage());
            log.error("Erro configureMessage", e.getMessage());
            throw new MyRuntimeException(e.getMessage(), e);
        }

    }

    private void sendMessage(Message message) {
        if (null == queueRequest) {
            this.queueRequest = jmsContext.createQueue(queueRequestNome);
            this.queueResponse = jmsContext.createQueue(queueResponseNome);
            log.info("Instanciou filas");
        }
        JMSProducer producer = jmsContext.createProducer();
        producer.send(queueRequest, message);
        log.info("Mensagem enviada para a fila de requisição");
    }

    private String receiveMessage(Message message)  {
        String receivedMessage = "";
        String messageSelector = "";
        JMSConsumer consumer = jmsContext.createConsumer(queueResponse, messageSelector);
        try {
            messageSelector = "JMSCorrelationID = '" + message.getJMSCorrelationID() + "'";
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

    //nova implementação
    private Message sendTextMessage(final String msg) throws JMSException {

        ExtendedMessageCreator<TextMessage> creator = new ExtendedMessageCreator<TextMessage>() {
            public void setParams(TextMessage message) throws JMSException {
                message.setText(msg);
            }
        };
        return this.enviaMensagemJMSTemplate(creator);
    }

    //nova implementação
    private Message enviaMensagemJMSTemplate(ExtendedMessageCreator<TextMessage> creator) {
        this.queueRequest = jmsContext.createQueue(queueRequestNome);
        this.queueResponse = jmsContext.createQueue(queueResponseNome);
        this.myTemplate.send(this.queueRequest, creator);
        return creator.getMessage();
    }

    private Message enviaMensagemRefatorada(String messageContent) {
        this.queueRequest = jmsContext.createQueue(queueRequestNome);
        this.queueResponse = jmsContext.createQueue(queueResponseNome);
        SuccessMessageCreator messageCreator = new SuccessMessageCreator(messageContent, 10000, (Queue) queueResponse, "uuid");
        configureMessage(messageCreator.getMessage());
        myTemplate.send(queueRequest, messageCreator);
        return messageCreator.getMessage();
    }
}

