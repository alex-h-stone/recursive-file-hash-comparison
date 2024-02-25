package dev.alexhstone.queue;

import dev.alexhstone.model.fileworkitem.FileWorkItem;

public interface QueuePublisher extends QueueLifecycle {

    Status publish(FileWorkItem fileWorkItem);

    long getNumberOfMessagesPublished();
}
