package dev.alexhstone.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigInteger;

@Value
@Builder
public class FileHashResult {

    @EqualsAndHashCode.Exclude
    String absolutePath;

    // TODO add field for relative path to selected directory

    String fileName;

    BigInteger fileSizeInBytes;

    @EqualsAndHashCode.Exclude
    String fileSize;

    HashDetails hashDetails;
}
