package dev.alexhstone.queue;

import dev.alexhstone.model.workitem.FileWorkItem;

public interface QueuePublisher extends QueueLifecycle {

    Status publish(FileWorkItem fileWorkItem);
}
