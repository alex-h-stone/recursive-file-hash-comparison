package dev.alexhstone.consumer;

import dev.alexhstone.calculator.FileHashResultCalculator;
import dev.alexhstone.calculator.HashDetailsCalculator;
import dev.alexhstone.model.queue.FileWorkItem;
import dev.alexhstone.queue.DurableQueueImpl;
import dev.alexhstone.queue.QueueConsumer;
import dev.alexhstone.storage.FileHashResultRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ProcessWorkItemsFromTheQueue {

    private final QueueConsumer queueConsumer;
    private final FileHashResultRepository fileHashResultRepository;
    private final FileHashResultCalculator fileHashResultCalculator;

    public static void main(String[] args) {
        DurableQueueImpl queue = new DurableQueueImpl();
        ProcessWorkItemsFromTheQueue processWorkItemsFromTheQueue = new ProcessWorkItemsFromTheQueue(queue);
        processWorkItemsFromTheQueue.execute();
    }

    public ProcessWorkItemsFromTheQueue(QueueConsumer queueConsumer) {
        this.queueConsumer = queueConsumer;
        fileHashResultRepository = new FileHashResultRepository();
        fileHashResultCalculator = new FileHashResultCalculator(new HashDetailsCalculator());
    }

    private void execute() {
        queueConsumer.initialise();
        log.info("About to process work items from the queue");
        AtomicInteger numberOfContinuousUnsuccessfulDequeues = new AtomicInteger(0);
        do {
            Optional<FileWorkItem> fileWorkItemOptional = queueConsumer.consumeMessage();
            if (fileWorkItemOptional.isPresent()) {

                FileWorkItem fileWorkItem = fileWorkItemOptional.get();
                log.info("About to process the work item with ID: [{}]", fileWorkItem.getId());
                numberOfContinuousUnsuccessfulDequeues.set(0);

                // TODO add logic to use last modified date/time and size to save recalculating hashes?
                /*if (!fileHashResultRepository.isAlreadyPresent(fileWorkItem)) {
                    log.info("Not already present in cache so processing");
                    Path workingDirectory = new DirectoryValidator().validateExists(fileWorkItem.getAbsolutePathToWorkingDirectory());
                    File file = new FileValidator().validateExists(fileWorkItem.getAbsolutePath());
                    FileHashResult hashResult = fileHashResultCalculator.process(workingDirectory, file);
                    fileHashResultRepository.put(hashResult);
                } else {
                    log.info("Work item already present in cache so NOT processing");
                }*/
                log.info("Completed processing the work item with ID: [{}]", fileWorkItem.getId());
            } else {
                numberOfContinuousUnsuccessfulDequeues.incrementAndGet();
            }
        } while (numberOfContinuousUnsuccessfulDequeues.get() < 50);

        log.info("Completed processing all work items on the queue");
        queueConsumer.destroy();
    }
}
