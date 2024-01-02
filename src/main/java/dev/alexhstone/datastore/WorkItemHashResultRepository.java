package dev.alexhstone.datastore;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import dev.alexhstone.model.datastore.HashResult;
import dev.alexhstone.model.datastore.HashResultDeserializer;
import dev.alexhstone.model.datastore.HashResultSerializer;
import dev.alexhstone.model.queue.WorkItem;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class WorkItemHashResultRepository {

    private final HashResultDeserializer deserializer = new HashResultDeserializer();
    private final HashResultSerializer serializer = new HashResultSerializer();

    private final MongoCollection<Document> collection;

    public static void main(String[] args) {
        WorkItemHashResultRepository repository = new WorkItemHashResultRepository();
        Optional<HashResult> hashResult = repository.retrieveHashResultByAbsolutePath("659458a23cda4d36f5fcfe31");
        int h = 5;
        // TODO
    }

    public WorkItemHashResultRepository() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

        MongoDatabase database = mongoClient.getDatabase("FileSystemHashCache");
        collection = database.getCollection("hashResults");

        collection.createIndex(Indexes.hashed("id"));
        collection.createIndex(Indexes.hashed("hashValue"));
    }

    public void put(HashResult hashResult) {
        Optional<HashResult> existingFileHashResult = retrieveHashResultByAbsolutePath(hashResult.getAbsolutePath());

        if (existingFileHashResult.isEmpty()) {
            String json = serializer.toJson(hashResult);
            Document document = new Document("absolutePath", hashResult.getAbsolutePath());
            document.append("hashResultJSON", json)
                    .append("hashValue", hashResult.getHashValue());
            collection.insertOne(document);
        }

        log.warn("Found id collision as [{}] is already in the repository so not inserting",
                hashResult);
    }

    public boolean hasAlreadyBeenCalculated(WorkItem workItem) {
        Optional<HashResult> existingHashResultOptional = retrieveHashResultByAbsolutePath(workItem.getId());
        if (existingHashResultOptional.isEmpty()) {
            return false;
        }

        HashResult existingHashResult = existingHashResultOptional.get();

        Instant itemLastModifiedTime = workItem.getItemLastModifiedTime();
        return workItem.getSizeInBytes().equals(existingHashResult.getSizeInBytes()) &&
                itemLastModifiedTime != null &&
                itemLastModifiedTime.isBefore(existingHashResult.getCreationTime());
    }

    private Optional<HashResult> retrieveHashResultByAbsolutePath(String absolutePath) {
        FindIterable<Document> matchingDocuments = collection.find(Filters.eq("absolutePath", absolutePath));
        List<? super Document> bsonDocuments = new ArrayList<>();
        matchingDocuments.into(bsonDocuments);

        Document existing = matchingDocuments.first();
        if (existing == null || existing.isEmpty()) {
            return Optional.empty();
        }
        String json = existing.toJson();
        HashResult hashResult = deserializer.fromJson(json);
        return Optional.of(hashResult);
    }
}
