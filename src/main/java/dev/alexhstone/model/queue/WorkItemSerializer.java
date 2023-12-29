package dev.alexhstone.model.queue;

import com.google.gson.Gson;

public class WorkItemSerializer {

    private final Gson gson = GsonFactory.getGsonInstance();

    public String toJson(WorkItem workItem) {
        return gson.toJson(workItem);
    }
}
