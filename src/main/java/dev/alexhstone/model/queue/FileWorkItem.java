package dev.alexhstone.model.queue;

import lombok.Builder;
import lombok.Value;

import java.math.BigInteger;
import java.time.Instant;

@Value
@Builder
public class FileWorkItem {

    String id;

    String absolutePathToFile;

    String absolutePathToWorkingDirectory;

    BigInteger fileSizeInBytes;

    Instant creationTime;
}
