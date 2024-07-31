package com.santander.kpv.utils;

import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.StreamMessage;
import jakarta.jms.TextMessage;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

public class JMSMessageConverter {

    public static String convertMessageToString(Message message) throws JMSException {
        if (message instanceof TextMessage) {
            return ((TextMessage) message).getText();
        } else if (message instanceof BytesMessage) {
            return convertBytesMessageToString((BytesMessage) message);
        } else if (message instanceof ObjectMessage) {
            return convertObjectMessageToString((ObjectMessage) message);
        } else if (message instanceof StreamMessage) {
            return convertStreamMessageToString((StreamMessage) message);
        } else {
            return message.toString();
        }
    }

    private static String convertBytesMessageToString(BytesMessage message) throws JMSException {
        byte[] byteData = new byte[(int) message.getBodyLength()];
        message.readBytes(byteData);
        return new String(byteData, StandardCharsets.UTF_8);
    }

    private static String convertObjectMessageToString(ObjectMessage message) throws JMSException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(message.getObject());
            oos.flush();
            return new String(bos.toByteArray(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new JMSException("Failed to convert ObjectMessage to String: " + e.getMessage());
        }
    }

    private static String convertStreamMessageToString(StreamMessage message) throws JMSException {
        StringBuilder sb = new StringBuilder();
        try {
            while (true) {
                Object obj = message.readObject();
                if (obj == null) {
                    break;
                }
                sb.append(obj.toString()).append(" ");
            }
        } catch (Exception e) {
            // Handle the exception or end of message stream
        }
        return sb.toString().trim();
    }
}
