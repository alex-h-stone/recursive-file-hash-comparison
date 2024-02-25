package dev.alexhstone.queue;

import dev.alexhstone.model.fileworkitem.FileWorkItem;

import java.util.Optional;

public interface QueueConsumer extends QueueLifecycle {
    Optional<FileWorkItem> consumeMessage();

    long getNumberOfMessagesConsumed();
}
