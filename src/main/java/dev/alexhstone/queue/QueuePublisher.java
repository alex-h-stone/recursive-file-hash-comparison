package dev.alexhstone.queue;

import dev.alexhstone.model.workitem.WorkItem;

public interface QueuePublisher extends QueueLifecycle {

    Status publish(WorkItem workItem);
}
