package com.santander.kpv.brincando;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;

import jakarta.jms.Connection;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;

public class JmsTimestampExample {


    public static void main(String[] args) throws Exception {
        // Criação e configuração da fábrica de conexões IBM MQ
        MQQueueConnectionFactory connectionFactory = new MQQueueConnectionFactory();
        connectionFactory.setHostName("localhost");  // Defina o hostname do seu servidor IBM MQ
        connectionFactory.setPort(1414);  // Defina a porta do servidor IBM MQ
        connectionFactory.setQueueManager("QM1");  // Defina o nome do seu Queue Manager
        connectionFactory.setChannel("DEV.ADMIN.SVRCONN");  // Defina o nome do seu canal
        connectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);  // Defina o tipo de transporte (cliente ou bindings)

        // Criação da conexão e sessão
        try (Connection connection = connectionFactory.createConnection("admin", "passw0rd")) {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("DEV.QUEUE.1");  // Defina o nome da fila

            // Criação do produtor e consumidor de mensagens
            MessageProducer producer = session.createProducer(queue);
            MessageConsumer consumer = session.createConsumer(queue);

            // Iniciando a conexão
            connection.start();

            // Enviando uma mensagem
            TextMessage message = session.createTextMessage("Hello, JMS!");
            producer.send(message);

            // Recebendo a mensagem
            TextMessage receivedMessage = (TextMessage) consumer.receive();
            System.out.println(receivedMessage);

            // Obtendo o timestamp da mensagem
            long jmsTimestamp = receivedMessage.getJMSTimestamp();
            System.out.println("Message sent at: " + jmsTimestamp);

            // Convertendo o timestamp para uma data legível
            java.util.Date date = new java.util.Date(jmsTimestamp);
            System.out.println("Message sent at (formatted): " + date);
        }
    }
}
