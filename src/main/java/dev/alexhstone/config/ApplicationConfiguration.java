package dev.alexhstone.config;

public class ApplicationConfiguration {

    public static String getActiveMQBrokerURL() {
        return "tcp://localhost:61616";
    }

    public static String getActiveMQQueueName() {
        return "testQueue";
    }
}
