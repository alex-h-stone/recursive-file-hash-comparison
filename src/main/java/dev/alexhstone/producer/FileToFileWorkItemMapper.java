package dev.alexhstone.producer;

import dev.alexhstone.model.queue.FileWorkItem;
import dev.alexhstone.util.DirectoryValidator;
import dev.alexhstone.util.FileValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.util.function.Function;

@Slf4j
public class FileToFileWorkItemMapper {

    private final FileValidator fileValidator = new FileValidator();
    private final DirectoryValidator directoryValidator = new DirectoryValidator();

    public FileWorkItem map(Path workingDirectory, File file) {
        Path validWorkingDirectory = directoryValidator.validateExists(workingDirectory);
        File fileExists = fileValidator.validateExists(file);

        FileWorkItem fileWorkItem = FileWorkItem.builder()
                .id(fileExists.getAbsolutePath())
                .absolutePathToFile(fileExists.getAbsolutePath())
                .absolutePathToWorkingDirectory(validWorkingDirectory.toFile().getAbsolutePath())
                .fileSizeInBytes(FileUtils.sizeOfAsBigInteger(fileExists))
                .workItemCreationTime(Instant.now())
                .build();
        log.debug("fileWorkItem: [{}]", fileWorkItem);
        return fileWorkItem;
    }

    public Function<File, FileWorkItem> asFunction(Path workingDirectory) {
        return new Function<>() {
            private final FileToFileWorkItemMapper mapper = new FileToFileWorkItemMapper();
            @Override
            public FileWorkItem apply(File file) {
                return mapper.map(workingDirectory, file);
            }
        };
    }
}
