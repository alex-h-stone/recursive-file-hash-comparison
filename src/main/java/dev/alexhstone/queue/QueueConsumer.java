package dev.alexhstone.queue;

import dev.alexhstone.model.queue.WorkItem;

import java.util.Optional;

public interface QueueConsumer extends QueueLifecycle{
    Optional<WorkItem> consumeMessage();
}
