package dev.alexhstone.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigInteger;

@Value
@Builder
public class FileHashResult {

    String fileName;
    String absolutePath;
    BigInteger fileSizeInBytes;
    String fileSize;
    HashDetails hashDetails;
}
