package dev.alexhstone;

import dev.alexhstone.consumer.ProcessWorkItemsFromTheQueue;
import dev.alexhstone.producer.PublishWorkItemsToQueue;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BackgroundTasks {

    private final PublishWorkItemsToQueue publishWorkItemsToQueue;
    private final ProcessWorkItemsFromTheQueue processWorkItemsFromTheQueue;

    @Scheduled(fixedRate = 50_000)
    public void publishWorkItems() {
        publishWorkItemsToQueue.execute();
    }

    @Scheduled(fixedRate = 50_000)
    public void processWorkItems() {
        processWorkItemsFromTheQueue.execute();
    }
}
