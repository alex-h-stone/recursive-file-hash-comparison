package dev.alexhstone.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dev.alexhstone.model.queue.FileWorkItem;

import java.lang.reflect.Type;
import java.time.Instant;

public class FileWorkItemSerializerAndDeserializer implements JsonSerializer<FileWorkItem>, JsonDeserializer<FileWorkItem> {

    @Override
    public JsonElement serialize(FileWorkItem fileWorkItem,
                                 Type type,
                                 JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", fileWorkItem.getId());
        jsonObject.addProperty("absolutePathToFile", fileWorkItem.getAbsolutePath());
        jsonObject.addProperty("absolutePathToWorkingDirectory", fileWorkItem.getAbsolutePathToWorkingDirectory());
        jsonObject.addProperty("fileSizeInBytes", fileWorkItem.getSizeInBytes());
        jsonObject.addProperty("workItemCreationTime", fileWorkItem.getWorkItemCreationTime().toEpochMilli());

        return jsonObject;
    }

    @Override
    public FileWorkItem deserialize(JsonElement json,
                                    Type type,
                                    JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        FileWorkItem fileWorkItem = FileWorkItem.builder()
                .id(jsonObject.get("id").getAsString())
                .absolutePath(jsonObject.get("absolutePathToFile").getAsString())
                .absolutePathToWorkingDirectory(jsonObject.get("absolutePathToWorkingDirectory").getAsString())
                .sizeInBytes(jsonObject.get("fileSizeInBytes").getAsBigInteger())
                .workItemCreationTime(Instant.ofEpochSecond(jsonObject.get("workItemCreationTime").getAsLong())).build();

        return fileWorkItem;
    }
}
