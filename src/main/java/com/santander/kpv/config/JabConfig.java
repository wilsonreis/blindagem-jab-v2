package com.santander.kpv.config;

import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.common.CommonConstants;
import com.santander.kpv.utils.Generated;
import com.santander.kpv.utils.ResolverUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import jakarta.jms.*;

@Configuration
@EnableJms
@Slf4j
@Generated
public class JabConfig {

    @Value("${ibm.mq.host}")
    private String mqHostName;

    @Value("${ibm.mq.port}")
    private int mqPort;

    @Value("${ibm.mq.queueManager}")
    private String mqQueueManager;

    @Value("${ibm.mq.channel}")
    private String mqChannel;

    @Value("${ibm.mq.jmsExpiration}")
    private long jmsExpiration;

    @Bean("mqQueueConnectionFactory")
    public MQQueueConnectionFactory mqQueueConnectionFactory() {
        MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
        try {
            mqQueueConnectionFactory.setHostName(this.getMqHostName());
            mqQueueConnectionFactory.setPort(this.getMqPort());
            mqQueueConnectionFactory.setQueueManager(this.getMqQueueManager());
            mqQueueConnectionFactory.setChannel(this.getMqChannel());
            mqQueueConnectionFactory.setCCSID(1208);
            mqQueueConnectionFactory.setClientReconnectTimeout(5000);
            mqQueueConnectionFactory.setAppName("kpv.blindagem");
            mqQueueConnectionFactory.setTransportType(CommonConstants.WMQ_CM_CLIENT);
            mqQueueConnectionFactory.setStringProperty(CommonConstants.USERID, "admin");
            mqQueueConnectionFactory.setStringProperty(CommonConstants.PASSWORD, "passw0rd");
            log.info("Sucesso ao conectar na fila");
        } catch (Exception e) {
            log.error("Error ao criar jms connection factory: {}", e.getMessage());
        }
        return mqQueueConnectionFactory;
    }

    @Bean("myTemplate")
    public JmsTemplate myTemplate(MQQueueConnectionFactory mqQueueConnectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(mqQueueConnectionFactory);
        jmsTemplate.setPriority(1);
        //jmsTemplate.setDestinationResolver(new ResolverUtils());
        jmsTemplate.setReceiveTimeout(getJmsExpiration());
        jmsTemplate.setConnectionFactory(mqQueueConnectionFactory);
        jmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        return jmsTemplate;
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

    public long getJmsExpiration() {
        return jmsExpiration;
    }
}
