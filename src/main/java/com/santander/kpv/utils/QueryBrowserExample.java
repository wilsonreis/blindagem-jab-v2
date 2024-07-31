package com.santander.kpv.utils;

import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;

public class QueryBrowserExample {

    public static void main(String[] args) {
        String queueManagerName = "QM1";
        String queueName = "REQUEST.QUEUE";
        String host = "localhost";
        int port = 1414;
        String channel = "SYSTEM.DEF.SVRCONN";
        String user = "mqm";
        String password = "password";
/*
        try {
            MQQueueManager queueManager = new MQQueueManager(queueManagerName, host, port, channel, user, password);
            int openOptions = MQConstants.MQOO_INPUT_AS_Q_DEF | MQConstants.MQOO_BROWSE;
            MQQueue queue = queueManager.accessQueue(queueName, openOptions);

            MQGetMessageOptions gmo = new MQGetMessageOptions();
            gmo.options = MQConstants.MQGMO_BROWSE_FIRST;

            MQMessage message = new MQMessage();
            queue.get(message, gmo);

            String messageContent = message.readStringOfByteLength(message.getMessageLength());
            System.out.println("Mensagem na fila: " + messageContent);

            queue.close();
            queueManager.disconnect();
        } catch (MQException | IOException e) {
            e.printStackTrace();
        }*/
    }
}
