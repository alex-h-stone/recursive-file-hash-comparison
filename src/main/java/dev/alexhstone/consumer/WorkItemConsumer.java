package dev.alexhstone.consumer;

import dev.alexhstone.ProgressLogging;
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

    private static final int MAX_NUMBER_OF_ATTEMPTS = 50;

    private final WorkItemToHashResultMapper mapper = new WorkItemToHashResultMapper();
    private final ProgressLogging progressLogging =
            new ProgressLogging("Consumed {} messages in the last {} minutes", 5);

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
        return 3;
    }

    @Override
    public void execute() {
        queueConsumer.initialise();
        log.info("About to consume work items from the queue");
        AtomicInteger numberOfContinuousUnsuccessfulDequeues = new AtomicInteger(0);
        do {
            Optional<WorkItem> fileWorkItemOptional = queueConsumer.consumeMessage();
            if (fileWorkItemOptional.isPresent()) {

                WorkItem workItem = fileWorkItemOptional.get();
                log.debug("About to process the workItem with ID: [{}]", workItem.getId());
                progressLogging.incrementProgress();
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
        } while (numberOfContinuousUnsuccessfulDequeues.get() < MAX_NUMBER_OF_ATTEMPTS);

        log.info("Completed processing all work items in the queue, attempted to get the next work item {} times but nothing was there",
                numberOfContinuousUnsuccessfulDequeues);
        queueConsumer.destroy();
    }
}
