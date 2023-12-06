package dev.alexhstone.model;


import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class FolderHierarchy {

    String absolutePathToWorkingDirectory;

    List<FileHashResult> fileHashResults;
}
