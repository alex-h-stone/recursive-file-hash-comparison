package dev.alexhstone.consumer;

import dev.alexhstone.storage.FileWorkItemQueueFacade;

public class ProcessFilesOnQueue {

    public static void main(String[] args) {
        ProcessFilesOnQueue processFilesOnQueue = new ProcessFilesOnQueue();
        processFilesOnQueue.execute();
    }

    public ProcessFilesOnQueue() {
        FileWorkItemQueueFacade fileWorkItemQueueFacade = new FileWorkItemQueueFacade();

        fileWorkItemQueueFacade.retrieveNextItem();
    }

    private void execute() {

    }
}
