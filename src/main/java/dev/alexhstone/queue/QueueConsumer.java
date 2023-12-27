package dev.alexhstone.queue;

import dev.alexhstone.model.queue.FileWorkItem;

import java.util.Optional;

public interface QueueConsumer extends QueueLifecycle{
    Optional<FileWorkItem> consumeMessage();
}
