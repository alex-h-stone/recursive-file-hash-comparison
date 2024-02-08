package dev.alexhstone;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class ProgressLogging {

    private final AtomicLong progressCount;
    private final String loggingFormat;
    private final int loggingIntervalInItems;

    /**
     * @param loggingFormat An SLF4J logging format with a number processed since lat log statement placeholder.
     *                      e.g. "Consumed another {} messages"
     */
    public ProgressLogging(String loggingFormat, int loggingIntervalInItems) {
        this.progressCount = new AtomicLong();
        this.loggingFormat = loggingFormat;
        this.loggingIntervalInItems = loggingIntervalInItems;
    }

    public void incrementProgress() {
        if (progressCount.incrementAndGet() >= loggingIntervalInItems) {
            log.info(loggingFormat, progressCount.getAndSet(0));
        }
    }
}
