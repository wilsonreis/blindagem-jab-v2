package com.santander.kpv.utils;

import jakarta.jms.*;

import java.util.Enumeration;

public class QueueBrowserUtils {
    public static synchronized String extracted(QueueSession session, Queue responseQueue, String correlationID) throws JMSException {
        QueueBrowser browser = session.createBrowser(responseQueue);
        Enumeration messages = browser.getEnumeration();
        while (messages.hasMoreElements()) {
            Message message = (Message) messages.nextElement();
            if (message.getJMSCorrelationID().contains("blindagem")) {
                if (correlationID.equals(message.getJMSCorrelationID())) {
                    String correlation = message.getJMSCorrelationID();
                    System.out.println("message.getJMSCorrelationID() achou : " + message.getJMSCorrelationID());
                    browser.close();
                    return correlation;
                } else {
                    System.out.println("message.getJMSCorrelationID() não achou : " + message.getJMSCorrelationID());
                }
            }
        }
        browser.close();
        return null;
    }

    private QueueBrowserUtils() {
    }
}
