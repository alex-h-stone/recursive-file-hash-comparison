package dev.alexhstone.producer;

import dev.alexhstone.model.queue.WorkItem;
import dev.alexhstone.validation.DirectoryValidator;
import dev.alexhstone.validation.FileValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;
import java.time.Instant;
import java.util.function.Function;

@Slf4j
public class FileToWorkItemMapper {

    private final FileValidator fileValidator = new FileValidator();
    private final DirectoryValidator directoryValidator = new DirectoryValidator();

    public WorkItem map(Path workingDirectory, File file) {
        Path validWorkingDirectory = directoryValidator.validateExists(workingDirectory);
        File fileExists = fileValidator.validateExists(file);

        WorkItem workItem = WorkItem.builder()
                .id(fileExists.getAbsolutePath())
                .name(fileExists.getName())
                .absolutePath(fileExists.getAbsolutePath())
                .absolutePathToWorkingDirectory(validWorkingDirectory.toFile().getAbsolutePath())
                .sizeInBytes(determineSizeInBytes(file))
                .workItemCreationTime(getInstantNow())
                .build();
        log.debug("Mapped the file [{}] to the workItem: [{}]", fileExists.getAbsolutePath(), workItem);
        return workItem;
    }

    public Function<File, WorkItem> asFunction(Path workingDirectory) {
        return new Function<>() {
            private final FileToWorkItemMapper mapper = new FileToWorkItemMapper();

            @Override
            public WorkItem apply(File file) {
                return mapper.map(workingDirectory, file);
            }
        };
    }

    Instant getInstantNow() {
        return Instant.now();
    }

    private BigInteger determineSizeInBytes(File file) {
        if (file.isDirectory()) {
            return FileUtils.sizeOfDirectoryAsBigInteger(file);
        }
        return FileUtils.sizeOfAsBigInteger(file);
    }
}
