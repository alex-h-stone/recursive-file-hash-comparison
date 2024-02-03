package dev.alexhstone;

public interface RunnableApplication {

    boolean matches(String applicationNameToMatch);

    void execute();

    String getApplicationName();

    /**
     * Default is a single thread.
     *
     * @return The number of threads to use when executing this application.
     */
    default int getNumberOfThreadsToUseForExecution() {
        return 1;
    }
}
