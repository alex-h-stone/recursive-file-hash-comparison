package dev.alexhstone.producer;

import dev.alexhstone.RunnableApplication;
import dev.alexhstone.datastore.HashResultPersistenceService;
import dev.alexhstone.model.fileworkitem.FileWorkItem;
import dev.alexhstone.queue.QueuePublisher;
import dev.alexhstone.queue.Status;
import dev.alexhstone.util.Clock;
import dev.alexhstone.util.PathWalker;
import dev.alexhstone.validation.DirectoryValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkItemProducer implements RunnableApplication {

    @Value("${application.producer.workingDirectories}")
    private String semicolonSeparatedWorkingDirectories;

    private final QueuePublisher queue;
    private final HashResultPersistenceService persistenceService;

    @Override
    public boolean matches(String applicationNameToMatch) {
        return "producer".equalsIgnoreCase(applicationNameToMatch);
    }

    @Override
    public String getApplicationName() {
        return "workItemProducer";
    }

    @Override
    public void execute() {
        List<String> workingDirectories = Arrays.asList(semicolonSeparatedWorkingDirectories.split(";"));
        log.info("About to publish FileWorkItems from the working directories [{}] to the queue",
                workingDirectories);

        DirectoryValidator directoryValidator = new DirectoryValidator();
        Set<Path> validatedWorkingDirectories = workingDirectories
                .stream()
                .map(directoryValidator::validateExists)
                .collect(Collectors.toSet());

        queue.initialise();
        validatedWorkingDirectories
                .stream()
                .forEach(workingDirectory -> toStreamOfWorkItems(workingDirectory)
                        .forEach(publishToQueue()));
        queue.destroy();
    }

    private Consumer<FileWorkItem> publishToQueue() {
        return workItem -> {
            log.debug("About to publish the FileWorkItem with AbsolutePath: [{}]", workItem.getAbsolutePath());
            Status publishStatus = queue.publish(workItem);
            if (Status.SUCCESS.equals(publishStatus)) {
                log.debug("Successfully published the message (FileWorkItem) with AbsolutePath: [{}]", workItem.getAbsolutePath());
            } else {
                log.warn("Failed to publish the message (FileWorkItem) [{}]", workItem);
            }
        };
    }

    private Stream<FileWorkItem> toStreamOfWorkItems(Path workingDirectory) {
        FileToWorkItemMapper fileToWorkItemMapper = new FileToWorkItemMapper(new Clock());
        Function<File, FileWorkItem> toFileWorkItemMapper = fileToWorkItemMapper
                .asFunction(workingDirectory);
        PathWalker pathWalker = new PathWalker(workingDirectory);

        return pathWalker
                .walk()
                .map(Path::toFile)
                .filter(File::isFile)
                .filter(file -> persistenceService.doesNotContainUpToDateHashFor(file.getAbsolutePath(),
                        FileUtils.sizeOfAsBigInteger(file)))
                .map(toFileWorkItemMapper);
    }
}
