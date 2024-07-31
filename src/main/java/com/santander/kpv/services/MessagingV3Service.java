package com.santander.kpv.services;

import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory;
import com.santander.kpv.exceptions.MyRuntimeException;
import com.santander.kpv.utils.SuccessMessageCreator;
import jakarta.jms.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Enumeration;
import java.util.UUID;

@Service
@Slf4j
public class MessagingV3Service {

    @Autowired
    @Qualifier("myTemplate")
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("mqQueueConnectionFactory")
    private MQQueueConnectionFactory mqQueueConnectionFactory;

    public String sendAndReceiveMessage(String cpf) throws JMSException {
        QueueConnection connection = null;
        QueueSession session = null;
        long timeToLive = 10 * 60 * 60 * 1000;
        String mensagemRetorno = "não achei mensagem";
        boolean achou = false;
        try {
            connection = mqQueueConnectionFactory.createQueueConnection();
            session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue requestQueue = session.createQueue("DEV.QUEUE.1");
            Queue responseQueue = session.createQueue("DEV.QUEUE.2");
            String correlationID = "blindagem" + UUID.randomUUID().toString();
            log.info(correlationID);
            SuccessMessageCreator messageCreator = new SuccessMessageCreator(cpf,10000,responseQueue, correlationID);
            this.jmsTemplate.setTimeToLive(timeToLive);
            this.jmsTemplate.send(requestQueue.getQueueName(), messageCreator);
            System.out.println("Veja mensagem :" + messageCreator.getMessage());
            Message requestMessage = messageCreator.getMessage();
            if (null != requestMessage) {
                correlationID = requestMessage.getJMSCorrelationID();
            }
            System.out.println("correlationId:" + correlationID);
            //Message requestMessagePos = messageCreator.getMessage();


            // Set up the consumer to receive the response
            String selector = "JMSCorrelationID = '" + correlationID + "'";
            try {
                Thread.sleep(3000);
            } catch (Exception e){
                new MyRuntimeException(e);
            }
            QueueBrowser browser = session.createBrowser(responseQueue);
            Enumeration messages = browser.getEnumeration();
            while (messages.hasMoreElements()) {
                Message message = (Message) messages.nextElement();
                if (message.getJMSCorrelationID().contains("blindagem")) {
                    String comparacao = message.getJMSCorrelationID();
                    if (correlationID.equals(comparacao)) {
                        System.out.println("message.getJMSCorrelationID() achou : " + message.getJMSCorrelationID());
                        achou = true;
                        mensagemRetorno = message.getBody(String.class);
                        browser.close();
                        break;
                    } else {
                        System.out.println("message.getJMSCorrelationID() não achou : " + message.getJMSCorrelationID());
                    }
                }
            }

            browser.close();
            return mensagemRetorno;
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
