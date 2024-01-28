package dev.alexhstone.datastore;

import dev.alexhstone.model.hashresult.HashResult;
import dev.alexhstone.model.hashresult.HashResultDeserializer;
import dev.alexhstone.model.hashresult.HashResultSerializer;
import dev.alexhstone.model.workitem.WorkItem;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class HashResultPersistenceService {

    private final HashResultDeserializer deserializer = new HashResultDeserializer();
    private final HashResultSerializer serializer = new HashResultSerializer();

    private final HashResultRepository hashResultRepository;

    public void put(HashResult hashResult) {
        Optional<HashResult> existingFileHashResult = retrieveHashResultByAbsolutePath(hashResult.getAbsolutePath());

        if (existingFileHashResult.isEmpty()) {
            String json = serializer.toJson(hashResult);
            HashResultDocument document = HashResultDocument.builder()
                    .absolutePath(hashResult.getAbsolutePath())
                    .relativePathToFile(hashResult.getRelativePathToFile())
                    .hashValue(hashResult.getHashValue())
                    .partitionUuid(hashResult.getPartitionUuid())
                    .hashResultJSON(json)
                    .build();

            hashResultRepository.insert(document);
        } else {
            log.warn("Found id collision as [{}] is already in the repository so not inserting",
                    hashResult);
        }
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
        Optional<HashResultDocument> optionalDocument = hashResultRepository.findById(absolutePath);


        if (optionalDocument.isEmpty()) {
            return Optional.empty();
        }

        HashResultDocument document = optionalDocument.get();

        return Optional.of(deserialise(document));
    }

    private HashResult deserialise(HashResultDocument document) {
        String hashResultJson = document.getHashResultJSON();
        return deserializer.fromJson(hashResultJson);
    }

    public void applyToAll(Consumer<HashResult> hashResultConsumer) {
        Page<HashResultDocument> page = hashResultRepository
                .findAll(Pageable.ofSize(1_000));

        do {
            List<HashResultDocument> documents = page.getContent();
            documents.parallelStream()
                    .map(this::deserialise)
                    .forEach(hashResultConsumer);
        }
        while (page.hasNext());
    }

    public List<HashResult> getByHashValue(String hashValue) {
        List<HashResultDocument> documents = hashResultRepository.findByHashValue(hashValue);

        return documents.parallelStream()
                .map(this::deserialise)
                .collect(Collectors.toList());
    }
}
