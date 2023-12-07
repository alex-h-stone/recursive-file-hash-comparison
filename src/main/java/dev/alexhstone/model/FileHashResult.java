package dev.alexhstone.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigInteger;

@Value
@Builder
public class FileHashResult {

    String fileName;

    String relativePathToFile;

    @EqualsAndHashCode.Exclude
    String absolutePathToFile;

    String absolutePathToWorkingDirectory;

    BigInteger fileSizeInBytes;

    @EqualsAndHashCode.Exclude
    String fileSize;

    HashDetails hashDetails;
}
