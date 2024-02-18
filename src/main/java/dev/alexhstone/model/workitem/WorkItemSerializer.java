package dev.alexhstone.model.workitem;

import com.google.gson.Gson;
import dev.alexhstone.model.GsonFactory;

public class WorkItemSerializer {

    private final Gson gson = GsonFactory.getGsonInstance();

    public String toJson(FileWorkItem fileWorkItem) {
        return gson.toJson(fileWorkItem);
    }
}
