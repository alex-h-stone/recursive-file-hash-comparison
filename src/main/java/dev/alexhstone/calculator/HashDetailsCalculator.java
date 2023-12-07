package dev.alexhstone.calculator;

import dev.alexhstone.model.HashDetails;

import java.io.File;

public class HashDetailsCalculator {

    private final HashCalculator hashCalculator = new HashCalculator();

    public HashDetails calculateHashDetails(File file) {
        return HashDetails.builder()
                .hashingAlgorithmName(hashCalculator.getAlgorithmName())
                .hashValue(hashCalculator.calculateHashFor(file))
                .build();
    }
}
