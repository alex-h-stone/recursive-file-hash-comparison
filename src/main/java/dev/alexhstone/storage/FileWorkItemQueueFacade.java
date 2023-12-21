package dev.alexhstone.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alexhstone.config.ApplicationConfiguration;
import dev.alexhstone.model.FileWorkItemSerializer;
import dev.alexhstone.model.queue.FileWorkItem;
import org.infobip.lib.popout.FileQueue;

import java.util.Optional;

public class FileWorkItemQueueFacade {

    private final FileQueue<String> queue;
    private final Gson gson;

    public FileWorkItemQueueFacade() {
        queue = FileQueue.<String>synced()
                .folder(ApplicationConfiguration.getLocationOfFileBackedQueue())
                .build();
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(FileWorkItem.class,
                        new FileWorkItemSerializer())
                .create();
    }

    public void publish(FileWorkItem fileWorkItem) {
        String workItemAsJson = gson.toJson(fileWorkItem);
        boolean added = queue.add(workItemAsJson);

        if(!added){
            throw new RuntimeException("Unable to add fileWorkItem to the queue");
        }
    }

    public Optional<FileWorkItem> retrieveNextItem(){
        String nextItem = queue.poll();
        if(nextItem == null){
            return Optional.empty();
        }

        FileWorkItem fileWorkItem = gson.fromJson(nextItem, FileWorkItem.class);
        return Optional.of(fileWorkItem);
    }
}
