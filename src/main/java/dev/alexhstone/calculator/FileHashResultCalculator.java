package dev.alexhstone.calculator;

import dev.alexhstone.model.HashDetails;
import dev.alexhstone.model.datastore.WorkItemHashResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;
import java.time.Instant;

@RequiredArgsConstructor
public class FileHashResultCalculator {

    private final HashDetailsCalculator hashDetailsCalculator;

    public WorkItemHashResult process(Path workingDirectory, File file) {
        HashDetails hashDetails = hashDetailsCalculator.calculateHashDetails(file);

        BigInteger sizeOfFileInBytes = FileUtils.sizeOfAsBigInteger(file);
        String byteCountToDisplaySize = FileUtils.byteCountToDisplaySize(sizeOfFileInBytes);

        Path relativePath = workingDirectory.relativize(file.toPath().getParent());

        String absolutePathToFile = file.getAbsolutePath();
        return WorkItemHashResult.builder()
                .id(absolutePathToFile)
                .name(file.getName())
                .relativePath(relativePath.toString())
                .absolutePath(absolutePathToFile)
                .absolutePathToWorkingDirectory(workingDirectory.toFile().getAbsolutePath())
                .sizeInBytes(sizeOfFileInBytes)
                .size(byteCountToDisplaySize)
                .creationTime(Instant.now())
                .hashDetails(hashDetails)
                .build();
    }
}
