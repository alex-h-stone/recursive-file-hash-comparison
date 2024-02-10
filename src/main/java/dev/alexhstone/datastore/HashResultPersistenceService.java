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

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class HashResultPersistenceService {

    private static final int MAXIMUM_PAGE_SIZE = 1_000;
    private final HashResultDeserializer deserializer = new HashResultDeserializer();
    private final HashResultSerializer serializer = new HashResultSerializer();

    private final HashResultRepository hashResultRepository;

    public void store(HashResult hashResult) {
        Optional<HashResult> existingFileHashResult = getByAbsolutePath(hashResult.getAbsolutePath());

        if (existingFileHashResult.isEmpty()) {
            String json = serializer.toJson(hashResult);
            HashResultDocument document = HashResultDocument.builder()
                    .absolutePath(hashResult.getAbsolutePath())
                    .relativePathToFile(hashResult.getRelativePathToFile())
                    .hashValue(hashResult.getHashValue())
                    .partitionUuid(hashResult.getPartitionUuid())
                    .sourceFileSizeInBytes(hashResult.getSizeInBytes())
                    .hashResultJSON(json)
                    .build();

            hashResultRepository.insert(document);
        } else {
            log.warn("Found id collision as [{}] is already in the repository so not inserting",
                    hashResult);
        }
    }

    public boolean hasAlreadyBeenCalculated(WorkItem workItem) {
        Optional<HashResult> existingHashResultOptional = getByAbsolutePath(workItem.getId());
        if (existingHashResultOptional.isEmpty()) {
            return false;
        }

        HashResult existingHashResult = existingHashResultOptional.get();

        boolean isWorkItemAndExistingResultSameFileSize = workItem.getSizeInBytes().equals(existingHashResult.getSizeInBytes());
        if (isWorkItemAndExistingResultSameFileSize) {
            log.info("Not calculating hash result as it has already been done for [{}]", workItem.getAbsolutePath());
        }
        return isWorkItemAndExistingResultSameFileSize;
    }

    private Optional<HashResult> getByAbsolutePath(String absolutePath) {
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
        Page<HashResultDocument> currentPage = hashResultRepository
                .findAll(Pageable.ofSize(MAXIMUM_PAGE_SIZE));
        log.info("About to apply hashResultConsumer {} pages where each page has a max size of {}",
                currentPage.getTotalPages(), MAXIMUM_PAGE_SIZE);

        do {
            log.info("Applying hashResultConsumer to page {} of {}", currentPage.getNumber(), currentPage.getTotalPages());
            List<HashResultDocument> documents = currentPage.getContent();
            documents.parallelStream()
                    .map(this::deserialise)
                    .forEach(hashResultConsumer);
            if (currentPage.hasNext()) {
                Page<HashResultDocument> nextPage = hashResultRepository.findAll(currentPage.nextPageable());
                currentPage = nextPage;
            } else {
                break;
            }
        } while (true);
    }

    public List<HashResult> getByHashValue(String hashValue) {
        List<HashResultDocument> documents = hashResultRepository.findByHashValue(hashValue);

        return documents.parallelStream()
                .map(this::deserialise)
                .collect(Collectors.toList());
    }

    public List<HashResult> getByHashValueAndPartitionUuid(String hashValue, String partitionUuid) {
        List<HashResultDocument> documents = hashResultRepository.findByHashValueAndPartitionUuid(hashValue, partitionUuid);

        return documents.parallelStream()
                .map(this::deserialise)
                .collect(Collectors.toList());
    }

    public boolean doesNotContainUpToDateHashFor(String absolutePath,
                                                 BigInteger fileSizeInBytes) {
        if (Objects.isNull(fileSizeInBytes)) {
            return false;
        }

        Optional<HashResultDocument> optionalHashResult = hashResultRepository.findById(absolutePath);
        if (optionalHashResult.isEmpty()) {
            return true;
        }
        BigInteger hashResultFileSizeInBytes = optionalHashResult.get().getSourceFileSizeInBytes();

        return !fileSizeInBytes.equals(hashResultFileSizeInBytes);
    }
}
