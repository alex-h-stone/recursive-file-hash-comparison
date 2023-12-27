package dev.alexhstone.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigInteger;
import java.time.Instant;

@Value
@Builder
public class FileHashResult {

    String id;

    String fileName;

    String relativePathToFile;

    String absolutePathToFile;

    String absolutePathToWorkingDirectory;

    BigInteger fileSizeInBytes;

    String fileSize;

    Instant creationTime;

    HashDetails hashDetails;
}
