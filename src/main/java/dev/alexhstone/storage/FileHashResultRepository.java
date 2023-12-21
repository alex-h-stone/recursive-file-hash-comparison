package dev.alexhstone.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.alexhstone.calculator.HashCalculator;
import dev.alexhstone.calculator.Location;
import dev.alexhstone.model.FileHashResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

@Slf4j
public class FileHashResultRepository {

    public static final String DATABASE_FILE_NAME = "databaseFileStore.dat";
    private final Gson gson;
    private final HashCalculator hashCalculator;
    private final MongoCollection<Document> collection;

    public FileHashResultRepository() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

        MongoDatabase database = mongoClient.getDatabase("yourDatabase");
        collection = database.getCollection("yourCollection");

        this.gson = new GsonBuilder().create();
        hashCalculator = new HashCalculator();
    }

    public void put(Location schema, FileHashResult fileHashResult) {
        String json = gson.toJson(fileHashResult);
        final String hashCode = toHash(fileHashResult);

        String id = fileHashResult.getId();
        Document existing = collection.find(new Document("_id", id)).first();
        if (existing == null || existing.isEmpty()) {
            collection.insertOne(new Document(id, json));
        }

        log.warn("Found hash collision as [{}] is already in the repository",
                fileHashResult);
    }

    //public boolean isAlreadyPresent()

    private String toHash(FileHashResult fileHashResult) {
        return hashCalculator.calculateHashFor(fileHashResult.getRelativePathToFile());
    }
}
