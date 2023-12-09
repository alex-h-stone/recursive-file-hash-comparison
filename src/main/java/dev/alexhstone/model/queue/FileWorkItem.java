package dev.alexhstone.model.queue;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FileWorkItem {

    String absolutePathToFile;

    String absolutePathToWorkingDirectory;
}
