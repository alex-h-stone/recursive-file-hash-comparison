package dev.alexhstone;

import dev.alexhstone.consumer.WorkItemConsumer;
import dev.alexhstone.producer.WorkItemProducer;
import dev.alexhstone.reports.DuplicateFileReport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

@Slf4j
@Component
@AllArgsConstructor
public class Applications {

    private static final Function<Future<String>, String> TO_STATUS_STRING =
            stringFuture -> "Result:%s State:%s"
                    .formatted(stringFuture.resultNow(), stringFuture.state());

    private final WorkItemProducer workItemProducer;
    private final WorkItemConsumer workItemConsumer;
    private final DuplicateFileReport duplicateFilesReport;

    public void run(String applicationName) {

        switch (applicationName) {
            case "producer":
                log.info("Executing workItemProducer");
                executeWithThreads(1, workItemProducer::execute);
                break;
            case "consumer":
                log.info("Executing workItemConsumer");
                executeWithThreads(5, workItemConsumer::execute);
                break;
            case "findDuplicateFiles":
                log.info("Executing duplicateFilesReport");
                executeWithThreads(1, duplicateFilesReport::execute);
                break;
            default:
                String message = "Unknown application name: [%s]".formatted(applicationName);
                throw new IllegalArgumentException(message);
        }
    }

    private void executeWithThreads(int numberOfThreads, Runnable runnable) {

        try (ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads)) {

            Collection<Callable<String>> tasks = createTasks(numberOfThreads, runnable);
            List<Future<String>> futuresOfTasks = executor.invokeAll(tasks);

            List<String> threadResults = futuresOfTasks
                    .stream()
                    .map(TO_STATUS_STRING)
                    .toList();
            log.info("All threads have completed their tasks with statuses: " + threadResults);

            executor.shutdown();
        } catch (InterruptedException e) {
            String message = "Unable to complete execution of threads due to: [%s]"
                    .formatted(e.getMessage());
            throw new RuntimeException(message, e);
        }
    }

    private Collection<Callable<String>> createTasks(int numberOfThreads, Runnable runnable) {
        Collection<Callable<String>> tasks = new ArrayList<>();
        for (int threadNumber = 1; threadNumber <= numberOfThreads; threadNumber++) {
            Callable<String> callable = Executors.callable(runnable, "Success");
            tasks.add(callable);
        }
        return tasks;
    }
}
