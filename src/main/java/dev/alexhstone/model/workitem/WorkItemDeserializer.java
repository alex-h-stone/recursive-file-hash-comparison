package dev.alexhstone.model.workitem;

import com.google.gson.Gson;
import dev.alexhstone.model.GsonFactory;

public class WorkItemDeserializer {

    private final Gson gson = GsonFactory.getGsonInstance();

    public FileWorkItem fromJson(String workItemJson) {
        return gson.fromJson(workItemJson, FileWorkItem.class);
    }
}
