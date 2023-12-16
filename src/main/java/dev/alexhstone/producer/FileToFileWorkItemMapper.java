package dev.alexhstone.producer;

import dev.alexhstone.model.queue.FileWorkItem;
import dev.alexhstone.util.DirectoryValidator;
import dev.alexhstone.util.FileValidator;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;

public class FileToFileWorkItemMapper {

    private final FileValidator fileValidator = new FileValidator();
    private final DirectoryValidator directoryValidator = new DirectoryValidator();

    public FileWorkItem map(Path workingDirectory, File file) {
        Path validWorkingDirectory = directoryValidator.validateExists(workingDirectory);
        File fileExists = fileValidator.validateExists(file);

        return FileWorkItem.builder()
                .absolutePathToFile(fileExists.getAbsolutePath())
                .absolutePathToWorkingDirectory(validWorkingDirectory.toFile().getAbsolutePath())
                .fileSizeInBytes(FileUtils.sizeOfAsBigInteger(fileExists))
                .build();
    }

    public Function<File, FileWorkItem> asFunction(Path workingDirectory) {
        return new Function<File, FileWorkItem>() {
            private final FileToFileWorkItemMapper mapper = new FileToFileWorkItemMapper();

            @Override
            public FileWorkItem apply(File file) {
                return mapper.map(workingDirectory, file);
            }
        };
    }
}
