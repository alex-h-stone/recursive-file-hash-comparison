package dev.alexhstone.consumer;

import dev.alexhstone.calculator.FileHashResultCalculator;
import dev.alexhstone.calculator.HashDetailsCalculator;
import dev.alexhstone.model.FileHashResult;
import dev.alexhstone.model.queue.FileWorkItem;
import dev.alexhstone.queue.DurableQueueImpl;
import dev.alexhstone.queue.QueueConsumer;
import dev.alexhstone.storage.FileHashResultRepository;
import dev.alexhstone.validation.DirectoryValidator;
import dev.alexhstone.validation.FileValidator;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
public class ProcessFilesFromTheQueue {

    private final QueueConsumer queueConsumer;
    private final FileHashResultRepository fileHashResultRepository;
    private final FileHashResultCalculator fileHashResultCalculator;

    public static void main(String[] args) {
        DurableQueueImpl queue = new DurableQueueImpl();
        ProcessFilesFromTheQueue processFilesFromTheQueue = new ProcessFilesFromTheQueue(queue);
        processFilesFromTheQueue.execute();
    }

    public ProcessFilesFromTheQueue(QueueConsumer queueConsumer) {
        this.queueConsumer = queueConsumer;
        fileHashResultRepository = new FileHashResultRepository();
        fileHashResultCalculator = new FileHashResultCalculator(new HashDetailsCalculator());
    }

    private void execute() {
        queueConsumer.initialise();
        log.info("About to process work items from the queue");
        boolean workItemToProcess = true;
        do {
            Optional<FileWorkItem> fileWorkItemOptional = queueConsumer.consumeMessage();
            if (fileWorkItemOptional.isPresent()) {

                FileWorkItem fileWorkItem = fileWorkItemOptional.get();
                log.info("About to process the fileWorkItem: [{}]", fileWorkItem);

                // TODO add logic to use last modified date/time and size to save recalculating hashes?
                if (!fileHashResultRepository.isAlreadyPresent(fileWorkItem)) {
                    log.info("Not already present in cache so processing");
                    Path workingDirectory = new DirectoryValidator().validateExists(fileWorkItem.getAbsolutePathToWorkingDirectory());
                    File file = new FileValidator().validateExists(fileWorkItem.getAbsolutePath());
                    FileHashResult hashResult = fileHashResultCalculator.process(workingDirectory, file);
                    fileHashResultRepository.put(hashResult);
                } else {
                    log.info("Work item already present in cache so NOT processing");
                }
            } else {
                workItemToProcess = false;
            }
        } while (workItemToProcess);

        log.info("Completed processing all work items on the queue");
        queueConsumer.destroy();
    }
}
