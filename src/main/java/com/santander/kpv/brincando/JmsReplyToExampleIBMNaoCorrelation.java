package com.santander.kpv.brincando;

import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;

import jakarta.jms.Destination;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.QueueConnection;
import jakarta.jms.QueueSession;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

public class JmsReplyToExampleIBMNaoCorrelation {

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
            Queue requestQueue = session.createQueue("DEV.QUEUE.1");
            Queue replyQueue = session.createQueue("DEV.QUEUE.2");

            // Criação do produtor de mensagens
            MessageProducer producer = session.createProducer(requestQueue);

            // Criação do consumidor de mensagens para a fila de respostas
            MessageConsumer consumer = session.createConsumer(replyQueue);


            // Iniciando a conexão
            connection.start();

            // Criação e envio da mensagem
            TextMessage requestMessage = session.createTextMessage("Hello, JMS!");
            requestMessage.setJMSReplyTo(replyQueue);
            producer.send(requestMessage);
            System.out.println("Message sent to DEV.QUEUE.1 with ReplyTo set to DEV.QUEUE.2 : " + requestMessage);

            // Simulação do processamento da mensagem e envio da resposta (Normalmente feito por um consumidor diferente)
            // Aqui estamos simulando isso na mesma aplicação para simplicidade
            MessageConsumer requestConsumer = session.createConsumer(requestQueue);
            Message receivedRequestMessage = requestConsumer.receive();

            Destination replyToDestination = receivedRequestMessage.getJMSReplyTo();

            // Criando a resposta
            TextMessage replyMessage = session.createTextMessage("Reply to: " + ((TextMessage) receivedRequestMessage).getText());
            MessageProducer replyProducer = session.createProducer(replyToDestination);
            replyProducer.send(replyMessage);
            System.out.println("Reply sent to DEV.QUEUE.2");

            // Recebendo a resposta
            TextMessage receivedReplyMessage = (TextMessage) consumer.receive();
            System.out.println(receivedReplyMessage);
            System.out.println("Received reply: " + receivedReplyMessage.getText());
        }
    }
}
