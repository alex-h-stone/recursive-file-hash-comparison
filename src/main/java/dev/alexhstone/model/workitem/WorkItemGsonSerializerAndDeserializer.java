package dev.alexhstone.model.workitem;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Objects;

public class WorkItemGsonSerializerAndDeserializer implements JsonSerializer<WorkItem>, JsonDeserializer<WorkItem> {

    @Override
    public JsonElement serialize(WorkItem workItem,
                                 Type type,
                                 JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", workItem.getName());
        jsonObject.addProperty("absolutePath", workItem.getAbsolutePath());
        jsonObject.addProperty("absolutePathToWorkingDirectory", workItem.getAbsolutePathToWorkingDirectory());
        jsonObject.addProperty("sizeInBytes", workItem.getSizeInBytes());
        jsonObject.addProperty("itemLastModifiedTime", toNullSafeMilliseconds(workItem.getItemLastModifiedTime()));
        jsonObject.addProperty("workItemCreationTime", toNullSafeMilliseconds(workItem.getWorkItemCreationTime()));

        return jsonObject;
    }

    @Override
    public WorkItem deserialize(JsonElement json,
                                Type type,
                                JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        WorkItem workItem = WorkItem.builder()
                .name(jsonObject.get("name").getAsString())
                .absolutePath(jsonObject.get("absolutePath").getAsString())
                .absolutePathToWorkingDirectory(jsonObject.get("absolutePathToWorkingDirectory").getAsString())
                .sizeInBytes(jsonObject.get("sizeInBytes").getAsBigInteger())
                .itemLastModifiedTime(toNullSafeInstant(jsonObject, "itemLastModifiedTime"))
                .workItemCreationTime(toNullSafeInstant(jsonObject, "workItemCreationTime"))
                .build();

        return workItem;
    }

    private Long toNullSafeMilliseconds(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.toEpochMilli();
    }

    private static Instant toNullSafeInstant(JsonObject jsonObject, String fieldName) {
        JsonElement jsonElement = jsonObject.get(fieldName);
        if (Objects.isNull(jsonElement) || jsonElement.isJsonNull()) {
            return null;
        }

        return Instant.ofEpochMilli(jsonElement.getAsLong());
    }
}
