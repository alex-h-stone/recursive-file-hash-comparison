package dev.alexhstone.consumer;

import dev.alexhstone.RunnableApplication;
import dev.alexhstone.datastore.HashResultPersistenceService;
import dev.alexhstone.model.hashresult.HashResult;
import dev.alexhstone.model.workitem.WorkItem;
import dev.alexhstone.queue.QueueConsumer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@AllArgsConstructor
public class WorkItemConsumer implements RunnableApplication {

    private final WorkItemToHashResultMapper mapper = new WorkItemToHashResultMapper();
    private final QueueConsumer queueConsumer;
    private final HashResultPersistenceService hashResultPersistenceService;

    @Override
    public boolean matches(String applicationNameToMatch) {
        return "consumer".equalsIgnoreCase(applicationNameToMatch);
    }

    @Override
    public String getApplicationName() {
        return "workItemConsumer";
    }

    @Override
    public int getNumberOfThreadsToUseForExecution() {
        return 2;
    }

    @Override
    public void execute() {
        queueConsumer.initialise();
        log.info("About to process work items from the queue");
        AtomicInteger numberOfContinuousUnsuccessfulDequeues = new AtomicInteger(0);
        do {
            Optional<WorkItem> fileWorkItemOptional = queueConsumer.consumeMessage();
            if (fileWorkItemOptional.isPresent()) {

                WorkItem workItem = fileWorkItemOptional.get();
                log.debug("About to process the workItem with ID: [{}]", workItem.getId());
                numberOfContinuousUnsuccessfulDequeues.set(0);

                if (hashResultPersistenceService.hasAlreadyBeenCalculated(workItem)) {
                    log.warn("Work item with Id [{}] has already been calculated so NOT processing",
                            workItem.getId());
                    continue;
                }

                HashResult hashResult = mapper.map(workItem);
                hashResultPersistenceService.store(hashResult);
                log.debug("Completed processing the work item with ID: [{}]", workItem.getId());
            } else {
                numberOfContinuousUnsuccessfulDequeues.incrementAndGet();
            }
        } while (numberOfContinuousUnsuccessfulDequeues.get() < 50);

        log.info("Completed processing all work items on the queue");
        queueConsumer.destroy();
    }
}
