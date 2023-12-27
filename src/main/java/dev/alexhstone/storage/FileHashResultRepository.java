package dev.alexhstone.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.alexhstone.calculator.HashCalculator;
import dev.alexhstone.model.FileHashResult;
import dev.alexhstone.model.queue.FileWorkItem;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.util.Optional;

@Slf4j
public class FileHashResultRepository {

    private static final String DATABASE_FILE_NAME = "databaseFileStore.dat";
    private final Gson gson;
    private final HashCalculator hashCalculator;
    private final MongoCollection<Document> collection;

    public FileHashResultRepository() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

        MongoDatabase database = mongoClient.getDatabase("myMongoDatabase");
        collection = database.getCollection("fileHashResultsByAbsolutePath");

        this.gson = new GsonBuilder().create();
        hashCalculator = new HashCalculator();
    }

    public void put(FileHashResult fileHashResult) {
        String id = fileHashResult.getId();
        Optional<FileHashResult> existingFileHashResult = retrieveFileHashResultById(id);

        if (existingFileHashResult.isEmpty()) {
            String json = gson.toJson(fileHashResult);
            collection.insertOne(new Document(id, json));
        }

        log.warn("Found id collision as [{}] is already in the repository",
                fileHashResult);
    }

    public boolean isAlreadyPresent(FileWorkItem fileWorkItem) {
        Optional<FileHashResult> existingFileHashResultOptional = retrieveFileHashResultById(fileWorkItem.getId());
        if (existingFileHashResultOptional.isEmpty()) {
            return false;
        }

        FileHashResult hashResult = existingFileHashResultOptional.get();

        return fileWorkItem.getSizeInBytes().equals(hashResult.getFileSizeInBytes());
    }

    private Optional<FileHashResult> retrieveFileHashResultById(String id) {
        Document existing = collection.find(new Document("_id", id)).first();
        if (existing == null || existing.isEmpty()) {
            return Optional.empty();
        }
        String json = existing.toJson();
        FileHashResult fileHashResult = gson.fromJson(json, FileHashResult.class);
        return Optional.of(fileHashResult);
    }
}
