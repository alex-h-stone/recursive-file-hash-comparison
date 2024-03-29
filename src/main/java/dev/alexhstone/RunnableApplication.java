package dev.alexhstone;

public interface RunnableApplication {

    boolean matches(String applicationNameToMatch);

    void execute();

    String getApplicationName();

    int getNumberOfThreadsToUseForExecution();
}
