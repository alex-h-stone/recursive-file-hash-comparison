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

    @Scheduled(fixedRate = 5_000) // 5 sec
    public void publishWorkItems() {
        publishWorkItemsToQueue.execute();
    }

    @Scheduled(fixedRate = 5_000) // 5 sec
    public void processWorkItems() {
        processWorkItemsFromTheQueue.execute();
    }
}
