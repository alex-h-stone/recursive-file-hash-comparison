package dev.alexhstone.queue;

import dev.alexhstone.model.queue.WorkItem;

public interface QueuePublisher extends QueueLifecycle {

    Status publish(WorkItem workItem);
}
