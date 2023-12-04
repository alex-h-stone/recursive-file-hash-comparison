package org.backup;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.backup.model.FileHashResult;
import org.backup.model.HashDetails;
import org.backup.util.HashCalculator;

import java.io.File;
import java.math.BigInteger;

@RequiredArgsConstructor
public class FileHashResultGenerator {

    private final HashCalculator hashCalculator;

    public FileHashResult process(File file) {
        HashDetails hashDetails = calculateHashDetails(file);

        BigInteger sizeOfFileInBytes = FileUtils.sizeOfAsBigInteger(file);
        String byteCountToDisplaySize = FileUtils.byteCountToDisplaySize(sizeOfFileInBytes);

        return FileHashResult.builder()
                .fileName(file.getName())
                .absolutePath(file.getAbsolutePath())
                .fileSizeInBytes(sizeOfFileInBytes)
                .fileSize(byteCountToDisplaySize)
                .hashDetails(hashDetails)
                .build();
    }

    private HashDetails calculateHashDetails(File file) {
        return HashDetails.builder()
                .hashingAlgorithmName(hashCalculator.getAlgorithmName())
                .hashValue(hashCalculator.calculateHashFor(file))
                .build();
    }
}
