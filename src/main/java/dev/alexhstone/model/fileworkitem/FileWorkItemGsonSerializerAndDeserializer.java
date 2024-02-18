package dev.alexhstone.model.fileworkitem;

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

public class FileWorkItemGsonSerializerAndDeserializer implements JsonSerializer<FileWorkItem>, JsonDeserializer<FileWorkItem> {

    @Override
    public JsonElement serialize(FileWorkItem fileWorkItem,
                                 Type type,
                                 JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", fileWorkItem.getName());
        jsonObject.addProperty("absolutePath", fileWorkItem.getAbsolutePath());
        jsonObject.addProperty("absolutePathToWorkingDirectory", fileWorkItem.getAbsolutePathToWorkingDirectory());
        jsonObject.addProperty("sizeInBytes", fileWorkItem.getSizeInBytes());
        jsonObject.addProperty("itemLastModifiedTime", toNullSafeMilliseconds(fileWorkItem.getItemLastModifiedTime()));
        jsonObject.addProperty("workItemCreationTime", toNullSafeMilliseconds(fileWorkItem.getWorkItemCreationTime()));

        return jsonObject;
    }

    @Override
    public FileWorkItem deserialize(JsonElement json,
                                    Type type,
                                    JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        FileWorkItem fileWorkItem = FileWorkItem.builder()
                .name(jsonObject.get("name").getAsString())
                .absolutePath(jsonObject.get("absolutePath").getAsString())
                .absolutePathToWorkingDirectory(jsonObject.get("absolutePathToWorkingDirectory").getAsString())
                .sizeInBytes(jsonObject.get("sizeInBytes").getAsBigInteger())
                .itemLastModifiedTime(toNullSafeInstant(jsonObject, "itemLastModifiedTime"))
                .workItemCreationTime(toNullSafeInstant(jsonObject, "workItemCreationTime"))
                .build();

        return fileWorkItem;
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
