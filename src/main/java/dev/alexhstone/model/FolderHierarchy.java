package dev.alexhstone.model;


import dev.alexhstone.model.datastore.WorkItemHashResult;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class FolderHierarchy {

    List<WorkItemHashResult> workItemHashResults;
}
