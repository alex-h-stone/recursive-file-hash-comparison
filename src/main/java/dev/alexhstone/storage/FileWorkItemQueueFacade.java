package dev.alexhstone.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alexhstone.model.FileWorkItemSerializerAndDeserializer;
import dev.alexhstone.model.queue.FileWorkItem;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class FileWorkItemQueueFacade {

    private final Gson gson;
    private final ActiveMQPublisher activeMQPublisher;

    public FileWorkItemQueueFacade() {
        activeMQPublisher = new ActiveMQPublisher();
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(FileWorkItem.class,
                        new FileWorkItemSerializerAndDeserializer())
                .create();
        logNumberOfWorkItemsOnQueue();
    }

    public void publish(FileWorkItem fileWorkItem) {
        logNumberOfWorkItemsOnQueue();
        String workItemAsJson = gson.toJson(fileWorkItem);
        boolean added = activeMQPublisher.publish(workItemAsJson);

        if(!added){
            throw new RuntimeException("Unable to add fileWorkItem to the queue");
        }
    }

    public Optional<FileWorkItem> retrieveNextItem(){
        logNumberOfWorkItemsOnQueue();
        String nextItem = activeMQPublisher.poll();
        if(nextItem == null){
            return Optional.empty();
        }

        FileWorkItem fileWorkItem = gson.fromJson(nextItem, FileWorkItem.class);
        return Optional.of(fileWorkItem);
    }

    private void logNumberOfWorkItemsOnQueue() {
        log.info("Number of work items on the queue is {}", activeMQPublisher.getQueueSize());
    }
}
