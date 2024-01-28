package dev.alexhstone.producer;

import dev.alexhstone.config.ApplicationConfiguration;
import dev.alexhstone.model.workitem.WorkItem;
import dev.alexhstone.queue.QueuePublisher;
import dev.alexhstone.queue.Status;
import dev.alexhstone.util.Clock;
import dev.alexhstone.util.PathWalker;
import dev.alexhstone.validation.DirectoryValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@AllArgsConstructor
public class PublishWorkItemsToQueue {

    private final ApplicationConfiguration configuration;
    private final QueuePublisher queue;

    public void execute() {
        // TODO handle multiple directories, split on ,?
        List<String> workingDirectoriesList = Arrays.asList(configuration.getWorkingDirectories());
        log.info("About to publish file work items from the working directories {} to the queue",
                workingDirectoriesList);

        DirectoryValidator directoryValidator = new DirectoryValidator();
        Set<Path> validatedWorkingDirectories = workingDirectoriesList
                .stream()
                .map(directoryValidator::validateExists)
                .collect(Collectors.toSet());


        queue.initialise();
        validatedWorkingDirectories
                .parallelStream()
                .forEach(workingDirectory -> toStreamOfWorkItems(workingDirectory)
                        .forEach(publishToQueue()));
        queue.destroy();
    }

    private Consumer<WorkItem> publishToQueue() {
        return workItem -> {
            log.debug("About to publish the WorkItem with AbsolutePath: [{}]", workItem.getAbsolutePath());
            Status publishStatus = queue.publish(workItem);
            if(Status.SUCCESS.equals(publishStatus)){
                log.debug("Successfully published the message (WorkItem) with AbsolutePath: [{}]", workItem.getAbsolutePath());
            } else {
                log.warn("Failed to publish the message (WorkItem) [{}]", workItem);
            }
        };
    }

    private Stream<WorkItem> toStreamOfWorkItems(Path workingDirectory) {
        FileToWorkItemMapper fileToWorkItemMapper = new FileToWorkItemMapper(new Clock());
        Function<File, WorkItem> toFileWorkItemMapper = fileToWorkItemMapper
                .asFunction(workingDirectory);
        PathWalker pathWalker = new PathWalker(workingDirectory);

        return pathWalker
                .walk()
                .parallel()
                .map(Path::toFile)
                .map(toFileWorkItemMapper);
    }
}
