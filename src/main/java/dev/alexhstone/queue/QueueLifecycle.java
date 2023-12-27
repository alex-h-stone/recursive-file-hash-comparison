package dev.alexhstone.queue;

public interface QueueLifecycle {

    void initialise();

    Status destroy();
}
