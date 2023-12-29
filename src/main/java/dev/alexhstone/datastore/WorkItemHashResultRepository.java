package dev.alexhstone.datastore;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.alexhstone.model.datastore.HashResult;
import dev.alexhstone.model.datastore.HashResultDeserializer;
import dev.alexhstone.model.datastore.HashResultSerializer;
import dev.alexhstone.model.queue.WorkItem;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.util.Optional;

@Slf4j
public class WorkItemHashResultRepository {

    private final HashResultDeserializer deserializer = new HashResultDeserializer();
    private final HashResultSerializer serializer = new HashResultSerializer();

    private final MongoCollection<Document> collection;

    public static void main(String[] args){
        WorkItemHashResultRepository repository = new WorkItemHashResultRepository();
    }

    public WorkItemHashResultRepository() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

        MongoDatabase database = mongoClient.getDatabase("myMongoDatabase");
        collection = database.getCollection("fileHashResultsByAbsolutePath");

    }

    public void put(HashResult hashResult) {
        String id = hashResult.getId();
        Optional<HashResult> existingFileHashResult = retrieveHashResultById(id);

        if (existingFileHashResult.isEmpty()) {
            String json = serializer.toJson(hashResult);
            collection.insertOne(new Document(id, json));
        }

        log.warn("Found id collision as [{}] is already in the repository",
                hashResult);
    }

    public boolean hasAlreadyBeenCalculated(WorkItem workItem) {
        Optional<HashResult> existingHashResultOptional = retrieveHashResultById(workItem.getId());
        if (existingHashResultOptional.isEmpty()) {
            return false;
        }

        HashResult existingHashResult = existingHashResultOptional.get();

        return workItem.getSizeInBytes().equals(existingHashResult.getSizeInBytes());
    }

    private Optional<HashResult> retrieveHashResultById(String id) {
        Document existing = collection.find(new Document("_id", id)).first();
        if (existing == null || existing.isEmpty()) {
            return Optional.empty();
        }
        String json = existing.toJson();
        HashResult hashResult = deserializer.fromJson(json);
        return Optional.of(hashResult);
    }
}
