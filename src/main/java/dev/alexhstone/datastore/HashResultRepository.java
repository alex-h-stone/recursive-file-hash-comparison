package dev.alexhstone.datastore;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface HashResultRepository extends MongoRepository<HashResultDocument, String> {

    List<HashResultDocument> findByHashValue(String hashValue);

    @Query("{ 'hashValue': ?0, 'partitionUuid': ?1 }")
    List<HashResultDocument> findByHashValueAndPartitionUuid(String hashValue, String partitionUuid);
}
