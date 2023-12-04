package org.backup.model;


import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class FolderHierarchy {
    String rootFolderAbsolutePath;
    List<FileHashResult> fileHashResults;
}
