package dev.alexhstone.queue;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.DeliveryMode;
import jakarta.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.JmsDestinationAccessor;

import java.time.Duration;

@Configuration
public class JmsConfig {

    private static final long TIME_TO_LIVE_24_HOURS = Duration.ofHours(24).toMillis();
    private static final int NORMAL_PRIORITY = 4;

    @Value("${application.queue.jmsBrokerUrl}")
    private String jmsBrokerUrl;

    @Value("${application.queue.jmsQueueName}")
    private String jmsQueueName;

    @Bean
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory(jmsBrokerUrl);
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);

        jmsTemplate.setDefaultDestinationName(jmsQueueName);
        jmsTemplate.setPriority(NORMAL_PRIORITY);
        jmsTemplate.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        jmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);
        jmsTemplate.setTimeToLive(TIME_TO_LIVE_24_HOURS);
        jmsTemplate.setReceiveTimeout(JmsDestinationAccessor.RECEIVE_TIMEOUT_INDEFINITE_WAIT);

        return jmsTemplate;
    }
}
