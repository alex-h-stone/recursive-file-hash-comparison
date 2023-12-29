package dev.alexhstone.model.datastore;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.Instant;

public class HashResultSerializerAndDeserializer implements JsonSerializer<HashResult>, JsonDeserializer<HashResult> {

    @Override
    public JsonElement serialize(HashResult hashResult,
                                 Type typeOfSrc,
                                 JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", hashResult.getId());
        jsonObject.addProperty("name", hashResult.getName());
        jsonObject.addProperty("absolutePath", hashResult.getAbsolutePath());
        jsonObject.addProperty("absolutePathToWorkingDirectory", hashResult.getAbsolutePathToWorkingDirectory());
        jsonObject.addProperty("relativePath", hashResult.getRelativePath());
        jsonObject.addProperty("sizeInBytes", hashResult.getSizeInBytes());
        jsonObject.addProperty("size", hashResult.getSize());
        jsonObject.addProperty("workItemCreationTime", hashResult.getWorkItemCreationTime().toEpochMilli());
        jsonObject.addProperty("creationTime", hashResult.getCreationTime().toEpochMilli());
        jsonObject.addProperty("hashValue", hashResult.getHashValue());
        jsonObject.addProperty("hashingAlgorithmName", hashResult.getHashingAlgorithmName());

        return jsonObject;
    }

    @Override
    public HashResult deserialize(JsonElement json,
                                  Type typeOfT,
                                  JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        HashResult hashResult = HashResult.builder()
                .id(jsonObject.get("id").getAsString())
                .name(jsonObject.get("name").getAsString())
                .absolutePath(jsonObject.get("absolutePath").getAsString())
                .absolutePathToWorkingDirectory(jsonObject.get("absolutePathToWorkingDirectory").getAsString())
                .relativePath(jsonObject.get("relativePath").getAsString())
                .sizeInBytes(jsonObject.get("sizeInBytes").getAsBigInteger())
                .size(jsonObject.get("size").getAsString())
                .workItemCreationTime(Instant.ofEpochMilli(jsonObject.get("workItemCreationTime").getAsLong()))
                .creationTime(Instant.ofEpochMilli(jsonObject.get("creationTime").getAsLong()))                .hashValue(jsonObject.get("hashValue").getAsString())
                .hashingAlgorithmName(jsonObject.get("hashingAlgorithmName").getAsString())
                .build();

        return hashResult;
    }
}
