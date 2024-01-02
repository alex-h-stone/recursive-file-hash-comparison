package dev.alexhstone.model.datastore;

import lombok.Builder;
import lombok.Value;

import java.math.BigInteger;
import java.time.Instant;

@Value
@Builder
public class HashResult {

    String name;
    String absolutePath;
    String absolutePathToWorkingDirectory;
    String relativePath;
    String relativePathToFile; // Note: This will be identical to relativePath for directories
    BigInteger sizeInBytes;
    String size;
    Instant workItemCreationTime;
    Instant creationTime;
    String hashingAlgorithmName;
    String hashValue;
}
