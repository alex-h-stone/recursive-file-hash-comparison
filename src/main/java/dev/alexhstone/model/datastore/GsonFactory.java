package dev.alexhstone.model.datastore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alexhstone.model.queue.WorkItem;
import lombok.experimental.UtilityClass;

@UtilityClass
class GsonFactory {

    static Gson getGsonInstance() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(WorkItem.class,
                        new HashResultSerializerAndDeserializer())
                .create();
    }
}
