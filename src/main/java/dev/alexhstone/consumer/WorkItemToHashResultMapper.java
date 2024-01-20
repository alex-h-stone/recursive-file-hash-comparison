package dev.alexhstone.consumer;

import dev.alexhstone.model.hashresult.FileSystemType;
import dev.alexhstone.model.hashresult.HashResult;
import dev.alexhstone.model.workitem.WorkItem;
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
        String absolutePathString = workItem.getAbsolutePath();
        Path pathToWorkItem = pathValidator.validateExists(absolutePathString);
        File file = new FileValidator().validateExists(pathToWorkItem.toFile());
        String hashValue = hashCalculator.calculateHashFor(file);

        BigInteger sizeOfFileInBytes = FileUtils.sizeOfAsBigInteger(file);
        String byteCountToDisplaySize = FileUtils.byteCountToDisplaySize(sizeOfFileInBytes);

        String absolutePathToWorkingDirectoryString = workItem.getAbsolutePathToWorkingDirectory();
        Path pathToWorkingDirectory = pathValidator.validateExists(absolutePathToWorkingDirectoryString);
        Path relativePath = pathToWorkingDirectory.relativize(pathToWorkItem.getParent());

        return HashResult.builder()
                .name(workItem.getName())
                .absolutePath(absolutePathString)
                .absolutePathToWorkingDirectory(absolutePathToWorkingDirectoryString)
                .relativePath(relativePath.toString())
                .relativePathToFile(pathToWorkingDirectory.relativize(pathToWorkItem).toString())
                .fileSystemType(FileSystemType.valueOfFile(file))
                .sizeInBytes(sizeOfFileInBytes)
                .size(byteCountToDisplaySize)
                .workItemCreationTime(workItem.getWorkItemCreationTime())
                .creationTime(clock.getInstantNow())
                .hashingAlgorithmName(hashCalculator.getAlgorithmName())
                .hashValue(hashValue)
                .build();
    }
}
