package dev.alexhstone.queue;

import dev.alexhstone.model.workitem.WorkItem;
import dev.alexhstone.model.workitem.WorkItemDeserializer;
import dev.alexhstone.model.workitem.WorkItemSerializer;
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
    private final WorkItemDeserializer deserializer = new WorkItemDeserializer();
    private final WorkItemSerializer serializer = new WorkItemSerializer();


    @Override
    public void initialise() {
        // Do nothing
    }

    public Status destroy() {
        log.info("Consumed: {} messages", toPrettyPrint(NUMBER_OF_MESSAGES_CONSUMED));
        log.info("Published: {} messages", toPrettyPrint(NUMBER_OF_MESSAGES_PUBLISHED));
        return Status.SUCCESS;
    }

    private String toPrettyPrint(AtomicLong atomicLongToFormat) {
        long longNumberToFormat = atomicLongToFormat.get();
        return PrettyPrintNumberFormatter.format(longNumberToFormat);
    }

    public Status publish(WorkItem workItem) {
        String workItemAsJson = serializer.toJson(workItem);
        return publish(workItem.getId(), workItemAsJson);
    }

    private Status publish(String id, String messageText) {
        log.debug("About to publish the message with ID: [{}] and messageText: [{}]",
                id, messageText);

        TextMessage textMessage;
        try {
            jmsTemplate.convertAndSend(messageText);
        } catch (JmsException e) {
            log.error("Unable to publish the message with ID: [{}] and messageText: [{}] because of the error: [{}]",
                    id, messageText, e.getMessage(), e);
            return Status.FAILURE;
        }

        NUMBER_OF_MESSAGES_PUBLISHED.incrementAndGet();
        return Status.SUCCESS;
    }

    public Optional<WorkItem> consumeMessage() {
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

        WorkItem workItem = deserializer.fromJson(messageText);
        NUMBER_OF_MESSAGES_CONSUMED.incrementAndGet();
        log.debug("Successfully dequeued the work item with Id [{}]", workItem.getId());
        return Optional.of(workItem);
    }
}
