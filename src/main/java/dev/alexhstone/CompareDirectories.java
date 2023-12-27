package dev.alexhstone;

import dev.alexhstone.calculator.DiffResultsCalculator;
import dev.alexhstone.calculator.Location;
import dev.alexhstone.calculator.RecursiveFileHashCalculator;
import dev.alexhstone.model.DiffResults;
import dev.alexhstone.model.HashDiffResults;
import dev.alexhstone.storage.FileHashResultRepository;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class CompareDirectories {

    private final String leftAbsolutePath;
    private final String rightAbsolutePath;
    private final RecursiveFileHashCalculator recursiveFileHashCalculator;
    private final PersistAsJsonToFile persistAsJSONToFile;
    private final FileHashResultRepository fileHashResultRepository;

    public CompareDirectories(String leftAbsolutePath,
                              String rightAbsolutePath,
                              String reportDirectoryAbsolutePath) {
        // TODO add validate for methods parameters
        this.leftAbsolutePath = leftAbsolutePath;
        this.rightAbsolutePath = rightAbsolutePath;
        this.persistAsJSONToFile = new PersistAsJsonToFile(Paths.get(reportDirectoryAbsolutePath));
        this.fileHashResultRepository = new FileHashResultRepository();
        this.recursiveFileHashCalculator = new RecursiveFileHashCalculator();
    }

    public DiffResults execute() {
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            calculateAndStoreHashResults(leftAbsolutePath, Location.LEFT);
        });

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            calculateAndStoreHashResults(rightAbsolutePath, Location.RIGHT);
        });

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future2);

        combinedFuture.join();



        DiffResultsCalculator diffResultsCalculator = new DiffResultsCalculator();
        HashDiffResults hashDiffResults = null;
        //diffResultsCalculator.compareResults(fileHashResultRepository.getLeftKeys(),
          //      fileHashResultRepository.getRightKeys());
        // TODO create deserialise and read logic
        persistAsJSONToFile.persist(hashDiffResults, "diffResults.json");

        return null;
    }

    private void calculateAndStoreHashResults(String absolutePathToWorkingDirectory,
                                              Location location) {
        Path workingDirectory = Paths.get(absolutePathToWorkingDirectory);
        String absolutePath = workingDirectory.toFile().getAbsolutePath();

        log.info("About to calculate hashes for the working directory [{}]", absolutePath);
        recursiveFileHashCalculator.process(workingDirectory,
                fileHashResult -> fileHashResultRepository.put(fileHashResult));
        log.info("Completed calculating the hashes for the working directory [{}]", absolutePath);
    }


}
