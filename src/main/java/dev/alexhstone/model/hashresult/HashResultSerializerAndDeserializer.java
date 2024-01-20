package dev.alexhstone.model.hashresult;

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
        jsonObject.addProperty("name", hashResult.getName());
        jsonObject.addProperty("absolutePath", hashResult.getAbsolutePath());
        jsonObject.addProperty("absolutePathToWorkingDirectory", hashResult.getAbsolutePathToWorkingDirectory());
        jsonObject.addProperty("relativePath", hashResult.getRelativePath());
        jsonObject.addProperty("relativePathToFile", hashResult.getRelativePathToFile());
        jsonObject.addProperty("fileSystemType", hashResult.getFileSystemType().name());
        jsonObject.addProperty("sizeInBytes", hashResult.getSizeInBytes());
        jsonObject.addProperty("size", hashResult.getSize());
        jsonObject.addProperty("workItemCreationTime", hashResult.getWorkItemCreationTime().toEpochMilli());
        jsonObject.addProperty("creationTime", hashResult.getCreationTime().toEpochMilli());
        jsonObject.addProperty("hashingAlgorithmName", hashResult.getHashingAlgorithmName());
        jsonObject.addProperty("hashValue", hashResult.getHashValue());

        return jsonObject;
    }

    @Override
    public HashResult deserialize(JsonElement json,
                                  Type typeOfT,
                                  JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        HashResult hashResult = HashResult.builder()
                .name(jsonObject.get("name").getAsString())
                .absolutePath(jsonObject.get("absolutePath").getAsString())
                .absolutePathToWorkingDirectory(jsonObject.get("absolutePathToWorkingDirectory").getAsString())
                .relativePath(jsonObject.get("relativePath").getAsString())
                .relativePathToFile(jsonObject.get("relativePathToFile").getAsString())
                .fileSystemType(FileSystemType.valueOf(jsonObject.get("relativePathToFile").getAsString()))
                .sizeInBytes(jsonObject.get("sizeInBytes").getAsBigInteger())
                .size(jsonObject.get("size").getAsString())
                .workItemCreationTime(Instant.ofEpochMilli(jsonObject.get("workItemCreationTime").getAsLong()))
                .creationTime(Instant.ofEpochMilli(jsonObject.get("creationTime").getAsLong()))
                .hashingAlgorithmName(jsonObject.get("hashingAlgorithmName").getAsString())
                .hashValue(jsonObject.get("hashValue").getAsString())
                .build();

        return hashResult;
    }
}
