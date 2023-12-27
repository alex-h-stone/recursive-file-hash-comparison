package dev.alexhstone.queue;

import dev.alexhstone.producer.Status;

public interface QueueLifecycle {

    void initialise();

    Status destroy();
}
