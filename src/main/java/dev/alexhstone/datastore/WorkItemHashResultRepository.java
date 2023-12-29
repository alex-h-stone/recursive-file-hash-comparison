package dev.alexhstone.datastore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.alexhstone.calculator.HashCalculator;
import dev.alexhstone.model.datastore.WorkItemHashResult;
import dev.alexhstone.model.queue.WorkItem;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.util.Optional;

@Slf4j
public class WorkItemHashResultRepository {

    private static final String DATABASE_FILE_NAME = "databaseFileStore.dat";
    private final Gson gson;
    private final HashCalculator hashCalculator;
    private final MongoCollection<Document> collection;

    public WorkItemHashResultRepository() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

        MongoDatabase database = mongoClient.getDatabase("myMongoDatabase");
        collection = database.getCollection("fileHashResultsByAbsolutePath");

        this.gson = new GsonBuilder().create();
        hashCalculator = new HashCalculator();
    }

    public void put(WorkItemHashResult workItemHashResult) {
        String id = workItemHashResult.getId();
        Optional<WorkItemHashResult> existingFileHashResult = retrieveHashResultById(id);

        if (existingFileHashResult.isEmpty()) {
            String json = gson.toJson(workItemHashResult);
            collection.insertOne(new Document(id, json));
        }

        log.warn("Found id collision as [{}] is already in the repository",
                workItemHashResult);
    }

    public boolean hasAlreadyBeenCalculated(WorkItem workItem) {
        Optional<WorkItemHashResult> existingHashResultOptional = retrieveHashResultById(workItem.getId());
        if (existingHashResultOptional.isEmpty()) {
            return false;
        }

        WorkItemHashResult existingHashResult = existingHashResultOptional.get();

        return workItem.getWorkItemCreationTime().isBefore(existingHashResult.getCreationTime());
    }

    private Optional<WorkItemHashResult> retrieveHashResultById(String id) {
        Document existing = collection.find(new Document("_id", id)).first();
        if (existing == null || existing.isEmpty()) {
            return Optional.empty();
        }
        String json = existing.toJson();
        WorkItemHashResult workItemHashResult = gson.fromJson(json, WorkItemHashResult.class);
        return Optional.of(workItemHashResult);
    }
}
