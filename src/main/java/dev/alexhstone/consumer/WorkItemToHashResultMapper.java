package dev.alexhstone.consumer;

import dev.alexhstone.model.datastore.HashResult;
import dev.alexhstone.model.queue.WorkItem;
import dev.alexhstone.util.Clock;
import dev.alexhstone.validation.FileValidator;
import dev.alexhstone.validation.PathValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;

@RequiredArgsConstructor
public class WorkItemToHashResultMapper {

    private final HashCalculator hashCalculator = new HashCalculator();
    private final PathValidator pathValidator = new PathValidator();
    private final Clock clock;

    public HashResult map(WorkItem workItem) {
        String absolutePath = workItem.getAbsolutePath();
        Path validPath = pathValidator.validateExists(absolutePath);
        File file = new FileValidator().validateExists(validPath.toFile());
        String hashValue = hashCalculator.calculateHashFor(file);

        BigInteger sizeOfFileInBytes = FileUtils.sizeOfAsBigInteger(file);
        String byteCountToDisplaySize = FileUtils.byteCountToDisplaySize(sizeOfFileInBytes);

        Path relativePath = pathValidator.validateExists(workItem.getAbsolutePathToWorkingDirectory())
                .relativize(validPath.getParent());

        return HashResult.builder()
                .id(absolutePath)
                .name(workItem.getName())
                .absolutePath(absolutePath)
                .absolutePathToWorkingDirectory(workItem.getAbsolutePathToWorkingDirectory())
                .relativePath(relativePath.toString())
                .sizeInBytes(sizeOfFileInBytes)
                .size(byteCountToDisplaySize)
                .workItemCreationTime(workItem.getWorkItemCreationTime())
                .creationTime(clock.getInstantNow())
                .hashingAlgorithmName(hashCalculator.getAlgorithmName())
                .hashValue(hashValue)
                .build();
    }
}
