package dev.alexhstone.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alexhstone.config.ApplicationConfiguration;
import dev.alexhstone.model.FileWorkItemSerializerAndDeserializer;
import dev.alexhstone.model.queue.FileWorkItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang3.StringUtils;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
public class DurableQueueImpl implements QueuePublisher, QueueConsumer {

    private final String brokerURL;
    private final Gson gson;
    private final ActiveMQConnectionFactory connectionFactory;
    private final String queueName;

    private Session session;
    private Connection connection;
    private Queue destination;
    private MessageProducer producer;

    public static void main(String[] args) {
        DurableQueueImpl queue = new DurableQueueImpl();
        queue.initialise();
        String absolutePath = Paths.get(".").toFile().getAbsolutePath();
        queue.publish(FileWorkItem.builder()
                .id("WorkItem_ID")
                .absolutePath(absolutePath)
                .absolutePathToWorkingDirectory(absolutePath)
                .sizeInBytes(BigInteger.valueOf(2000))
                .workItemCreationTime(Instant.now())
                .build());
        Optional<FileWorkItem> workItem = queue.consumeMessage();
        log.info("workItem: {}", workItem);
        queue.destroy();
    }

    public DurableQueueImpl() {
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
            String message = "Unable to initialise the DurableQueueImpl with brokerURL [%s] because of the  error: [%s]"
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
            log.error("Unable to destroy the DurableQueueImpl because of the error: [{}]",
                    e.getMessage(), e);
            return Status.FAILURE;
        }

        return Status.SUCCESS;
    }

    public Status publish(FileWorkItem fileWorkItem) {
        String workItemAsJson = gson.toJson(fileWorkItem);
        return publish(fileWorkItem.getId(), workItemAsJson);
    }

    private Status publish(String id, String messageText) {
        log.debug("About to publish the message with ID: [{}] and messageText: [{}]",
                id, messageText);

        TextMessage textMessage;
        try {
            textMessage = session.createTextMessage(messageText);
            textMessage.setJMSMessageID(id);
            producer.send(textMessage);
        } catch (JMSException e) {
            log.error("Unable to publish the message with ID: [{}] and messageText: [{}] because of the error: [{}]",
                    id, messageText, e.getMessage(), e);
            return Status.FAILURE;
        }

        return Status.SUCCESS;
    }

    public Optional<FileWorkItem> consumeMessage() {
        try {
            MessageConsumer consumer = session.createConsumer(destination, StringUtils.EMPTY);
            Message message = consumer.receive(Duration.ofSeconds(2).toMillis());
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String messageText = textMessage.getText();
                FileWorkItem fileWorkItem = gson.fromJson(messageText, FileWorkItem.class);
                message.acknowledge();
                log.debug("Successfully dequeued and acknowledged the work item with ID [{}]", fileWorkItem.getId());
                return Optional.of(fileWorkItem);
            }
            log.error("Unable to cast the Message: [{}] as a TextMessage", message);
        } catch (JMSException e) {
            log.error("Unable to consume a message because of the error: [{}]",
                    e.getMessage(), e);
        }

        return Optional.empty();
    }


    public BigInteger getQueueSize() {
        // TODO should we add this, do we need it?
        return BigInteger.ZERO;
    }
}
