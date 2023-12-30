package dev.alexhstone.producer;

import dev.alexhstone.model.queue.WorkItem;
import dev.alexhstone.util.Clock;
import dev.alexhstone.validation.DirectoryValidator;
import dev.alexhstone.validation.FileValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.function.Function;

@Slf4j
public class FileToWorkItemMapper {

    private final FileValidator fileValidator = new FileValidator();
    private final DirectoryValidator directoryValidator = new DirectoryValidator();
    private final Clock clock;

    public FileToWorkItemMapper(Clock clock) {
        this.clock = clock;
    }

    public WorkItem map(Path workingDirectory, File file) {
        Path validWorkingDirectory = directoryValidator.validateExists(workingDirectory);
        File fileExists = fileValidator.validateExists(file);

        WorkItem workItem = WorkItem.builder()
                .id(fileExists.getAbsolutePath())
                .name(fileExists.getName())
                .absolutePath(fileExists.getAbsolutePath())
                .absolutePathToWorkingDirectory(validWorkingDirectory.toFile().getAbsolutePath())
                .sizeInBytes(determineSizeInBytes(file))
                .workItemCreationTime(clock.getInstantNow())
                .build();
        log.debug("Mapped the file [{}] to the workItem: [{}]", fileExists.getAbsolutePath(), workItem);
        return workItem;
    }

    public Function<File, WorkItem> asFunction(Path workingDirectory) {
        return new Function<>() {
            private final FileToWorkItemMapper mapper = new FileToWorkItemMapper(clock);

            @Override
            public WorkItem apply(File file) {
                return mapper.map(workingDirectory, file);
            }
        };
    }

    private BigInteger determineSizeInBytes(File file) {
        if (file.isDirectory()) {
            return FileUtils.sizeOfDirectoryAsBigInteger(file);
        }
        return FileUtils.sizeOfAsBigInteger(file);
    }
}
