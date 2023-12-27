package dev.alexhstone.queue;

import dev.alexhstone.model.queue.FileWorkItem;

public interface QueuePublisher extends QueueLifecycle {

    Status publish(FileWorkItem fileWorkItem);
}
