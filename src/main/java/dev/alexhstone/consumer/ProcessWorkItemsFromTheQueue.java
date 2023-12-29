package dev.alexhstone.consumer;

import dev.alexhstone.datastore.WorkItemHashResultRepository;
import dev.alexhstone.model.datastore.HashResult;
import dev.alexhstone.model.queue.WorkItem;
import dev.alexhstone.queue.DurableQueueImpl;
import dev.alexhstone.queue.QueueConsumer;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ProcessWorkItemsFromTheQueue {

    private final QueueConsumer queueConsumer;
    private final WorkItemHashResultRepository repository;
    private final WorkItemToHashResultMapper mapper;

    public static void main(String[] args) {
        DurableQueueImpl queue = new DurableQueueImpl();
        ProcessWorkItemsFromTheQueue processWorkItemsFromTheQueue = new ProcessWorkItemsFromTheQueue(queue);
        processWorkItemsFromTheQueue.execute();
    }

    public ProcessWorkItemsFromTheQueue(QueueConsumer queueConsumer) {
        this.queueConsumer = queueConsumer;
        repository = new WorkItemHashResultRepository();
        mapper = new WorkItemToHashResultMapper();
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

                if (repository.hasAlreadyBeenCalculated(workItem)) {
                    log.debug("Work item with ID [{}] has already been calculated so NOT processing",
                            workItem.getId());
                    continue;
                }

                HashResult hashResult = mapper.map(workItem);
                repository.put(hashResult);
                log.info("Completed processing the work item with ID: [{}]", workItem.getId());
            } else {
                numberOfContinuousUnsuccessfulDequeues.incrementAndGet();
            }
        } while (numberOfContinuousUnsuccessfulDequeues.get() < 50);

        log.info("Completed processing all work items on the queue");
        queueConsumer.destroy();
    }
}
