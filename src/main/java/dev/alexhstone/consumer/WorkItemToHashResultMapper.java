package dev.alexhstone.consumer;

import dev.alexhstone.diskmetadata.MetaDataRetriever;
import dev.alexhstone.model.hashresult.FileSystemType;
import dev.alexhstone.model.hashresult.HashResult;
import dev.alexhstone.model.workitem.FileWorkItem;
import dev.alexhstone.util.Clock;
import dev.alexhstone.validation.FileValidator;
import dev.alexhstone.validation.PathValidator;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;

public class WorkItemToHashResultMapper {

    private final HashCalculator hashCalculator = new HashCalculator();
    private final PathValidator pathValidator = new PathValidator();
    private final MetaDataRetriever metaDataRetriever = new MetaDataRetriever();
    private final Clock clock;

    public WorkItemToHashResultMapper(Clock clock) {
        this.clock = clock;
    }

    public WorkItemToHashResultMapper() {
        this(new Clock());
    }

    public HashResult map(FileWorkItem fileWorkItem) {
        String absolutePathString = fileWorkItem.getAbsolutePath();
        Path pathToWorkItem = pathValidator.validateExists(absolutePathString);
        File file = new FileValidator().validateExists(pathToWorkItem.toFile());
        String hashValue = hashCalculator.calculateHashFor(file);

        BigInteger sizeOfFileInBytes = FileUtils.sizeOfAsBigInteger(file);
        String byteCountToDisplaySize = FileUtils.byteCountToDisplaySize(sizeOfFileInBytes);

        String absolutePathToWorkingDirectoryString = fileWorkItem.getAbsolutePathToWorkingDirectory();
        Path pathToWorkingDirectory = pathValidator.validateExists(absolutePathToWorkingDirectoryString);
        Path relativePath = pathToWorkingDirectory.relativize(pathToWorkItem.getParent());

        return HashResult.builder()
                .name(fileWorkItem.getName())
                .absolutePath(absolutePathString)
                .absolutePathToWorkingDirectory(absolutePathToWorkingDirectoryString)
                .partitionUuid(metaDataRetriever.retrievePartitionUuid(absolutePathString))
                .relativePath(relativePath.toString())
                .relativePathToFile(pathToWorkingDirectory.relativize(pathToWorkItem).toString())
                .fileSystemType(FileSystemType.valueOfFile(file))
                .sizeInBytes(sizeOfFileInBytes)
                .size(byteCountToDisplaySize)
                .workItemCreationTime(fileWorkItem.getWorkItemCreationTime())
                .creationTime(clock.getInstantNow())
                .hashingAlgorithmName(hashCalculator.getAlgorithmName())
                .hashValue(hashValue)
                .build();
    }
}
