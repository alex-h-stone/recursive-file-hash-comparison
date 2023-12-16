package dev.alexhstone.model.queue;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigInteger;

@Value
@Builder
public class FileWorkItem {

    String absolutePathToFile;

    String absolutePathToWorkingDirectory;

    BigInteger fileSizeInBytes;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("absolutePathToFile", absolutePathToFile)
                .append("absolutePathToWorkingDirectory", absolutePathToWorkingDirectory)
                .append("fileSizeInBytes", fileSizeInBytes)
                .toString();
    }
}
