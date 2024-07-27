package com.santander.kpv.services;

import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory;
import com.santander.kpv.utils.SuccessMessageCreator;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Queue;
import jakarta.jms.QueueConnection;
import jakarta.jms.QueueSession;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class MessagingService {

    @Autowired
    @Qualifier("myTemplate")
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("mqQueueConnectionFactory")
    private MQQueueConnectionFactory mqQueueConnectionFactory;

    public String sendAndReceiveMessage(String cpf) throws JMSException {
        QueueConnection connection = null;
        QueueSession session = null;
        try {
            connection = mqQueueConnectionFactory.createQueueConnection();
            session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue requestQueue = session.createQueue("DEV.QUEUE.1");
            Queue responseQueue = session.createQueue("DEV.QUEUE.2");
            String correlationID = UUID.randomUUID().toString();
            log.info(correlationID);
            SuccessMessageCreator messageCreator = new SuccessMessageCreator(cpf,10000,responseQueue, correlationID);
            this.jmsTemplate.send(requestQueue.getQueueName(), messageCreator);
            Message requestMessage = messageCreator.getMessage();
            if (null != requestMessage) {
                correlationID = requestMessage.getJMSCorrelationID();
            }
            System.out.println("correlationId:" + correlationID);
            //Message requestMessagePos = messageCreator.getMessage();

            // Set up the consumer to receive the response
            String selector = "JMSCorrelationID = '" + correlationID + "'";
            //MessageConsumer consumer = session.createConsumer(responseQueue, selector);
            MessageConsumer consumer = session.createConsumer(responseQueue,selector );

            // Start the connection to receive the message
            connection.start();
            Message responseMessage = consumer.receive(jmsTemplate.getReceiveTimeout());
            System.out.println("responseMessage.getJMSCorrelationID():" + responseMessage.getJMSCorrelationID());

            if (responseMessage != null) {
                return ((TextMessage) responseMessage).getText();
            } else {
                throw new JMSException("No response received within the timeout period");
            }
        } catch (JMSException e) {
            log.error("Error in sendAndReceiveMessage: {}", e.getMessage());
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}
