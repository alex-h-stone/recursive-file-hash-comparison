package dev.alexhstone;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import dev.alexhstone.model.FileHashResult;
import dev.alexhstone.model.HashDetails;
import dev.alexhstone.util.HashCalculator;

import java.io.File;
import java.math.BigInteger;

@RequiredArgsConstructor
public class FileHashResultCalculator {

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
