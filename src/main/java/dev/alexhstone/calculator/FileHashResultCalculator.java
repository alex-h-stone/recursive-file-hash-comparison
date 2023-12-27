package dev.alexhstone.calculator;

import dev.alexhstone.model.FileHashResult;
import dev.alexhstone.model.HashDetails;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;
import java.time.Instant;

@RequiredArgsConstructor
public class FileHashResultCalculator {

    private final HashDetailsCalculator hashDetailsCalculator;

    public FileHashResult process(Path workingDirectory, File file) {
        HashDetails hashDetails = hashDetailsCalculator.calculateHashDetails(file);

        BigInteger sizeOfFileInBytes = FileUtils.sizeOfAsBigInteger(file);
        String byteCountToDisplaySize = FileUtils.byteCountToDisplaySize(sizeOfFileInBytes);

        Path relativePath = workingDirectory.relativize(file.toPath().getParent());

        String absolutePathToFile = file.getAbsolutePath();
        return FileHashResult.builder()
                .id(absolutePathToFile)
                .fileName(file.getName())
                .relativePathToFile(relativePath.toString())
                .absolutePathToFile(absolutePathToFile)
                .absolutePathToWorkingDirectory(workingDirectory.toFile().getAbsolutePath())
                .fileSizeInBytes(sizeOfFileInBytes)
                .fileSize(byteCountToDisplaySize)
                .creationTime(Instant.now())
                .hashDetails(hashDetails)
                .build();
    }
}
