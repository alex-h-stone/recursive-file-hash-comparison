package dev.alexhstone.model.queue;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.Instant;

public class WorkItemSerializerAndDeserializer implements JsonSerializer<WorkItem>, JsonDeserializer<WorkItem> {

    @Override
    public JsonElement serialize(WorkItem workItem,
                                 Type type,
                                 JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", workItem.getId());
        jsonObject.addProperty("name", workItem.getName());
        jsonObject.addProperty("absolutePath", workItem.getAbsolutePath());
        jsonObject.addProperty("absolutePathToWorkingDirectory", workItem.getAbsolutePathToWorkingDirectory());
        jsonObject.addProperty("sizeInBytes", workItem.getSizeInBytes());
        jsonObject.addProperty("workItemCreationTime", workItem.getWorkItemCreationTime().toEpochMilli());

        return jsonObject;
    }

    @Override
    public WorkItem deserialize(JsonElement json,
                                Type type,
                                JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        WorkItem workItem = WorkItem.builder()
                .id(jsonObject.get("id").getAsString())
                .name(jsonObject.get("name").getAsString())
                .absolutePath(jsonObject.get("absolutePath").getAsString())
                .absolutePathToWorkingDirectory(jsonObject.get("absolutePathToWorkingDirectory").getAsString())
                .sizeInBytes(jsonObject.get("sizeInBytes").getAsBigInteger())
                .workItemCreationTime(Instant.ofEpochSecond(jsonObject.get("workItemCreationTime").getAsLong()))
                .build();

        return workItem;
    }
}
