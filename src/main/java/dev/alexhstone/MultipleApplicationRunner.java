package dev.alexhstone;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class MultipleApplicationRunner implements ApplicationRunner {

    public static final String INVALID_ARGUEMENTS_MESSAGE = "Expected application option arguments but found " +
            "none, check the 'How to run' section in the README.md";

    private final Applications applications;

    @Override
    public void run(ApplicationArguments arguments) {
        List<String> nonOptionArgs = arguments.getNonOptionArgs();
        log.info("About to execute application with nonOptionArgs {}", nonOptionArgs);

        Validate.isTrue(nonOptionArgs.size() == 1, INVALID_ARGUEMENTS_MESSAGE);
        String applicationsToRunRaw = nonOptionArgs.get(0);
        Validate.notBlank(applicationsToRunRaw, INVALID_ARGUEMENTS_MESSAGE);

        String[] applicationsToRunSplit = applicationsToRunRaw.split(",");

        Arrays.stream(applicationsToRunSplit)
                .forEach(applicationName -> {
                    log.info("About to run the application [{}]", applicationName);
                    applications.run(applicationName);
                    log.info("Completed running the application [{}]", applicationName);
                });
    }
}
