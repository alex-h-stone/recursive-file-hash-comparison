package dev.alexhstone.model.fileworkitem;

import com.google.gson.Gson;
import dev.alexhstone.model.GsonFactory;

public class FileWorkItemSerializer {

    private final Gson gson = GsonFactory.getGsonInstance();

    public String toJson(FileWorkItem fileWorkItem) {
        return gson.toJson(fileWorkItem);
    }
}
