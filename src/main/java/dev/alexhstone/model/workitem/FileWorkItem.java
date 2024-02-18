package dev.alexhstone.model.workitem;

import lombok.Builder;
import lombok.Value;

import java.math.BigInteger;
import java.time.Instant;

@Value
@Builder
public class FileWorkItem {
    String name;
    String absolutePath;
    String absolutePathToWorkingDirectory;
    BigInteger sizeInBytes;
    Instant itemLastModifiedTime;
    Instant workItemCreationTime;

    public String getId() {
        return absolutePath;
    }
}
