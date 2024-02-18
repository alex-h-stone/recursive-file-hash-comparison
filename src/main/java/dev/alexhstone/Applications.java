package dev.alexhstone;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class Applications {

    private static final Function<Future<String>, String> TO_STATUS_STRING =
            stringFuture -> "Result:%s State:%s"
                    .formatted(stringFuture.resultNow(), stringFuture.state());

    @Autowired
    private List<RunnableApplication> runnableApplications;

    public void run(String applicationName) {

        List<RunnableApplication> matchingApplications = runnableApplications.stream()
                .filter(application -> application.matches(applicationName))
                .collect(Collectors.toList());

        Validate.notEmpty(matchingApplications, "Unknown application name: [%s]".formatted(applicationName));
        Validate.isTrue(matchingApplications.size() == 1,
                "Found multiple applications matching the name: [%s] they are: %s".formatted(applicationName, matchingApplications));

        RunnableApplication application = matchingApplications.getFirst();

        log.info("Executing {}", application.getApplicationName());
        executeWithThreads(application.getNumberOfThreadsToUseForExecution(), application::execute);
    }

    private void executeWithThreads(int numberOfThreads, Runnable runnable) {

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {

            Collection<Callable<String>> tasks = createTasks(numberOfThreads, runnable);
            List<Future<String>> futuresOfTasks = executor.invokeAll(tasks);

            List<String> threadResults = futuresOfTasks
                    .stream()
                    .map(TO_STATUS_STRING)
                    .toList();
            log.info("All threads have completed their tasks, results: {}", threadResults);

            executor.shutdown();
        } catch (InterruptedException e) {
            String message = "Unable to complete execution of threads due to: [%s]"
                    .formatted(e.getMessage());
            log.warn(message);
            throw new RuntimeException(message, e);
        }
    }

    private Collection<Callable<String>> createTasks(int numberOfTasksToCreate, Runnable runnable) {
        Collection<Callable<String>> tasks = new ArrayList<>();
        for (int task = 1; task <= numberOfTasksToCreate; task++) {
            Callable<String> callable = Executors.callable(runnable, "Success");
            tasks.add(callable);
        }
        return tasks;
    }
}
