package dev.alexhstone.model;


import dev.alexhstone.model.hashresult.HashResult;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class WorkingDirectoryHashResults {

    String partitionUuid; // TODO check population
    List<HashResult> hashResults;
}
