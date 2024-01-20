package dev.alexhstone.datastore;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import dev.alexhstone.model.hashresult.HashResult;
import dev.alexhstone.model.hashresult.HashResultDeserializer;
import dev.alexhstone.model.hashresult.HashResultSerializer;
import dev.alexhstone.model.workitem.WorkItem;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class HashResultRepository {

    private final HashResultDeserializer deserializer = new HashResultDeserializer();
    private final HashResultSerializer serializer = new HashResultSerializer();

    private final MongoCollection<Document> collection;

    public static void main(String[] args) {
        HashResultRepository repository = new HashResultRepository();
        Optional<HashResult> hashResult = repository.retrieveHashResultByAbsolutePath("F:\\Data\\Audio\\A\\AC DC\\AC DC - Let There Be Rock\\05 AC DC - Problem Child.mp3");
    }

    public HashResultRepository() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

        MongoDatabase database = mongoClient.getDatabase("FileSystemHashCache");
        collection = database.getCollection("hashResults");

        collection.createIndex(Indexes.hashed("absolutePath"));
        collection.createIndex(Indexes.hashed("relativePathToFile"));
        collection.createIndex(Indexes.hashed("hashValue"));
    }

    public void put(HashResult hashResult) {
        Optional<HashResult> existingFileHashResult = retrieveHashResultByAbsolutePath(hashResult.getAbsolutePath());

        if (existingFileHashResult.isEmpty()) {
            String json = serializer.toJson(hashResult);
            Document document = new Document("hashResultJSON", json);
            document.append("absolutePath", hashResult.getAbsolutePath())
                    .append("relativePathToFile", hashResult.getRelativePathToFile())
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
        List<Document> matchingDocuments = new ArrayList<>();
        collection.find(Filters.eq("absolutePath", absolutePath))
                .into(matchingDocuments);

        int numberOfMatchingDocuments = matchingDocuments.size();
        if (numberOfMatchingDocuments > 1) {
            log.warn("Found multiple ({}) Documents with the absolutePath [{}], the absolutePath should be a unique Id",
                    numberOfMatchingDocuments, absolutePath);
        }

        if (numberOfMatchingDocuments == 0) {
            return Optional.empty();
        }

        Document matchingDocument = matchingDocuments.get(0);

        return Optional.of(deserialise(matchingDocument));
    }

    private HashResult deserialise(Document matchingDocument) {
        String hashResultJson = matchingDocument.getString("hashResultJSON");
        return deserializer.fromJson(hashResultJson);
    }

    public void applyToAll(Consumer<HashResult> hashResultConsumer) {
        collection.find()
                .allowDiskUse(true)
                .batchSize(100)
                .spliterator()
                .forEachRemaining(document -> hashResultConsumer.accept(deserialise(document)));
    }

    public List<HashResult> getByHashValue(String hashValue) {
        List<Document> matchingDocuments = new ArrayList<>();
        collection.find(Filters.eq("hashValue", hashValue))
                .into(matchingDocuments);

        return matchingDocuments.stream()
                .map(this::deserialise)
                .collect(Collectors.toList());
    }
}
