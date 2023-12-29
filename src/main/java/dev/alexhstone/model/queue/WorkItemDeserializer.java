package dev.alexhstone.model.queue;

import com.google.gson.Gson;
import dev.alexhstone.model.GsonFactory;

public class WorkItemDeserializer {

    private final Gson gson = GsonFactory.getGsonInstance();

    public WorkItem fromJson(String workItemJson) {
        return gson.fromJson(workItemJson, WorkItem.class);
    }
}
