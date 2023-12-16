package dev.alexhstone;


import dev.alexhstone.model.DiffResults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CalculateHashesAndReport {
    public static void main(String[] args) {
        CompareDirectories compareDirectories = new CompareDirectories(
                "F:\\Data\\Pictures",
                "O:\\Partial Backup\\Pictures",
                "C:\\tmp");

        DiffResults diffResults = compareDirectories.execute();
        log.info("diffResults: {}", diffResults);
    }
}