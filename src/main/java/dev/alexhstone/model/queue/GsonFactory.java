package dev.alexhstone.model.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;

@UtilityClass
class GsonFactory {

    static Gson getGsonInstance() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(WorkItem.class,
                        new WorkItemGsonSerializerAndDeserializer())
                .create();
    }
}
