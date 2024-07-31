package com.santander.kpv.utils;

import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.common.CommonConstants;
import jakarta.jms.*;

import java.util.Enumeration;

public class IBMQueueBrowserUtils {
    private static final String QUEUE_MANAGER = "QM1";
    private static final String CHANNEL = "DEV.ADMIN.SVRCONN";
    private static final String QUEUE_NAME = "DEV.QUEUE.1";

    public static void rastreiaFila2(String nomeDaFila) throws JMSException  {
        Connection connection = null;
        Session session = null;

        try {
            // Configurar a fábrica de conexão
            MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
            mqQueueConnectionFactory.setHostName("localhost");
            mqQueueConnectionFactory.setPort(1414);
            mqQueueConnectionFactory.setQueueManager(QUEUE_MANAGER);
            mqQueueConnectionFactory.setChannel(CHANNEL);
            mqQueueConnectionFactory.setCCSID(1208);
            mqQueueConnectionFactory.setClientReconnectTimeout(5000);
            mqQueueConnectionFactory.setAppName("kpv.blindagem");
            mqQueueConnectionFactory.setTransportType(CommonConstants.WMQ_CM_CLIENT);
            mqQueueConnectionFactory.setStringProperty(CommonConstants.USERID, "admin");
            mqQueueConnectionFactory.setStringProperty(CommonConstants.PASSWORD, "passw0rd");
            // Criar a conexão
            connection= mqQueueConnectionFactory.createConnection();
            connection.start();

            // Criar a sessão
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Acessar a fila
            Queue queue = session.createQueue(nomeDaFila);
            QueueBrowser browser = session.createBrowser(queue);

            // Percorrer as mensagens na fila
            Enumeration<?> messages = browser.getEnumeration();
            int contador = 1;
            while (messages.hasMoreElements()) {
                Message message = (Message) messages.nextElement();

                System.out.println(contador++ + "////////////////////////////////////");
                printMessageProperties(message);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            // Fechar recursos
            try {
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    private static void printMessageProperties(Message message) throws JMSException {
        System.out.println("Message ID: " + message.getJMSMessageID());
        System.out.println("Correlation ID: " + message.getJMSCorrelationID());
        System.out.println("Timestamp: " + message.getJMSTimestamp());
        System.out.println("Delivery Mode: " + message.getJMSDeliveryMode());
        System.out.println("Expiration: " + message.getJMSExpiration());
        System.out.println("Priority: " + message.getJMSPriority());

        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            System.out.println(message);
            System.out.println("Text: " + textMessage.getText());
        }
        // Adicionar outros tipos de mensagens se necessário
    }
}
