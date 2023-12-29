package dev.alexhstone.model.queue;

import com.google.gson.Gson;
import dev.alexhstone.model.GsonFactory;

public class WorkItemSerializer {

    private final Gson gson = GsonFactory.getGsonInstance();

    public String toJson(WorkItem workItem) {
        return gson.toJson(workItem);
    }
}
