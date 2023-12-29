package dev.alexhstone.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alexhstone.config.ApplicationConfiguration;
import dev.alexhstone.model.queue.WorkItem;
import dev.alexhstone.model.queue.WorkItemSerializerAndDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang3.StringUtils;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
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

    private static final int NORMAL_PRIORITY = 4;
    private static final long TIME_TO_LIVE_SIX_HOURS = Duration.ofHours(6).toMillis();
    private static final long WITH_TWO_SECOND_TIMEOUT = Duration.ofSeconds(2).toMillis();

    private final String brokerURL;
    private final Gson gson;
    private final ActiveMQConnectionFactory connectionFactory;
    private final String queueName;

    private Session session;
    private Connection connection;
    private MessageProducer producer;
    private MessageConsumer consumer;

    public static void main(String[] args) {
        DurableQueueImpl queue = new DurableQueueImpl();
        queue.initialise();
        String absolutePath = Paths.get(".").toFile().getAbsolutePath();
        queue.publish(WorkItem.builder()
                .id("WorkItem_ID")
                .name("workItemName.dat")
                .absolutePath(absolutePath)
                .absolutePathToWorkingDirectory(absolutePath)
                .sizeInBytes(BigInteger.valueOf(250))
                .workItemCreationTime(Instant.now())
                .build());
        Optional<WorkItem> workItem = queue.consumeMessage();
        log.info("Consumed workItem: {}", workItem);
        queue.destroy();
    }

    public DurableQueueImpl() {
        this.brokerURL = ApplicationConfiguration.getActiveMQBrokerURL();
        this.queueName = ApplicationConfiguration.getActiveMQQueueName();
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(WorkItem.class,
                        new WorkItemSerializerAndDeserializer())
                .create();
        this.connectionFactory = new ActiveMQConnectionFactory(brokerURL);
    }

    public void initialise() {
        log.info("About to initialise the queue [{}] with brokerURL: [{}]",
                queueName, brokerURL);
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(Session.AUTO_ACKNOWLEDGE);
            Queue destination = session.createQueue(queueName);
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            consumer = session.createConsumer(destination, StringUtils.EMPTY);
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

    public Status publish(WorkItem workItem) {
        String workItemAsJson = gson.toJson(workItem);
        return publish(workItem.getId(), workItemAsJson);
    }

    private Status publish(String id, String messageText) {
        log.debug("About to publish the message with ID: [{}] and messageText: [{}]",
                id, messageText);

        TextMessage textMessage;
        try {
            textMessage = session.createTextMessage(messageText);
            textMessage.setJMSMessageID(id);
            producer.send(textMessage,
                    DeliveryMode.PERSISTENT,
                    NORMAL_PRIORITY,
                    TIME_TO_LIVE_SIX_HOURS);
        } catch (JMSException e) {
            log.error("Unable to publish the message with ID: [{}] and messageText: [{}] because of the error: [{}]",
                    id, messageText, e.getMessage(), e);
            return Status.FAILURE;
        }

        return Status.SUCCESS;
    }

    public Optional<WorkItem> consumeMessage() {
        try {
            Message message = consumer.receive(WITH_TWO_SECOND_TIMEOUT);
            if (message == null) {
                log.info("Received null message, so returning empty");
                return Optional.empty();
            }

            if (message instanceof TextMessage textMessage) {
                String messageText = textMessage.getText();
                WorkItem workItem = gson.fromJson(messageText, WorkItem.class);
                log.debug("Successfully dequeued the work item with ID [{}]", workItem.getId());
                return Optional.of(workItem);
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
