package dev.alexhstone.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alexhstone.model.hashresult.HashResult;
import dev.alexhstone.model.hashresult.HashResultSerializerAndDeserializer;
import dev.alexhstone.model.workitem.FileWorkItem;
import dev.alexhstone.model.workitem.WorkItemGsonSerializerAndDeserializer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GsonFactory {

    public static Gson getGsonInstance() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(FileWorkItem.class,
                        new WorkItemGsonSerializerAndDeserializer())
                .registerTypeAdapter(HashResult.class,
                        new HashResultSerializerAndDeserializer())
                .create();
    }
}
