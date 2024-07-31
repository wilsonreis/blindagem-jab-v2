package com.santander.kpv.utils;

import jakarta.jms.*;

import java.util.Enumeration;

public class QueueBrowserUtils {
    private static boolean extracted(QueueSession session, Queue responseQueue, String correlationID, boolean achou, String mensagemRetorno) throws JMSException {
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
        if (achou){
            return true;
        }
        return false;
    }
    private QueueBrowserUtils() {
    }
}
