package dev.alexhstone.consumer;

import dev.alexhstone.datastore.HashResultRepository;
import dev.alexhstone.model.hashresult.HashResult;
import dev.alexhstone.model.workitem.WorkItem;
import dev.alexhstone.queue.DurableQueueImpl;
import dev.alexhstone.queue.QueueConsumer;
import dev.alexhstone.util.Clock;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ProcessWorkItemsFromTheQueue {

    private final QueueConsumer queueConsumer;
    private final HashResultRepository repository;
    private final WorkItemToHashResultMapper mapper;

    public static void main(String[] args) {
        ProcessWorkItemsFromTheQueue processWorkItemsFromTheQueue = new ProcessWorkItemsFromTheQueue();
        processWorkItemsFromTheQueue.execute();
    }

    public ProcessWorkItemsFromTheQueue() {
        this.queueConsumer = new DurableQueueImpl();
        this.repository = new HashResultRepository();
        this.mapper = new WorkItemToHashResultMapper(new Clock());
    }

    private void execute() {
        queueConsumer.initialise();
        log.info("About to process work items from the workitem");
        AtomicInteger numberOfContinuousUnsuccessfulDequeues = new AtomicInteger(0);
        do {
            Optional<WorkItem> fileWorkItemOptional = queueConsumer.consumeMessage();
            if (fileWorkItemOptional.isPresent()) {

                WorkItem workItem = fileWorkItemOptional.get();
                log.debug("About to process the workItem with ID: [{}]", workItem.getId());
                numberOfContinuousUnsuccessfulDequeues.set(0);

                if (repository.hasAlreadyBeenCalculated(workItem)) {
                    log.warn("Work item with ID [{}] has already been calculated so NOT processing",
                            workItem.getId());
                    continue;
                }

                HashResult hashResult = mapper.map(workItem);
                repository.put(hashResult);
                log.debug("Completed processing the work item with ID: [{}]", workItem.getId());
            } else {
                numberOfContinuousUnsuccessfulDequeues.incrementAndGet();
            }
        } while (numberOfContinuousUnsuccessfulDequeues.get() < 50);

        log.info("Completed processing all work items on the workitem");
        queueConsumer.destroy();
    }
}
