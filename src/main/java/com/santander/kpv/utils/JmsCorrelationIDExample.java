package com.santander.kpv.utils;

import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory;
import jakarta.jms.*;

import com.ibm.msg.client.wmq.WMQConstants;

import java.util.UUID;

public class JmsCorrelationIDExample {

    public static void main(String[] args) {
        String queueManager = "QM1";
        String requestQueueName = "REQUEST.QUEUE";
        String responseQueueName = "RESPONSE.QUEUE";
        String host = "localhost";
        int port = 1414;
        String channel = "DEV.ADMIN.SVRCONN";
        String user = "admin";
        String password = "passw0rd";

        MQQueueConnectionFactory factory = new MQQueueConnectionFactory();
        try {
            factory.setHostName(host);
            factory.setPort(port);
            factory.setQueueManager(queueManager);
            factory.setChannel(channel);
            factory.setTransportType(WMQConstants.WMQ_CM_CLIENT);

            JMSContext context = factory.createContext(user, password);
            Queue requestQueue = context.createQueue("queue:///" + "DEV.QUEUE.1");
            Queue responseQueue = context.createQueue("queue:///" + "DEV.QUEUE.2");

            // Enviar mensagem
            String correlationID = "ID:" + UUID.randomUUID().toString().replace("-", "");
            TextMessage message = context.createTextMessage("Mensagem de teste");
            message.setJMSCorrelationID(correlationID);
            context.createProducer().send(requestQueue, message);
            System.out.println("Mensagem enviada com CorrelationID: " + correlationID);

            // Receber mensagem
            String messageSelector = "JMSCorrelationID = '" + correlationID + "'";
            JMSConsumer consumer = context.createConsumer(responseQueue, messageSelector);
            Message responseMessage = consumer.receive(15000);
            if (responseMessage == null) {
                System.out.println("Não capturou mensagem de resposta.");
            } else {
                System.out.println("Mensagem recebida com CorrelationID: " + responseMessage.getJMSCorrelationID());
                if (responseMessage instanceof TextMessage) {
                    String mensagemRetorno = ((TextMessage) responseMessage).getText();
                    System.out.println("Conteúdo da mensagem: " + mensagemRetorno);
                }
            }

            context.close();
        } catch (JMSException  e) {
            e.printStackTrace();
        }
    }
}
