package dev.alexhstone.config;

import dev.alexhstone.consumer.WorkItemConsumer;
import dev.alexhstone.producer.WorkItemProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@EnableScheduling
@AllArgsConstructor
public class BackgroundTasks {

    private final AtomicBoolean hasPublishWorkItemsRun = new AtomicBoolean(false);
    private final AtomicBoolean hasProcessWorkItemsRun = new AtomicBoolean(false);

    private final WorkItemProducer workItemProducer;
    private final WorkItemConsumer workItemConsumer;

    @Async("produceWorkItemExecutor")
    @Scheduled(initialDelay = 4_000)
    public void runWorkItemProducer() {
        if (!hasPublishWorkItemsRun.get()) {
            workItemProducer.execute();
            log.info("Completed runWorkItemProducer");
            hasPublishWorkItemsRun.set(true);
        }
    }

    @Async("consumeWorkItemExecutor")
    @Scheduled(initialDelay = 4_000)
    public void runWorkItemConsumer() {
        if (!hasProcessWorkItemsRun.get()) {
            workItemConsumer.execute();
            log.info("Completed runWorkItemConsumer");
            hasProcessWorkItemsRun.set(true);
        }
    }
}
