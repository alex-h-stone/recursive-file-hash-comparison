package dev.alexhstone.queue;

import dev.alexhstone.model.fileworkitem.FileWorkItem;
import dev.alexhstone.model.fileworkitem.FileWorkItemDeserializer;
import dev.alexhstone.model.fileworkitem.FileWorkItemSerializer;
import dev.alexhstone.util.PrettyPrintNumberFormatter;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.TextMessage;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@AllArgsConstructor
public class DurableQueue implements QueuePublisher, QueueConsumer {

    private static final AtomicLong NUMBER_OF_MESSAGES_CONSUMED = new AtomicLong();
    private static final AtomicLong NUMBER_OF_MESSAGES_PUBLISHED = new AtomicLong();

    private final JmsTemplate jmsTemplate;
    private final FileWorkItemDeserializer deserializer = new FileWorkItemDeserializer();
    private final FileWorkItemSerializer serializer = new FileWorkItemSerializer();


    @Override
    public void initialise() {
        // Do nothing
    }

    public Status destroy() {
        log.info("Consumed: {} messages", toPrettyPrint(NUMBER_OF_MESSAGES_CONSUMED));
        log.info("Published: {} messages", toPrettyPrint(NUMBER_OF_MESSAGES_PUBLISHED));
        return Status.SUCCESS;
    }

    @Override
    public long getNumberOfMessagesConsumed() {
        return NUMBER_OF_MESSAGES_CONSUMED.get();
    }

    @Override
    public long getNumberOfMessagesPublished() {
        return NUMBER_OF_MESSAGES_PUBLISHED.get();
    }

    private String toPrettyPrint(AtomicLong atomicLongToFormat) {
        long longNumberToFormat = atomicLongToFormat.get();
        return PrettyPrintNumberFormatter.format(longNumberToFormat);
    }

    public Status publish(FileWorkItem fileWorkItem) {
        String workItemAsJson = serializer.toJson(fileWorkItem);
        return publish(fileWorkItem.getId(), workItemAsJson);
    }

    private Status publish(String id, String messageText) {
        log.debug("About to publish the message with ID: [{}] and messageText: [{}]",
                id, messageText);

        TextMessage textMessage;
        try {
            jmsTemplate.convertAndSend(messageText);
        } catch (JmsException e) {
            log.info("Unable to publish the message with ID: [{}] because of the error: [{}] and cause [{}]",
                    id, e.getMessage(), e.getCause().getMessage());
            return Status.FAILURE;
        }

        NUMBER_OF_MESSAGES_PUBLISHED.incrementAndGet();
        return Status.SUCCESS;
    }

    public Optional<FileWorkItem> consumeMessage() {
        Message message = jmsTemplate.receive();
        if (Objects.isNull(message)) {
            log.debug("Received null message, so returning empty");
            return Optional.empty();
        }

        String messageText;
        try {
            messageText = message.getBody(String.class);
        } catch (JMSException e) {
            log.error("Unable to consume the message because of the error: [{}]",
                    e.getMessage(), e);
            return Optional.empty();
        }

        FileWorkItem fileWorkItem = deserializer.fromJson(messageText);
        NUMBER_OF_MESSAGES_CONSUMED.incrementAndGet();
        log.debug("Successfully dequeued the work item with Id [{}]", fileWorkItem.getId());
        return Optional.of(fileWorkItem);
    }
}
