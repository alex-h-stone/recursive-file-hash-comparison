package dev.alexhstone.datastore;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HashResultRepository extends MongoRepository<HashResultDocument, String> {

    List<HashResultDocument> findByHashValue(String hashValue);

    List<HashResultDocument> findByHashValueAndPartitionUuid(String hashValue, String partitionUuid);
}
