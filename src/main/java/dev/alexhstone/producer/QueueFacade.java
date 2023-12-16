package dev.alexhstone.producer;

import org.infobip.lib.popout.FileQueue;

public class QueueFacade {

    private final FileQueue<String> queue;

    public QueueFacade() {
        queue = FileQueue.<String>synced().build();
    }

}
