package dev.alexhstone.model.fileworkitem;

import com.google.gson.Gson;
import dev.alexhstone.model.GsonFactory;

public class FileWorkItemDeserializer {

    private final Gson gson = GsonFactory.getGsonInstance();

    public FileWorkItem fromJson(String workItemJson) {
        return gson.fromJson(workItemJson, FileWorkItem.class);
    }
}
