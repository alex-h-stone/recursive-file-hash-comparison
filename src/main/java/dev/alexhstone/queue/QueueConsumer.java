package dev.alexhstone.queue;

import dev.alexhstone.model.workitem.FileWorkItem;

import java.util.Optional;

public interface QueueConsumer extends QueueLifecycle {
    Optional<FileWorkItem> consumeMessage();
}
