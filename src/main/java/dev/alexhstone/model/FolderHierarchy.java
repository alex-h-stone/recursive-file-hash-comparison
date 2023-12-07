package dev.alexhstone.model;


import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class FolderHierarchy {

    List<FileHashResult> fileHashResults;
}
