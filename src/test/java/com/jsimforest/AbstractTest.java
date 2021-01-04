package com.jsimforest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;

public class AbstractTest {

    public static Instant startedAt;

    @BeforeAll
    public static void initStartingTime() {
        startedAt = Instant.now();
    }

    @AfterAll
    static public void showTestDuration() {

        Instant endedAt = Instant.now();
        long duration = Duration.between(startedAt, endedAt).toMillis();
        System.out.println(MessageFormat.format("Tests duration : {0} ms", duration));
    }
}
