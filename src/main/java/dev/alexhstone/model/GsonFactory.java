package dev.alexhstone.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alexhstone.model.datastore.HashResult;
import dev.alexhstone.model.datastore.HashResultSerializerAndDeserializer;
import dev.alexhstone.model.queue.WorkItem;
import dev.alexhstone.model.queue.WorkItemGsonSerializerAndDeserializer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GsonFactory {

    public static Gson getGsonInstance() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(WorkItem.class,
                        new WorkItemGsonSerializerAndDeserializer())
                .registerTypeAdapter(HashResult.class,
                        new HashResultSerializerAndDeserializer())
                .create();
    }
}
