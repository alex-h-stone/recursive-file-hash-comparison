package dev.alexhstone.consumer;

import dev.alexhstone.calculator.FileHashResultCalculator;
import dev.alexhstone.calculator.HashDetailsCalculator;
import dev.alexhstone.datastore.WorkItemHashResultRepository;
import dev.alexhstone.model.queue.WorkItem;
import dev.alexhstone.queue.DurableQueueImpl;
import dev.alexhstone.queue.QueueConsumer;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ProcessWorkItemsFromTheQueue {

    private final QueueConsumer queueConsumer;
    private final WorkItemHashResultRepository workItemHashResultRepository;
    private final FileHashResultCalculator fileHashResultCalculator;

    public static void main(String[] args) {
        DurableQueueImpl queue = new DurableQueueImpl();
        ProcessWorkItemsFromTheQueue processWorkItemsFromTheQueue = new ProcessWorkItemsFromTheQueue(queue);
        processWorkItemsFromTheQueue.execute();
    }

    public ProcessWorkItemsFromTheQueue(QueueConsumer queueConsumer) {
        this.queueConsumer = queueConsumer;
        workItemHashResultRepository = new WorkItemHashResultRepository();
        fileHashResultCalculator = new FileHashResultCalculator(new HashDetailsCalculator());
    }

    private void execute() {
        queueConsumer.initialise();
        log.info("About to process work items from the queue");
        AtomicInteger numberOfContinuousUnsuccessfulDequeues = new AtomicInteger(0);
        do {
            Optional<WorkItem> fileWorkItemOptional = queueConsumer.consumeMessage();
            if (fileWorkItemOptional.isPresent()) {

                WorkItem workItem = fileWorkItemOptional.get();
                log.info("About to process the work item with ID: [{}]", workItem.getId());
                numberOfContinuousUnsuccessfulDequeues.set(0);

                // TODO add logic to use last modified date/time and size to save recalculating hashes?
                /*if (!workItemHashResultRepository.isAlreadyPresent(workItem)) {
                    log.info("Not already present in cache so processing");
                    Path workingDirectory = new DirectoryValidator().validateExists(workItem.getAbsolutePathToWorkingDirectory());
                    File file = new FileValidator().validateExists(workItem.getAbsolutePath());
                    WorkItemHashResult hashResult = fileHashResultCalculator.process(workingDirectory, file);
                    workItemHashResultRepository.put(hashResult);
                } else {
                    log.info("Work item already present in cache so NOT processing");
                }*/
                log.info("Completed processing the work item with ID: [{}]", workItem.getId());
            } else {
                numberOfContinuousUnsuccessfulDequeues.incrementAndGet();
            }
        } while (numberOfContinuousUnsuccessfulDequeues.get() < 50);

        log.info("Completed processing all work items on the queue");
        queueConsumer.destroy();
    }
}
