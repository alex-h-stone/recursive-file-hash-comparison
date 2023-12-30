package dev.alexhstone.util;

import java.time.Instant;

public class Clock {

    public Instant getInstantNow() {
        return Instant.now();
    }

    /**
     * Factory method to create a stub Clock for testing purposes
     *
     * @param instant of the form "2023-12-20T10:15:30Z"
     * @return a Clock which always returns the same (stubbed) instant
     */
    public static Clock stubClockOf(String instant) {
        return stubClockOf(Instant.parse(instant));
    }

    /**
     * Factory method to create a stub Clock for testing purposes
     *
     * @param instant Instant
     * @return a Clock which always returns the same (stubbed) instant
     */
    public static Clock stubClockOf(Instant instant) {
        return new Clock() {
            @Override
            public Instant getInstantNow() {
                return instant;
            }
        };
    }
}
