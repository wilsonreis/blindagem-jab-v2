package com.santander.kpv.brincando;

import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;

import jakarta.jms.*;

import java.util.UUID;

public class JmsReplyToExampleIBM {

    public static void main(String[] args) throws Exception {
        // Criação e configuração da fábrica de conexões IBM MQ
        MQQueueConnectionFactory connectionFactory = new MQQueueConnectionFactory();
        connectionFactory.setHostName("localhost");
        connectionFactory.setPort(1414);
        connectionFactory.setQueueManager("QM1");
        connectionFactory.setChannel("DEV.ADMIN.SVRCONN");
        connectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);

        // Criação da conexão e sessão
        try (QueueConnection connection = (QueueConnection) connectionFactory.createConnection("admin", "passw0rd")) {
            QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue requestQueue = session.createQueue("queue:///DEV.QUEUE.1");
            Queue replyQueue = session.createQueue("queue:///DEV.QUEUE.2");

            // Criação do produtor de mensagens
            MessageProducer producer = session.createProducer(requestQueue);

            // Geração de um Correlation ID único
            String correlationId = UUID.randomUUID().toString();

            // Criação e envio da mensagem
            TextMessage requestMessage = session.createTextMessage("Hello, JMS!");
            requestMessage.setJMSReplyTo(replyQueue);
            requestMessage.setJMSCorrelationID(correlationId);
            producer.send(requestMessage);
            System.out.println("Message sent to DEV.QUEUE.1 with ReplyTo set to DEV.QUEUE.2 and CorrelationID: " + correlationId);

            // Simulação do processamento da mensagem e envio da resposta (Normalmente feito por um consumidor diferente)
            // Aqui estamos simulando isso na mesma aplicação para simplicidade
            MessageConsumer requestConsumer = session.createConsumer(requestQueue);
            Message receivedRequestMessage = requestConsumer.receive(5000); // Timeout de 5 segundos

            if (receivedRequestMessage == null) {
                System.out.println("No message received from DEV.QUEUE.1 within the given timeout.");
                return;
            }

            Destination replyToDestination = receivedRequestMessage.getJMSReplyTo();

            // Criando a resposta
            TextMessage replyMessage = session.createTextMessage("Reply to: " + ((TextMessage) receivedRequestMessage).getText());
            replyMessage.setJMSCorrelationID(receivedRequestMessage.getJMSCorrelationID());
            MessageProducer replyProducer = session.createProducer(replyToDestination);
            replyProducer.send(replyMessage);
            System.out.println("Reply sent to DEV.QUEUE.2");

            // Criação do consumidor de mensagens para a fila de respostas com um selector para o Correlation ID
            String messageSelector = "JMSCorrelationID = '" + correlationId + "'";
            MessageConsumer consumer = session.createConsumer(replyQueue, messageSelector);

            // Recebendo a resposta
            TextMessage receivedReplyMessage = (TextMessage) consumer.receive(5000); // Timeout de 5 segundos
            if (receivedReplyMessage == null) {
                System.out.println("No reply received from DEV.QUEUE.2 within the given timeout.");
            } else {
                System.out.println("Received reply: " + receivedReplyMessage.getText());
            }
        }
    }
}
