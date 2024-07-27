package com.santander.kpv.utils;

import jakarta.jms.*;
import org.springframework.jms.core.MessageCreator;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ExtendedMessageCreator<T extends Message> implements MessageCreator {
    private Message message;
    private Class<T> messageType;

    @Override
    public Message createMessage(Session session) throws JMSException {
        if (this.messageType.equals(TextMessage.class)) {
            this.message = session.createTextMessage();
        } else if (this.messageType.equals(MapMessage.class)) {
            this.message = session.createMapMessage();
        } else if (this.messageType.equals(BytesMessage.class)) {
            this.message = session.createBytesMessage();
        } else if (this.messageType.equals(ObjectMessage.class)) {
            this.message = session.createObjectMessage();
        } else if (this.messageType.equals(StreamMessage.class)) {
            this.message = session.createStreamMessage();
        } else if (this.messageType.equals(Message.class)) {
            this.message = session.createMessage();
        }

        this.setParams(this.message);
        return this.message;
    }

    public ExtendedMessageCreator() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        ParameterizedType type = (ParameterizedType) genericSuperclass;
        this.messageType = (Class<T>) type.getActualTypeArguments()[0];
    }

    public void setParams(Message message) throws JMSException {
        // Método a ser sobrescrito nas subclasses ou instâncias anônimas
    }

    public Message getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " - message: \n" + (this.message != null ? this.message.toString() : "message not yet created!");
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
