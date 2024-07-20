package com.santander.kpv.config;



import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import com.santander.kpv.exceptions.MyRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Session;

@Configuration
@EnableJms
@Slf4j
public class MQTextMessageConfiguration {

    @Value("${ibm.mq.host}")
    private String mqHostName;

    @Value("${ibm.mq.port}")
    private int mqPort;

    @Value("${ibm.mq.queueManager}")
    private String mqQueueManager;

    @Value("${ibm.mq.channel}")
    private String mqChannel;

    @Value("${ibm.mq.user}")
    private String user;
    @Value("${ibm.mq.password}")
    private String password;
    @Value("${ibm.mq.queueRequest}")
    private String queueRequest;

    @Value("${ibm.mq.queueResponse}")
    private String queueResponse;

    public String getQueueRequest() {
        return queueRequest;
    }

    public String getQueueResponse() {
        return queueResponse;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getMqHostName() {
        return mqHostName;
    }

    public int getMqPort() {
        return mqPort;
    }

    public String getMqQueueManager() {
        return mqQueueManager;
    }

    public String getMqChannel() {
        return mqChannel;
    }

    @Bean("myMQConnectionFactory")
    public MQConnectionFactory  myMQConnectionFactory () {
        try {
            MQConnectionFactory  myMQConnectionFactory = new MQConnectionFactory ();
            myMQConnectionFactory.setHostName(this.getMqHostName());
            myMQConnectionFactory.setPort(this.getMqPort());
            myMQConnectionFactory.setQueueManager(this.getMqQueueManager());
            myMQConnectionFactory.setChannel(this.getMqChannel());
            myMQConnectionFactory.setCCSID(1208);
            //myMQConnectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
            myMQConnectionFactory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
            myMQConnectionFactory.setStringProperty(WMQConstants.USERID, "admin");
            myMQConnectionFactory.setStringProperty(WMQConstants.PASSWORD, "passw0rd");
            log.info("Sucesso ao conectar na fila");
            return myMQConnectionFactory;
        } catch (Exception e) {
            throw new MyRuntimeException(e);
        }
    }
}