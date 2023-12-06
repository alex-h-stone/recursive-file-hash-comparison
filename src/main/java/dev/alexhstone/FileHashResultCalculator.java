package dev.alexhstone;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import dev.alexhstone.model.FileHashResult;
import dev.alexhstone.model.HashDetails;
import dev.alexhstone.util.HashCalculator;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;

@RequiredArgsConstructor
public class FileHashResultCalculator {

    private final HashCalculator hashCalculator;

    public FileHashResult process(Path absolutePathToWorkingDirectoryFile, File file) {
        HashDetails hashDetails = calculateHashDetails(file);

        BigInteger sizeOfFileInBytes = FileUtils.sizeOfAsBigInteger(file);
        String byteCountToDisplaySize = FileUtils.byteCountToDisplaySize(sizeOfFileInBytes);

        String absolutePathToWorkingDirectory = absolutePathToWorkingDirectoryFile.toFile().getAbsolutePath();
        String absolutePathToFile = file.getAbsolutePath();

        String pathRelativeToWorkingDirectory = absolutePathToFile.replace(absolutePathToWorkingDirectory, "")
                .replace(file.getName(), "");

        return FileHashResult.builder()
                .fileName(file.getName())
                .relativePathToFile(pathRelativeToWorkingDirectory)
                .absolutePathToFile(absolutePathToFile)
                .fileSizeInBytes(sizeOfFileInBytes)
                .fileSize(byteCountToDisplaySize)
                .hashDetails(hashDetails)
                .build();
    }


    private HashDetails calculateHashDetails(File file) {
        return HashDetails.builder()
                .hashingAlgorithmName(hashCalculator.getAlgorithmName())
                .hashValue(hashCalculator.calculateHashFor(file))
                .build();
    }
}
