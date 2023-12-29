package dev.alexhstone.model.datastore;

import lombok.Builder;
import lombok.Value;

import java.math.BigInteger;
import java.time.Instant;

@Value
@Builder
public class HashResult {

    String id;
    String name;
    String absolutePath;
    String absolutePathToWorkingDirectory;
    String relativePath;
    BigInteger sizeInBytes;
    String size;
    Instant workItemCreationTime;
    Instant creationTime;
    String hashValue;
    String hashingAlgorithmName;
}
