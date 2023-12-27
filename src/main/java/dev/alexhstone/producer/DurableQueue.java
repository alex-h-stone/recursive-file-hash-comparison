package dev.alexhstone.producer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alexhstone.config.ApplicationConfiguration;
import dev.alexhstone.model.FileWorkItemSerializerAndDeserializer;
import dev.alexhstone.model.queue.FileWorkItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.math.BigInteger;

@Slf4j
public class DurableQueue {

    private final String brokerURL;
    private final Gson gson;
    private final ActiveMQConnectionFactory connectionFactory;
    private final String queueName;

    private Session session;
    private Connection connection;
    private Queue destination;
    private MessageProducer producer;

    public DurableQueue() {
        this.brokerURL = ApplicationConfiguration.getActiveMQBrokerURL();
        this.queueName = ApplicationConfiguration.getActiveMQQueueName();
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(FileWorkItem.class,
                        new FileWorkItemSerializerAndDeserializer())
                .create();
        this.connectionFactory = new ActiveMQConnectionFactory(brokerURL);
    }

    public void initialise() {
        log.info("About to initialise the queue [{}] with brokerURL: [{}]",
                queueName, brokerURL);
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(Session.CLIENT_ACKNOWLEDGE);
            destination = session.createQueue(queueName);
            producer = session.createProducer(destination);
        } catch (JMSException e) {
            String message = "Unable to initialise the DurableQueue with brokerURL [%s] because of the  error: [%s]"
                    .formatted(brokerURL, e.getMessage());
            throw new RuntimeException(message, e);
        }
        log.info("Successfully initialised the queue [{}] with brokerURL: [{}]",
                queueName, brokerURL);
    }

    public Status destroy() {
        // Clean up
        try {
            session.close();
            connection.close();
        } catch (JMSException e) {
            log.error("Unable to destroy the DurableQueue because of the error: [{}]",
                    e.getMessage(), e);
            return Status.FAILURE;
        }

        return Status.SUCCESS;
    }

    public static void main(String[] args) {
        ActiveMQPublisher activeMQPublisher = new ActiveMQPublisher();
        activeMQPublisher.initialise();
        activeMQPublisher.doStuff();
    }

    public boolean publish(FileWorkItem fileWorkItem) {
        String workItemAsJson = gson.toJson(fileWorkItem);
        publish(workItemAsJson);

        return false;
    }

    public Status publish(String message) {
        log.debug("About to publish the message: [{}]", message);

        TextMessage textMessage;
        try {
            textMessage = session.createTextMessage(message);
            producer.send(textMessage);
        } catch (JMSException e) {
            log.error("Unable to publish the message [{}] because of the error: [{}]",
                    message, e.getMessage(), e);
            return Status.FAILURE;
        }

        return Status.SUCCESS;
    }

    private BigInteger getQueueSize() {
        // TODO should we add this, do we need it?
        return BigInteger.ZERO;
    }
}
