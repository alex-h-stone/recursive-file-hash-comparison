package org.backup.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class HashDetails {

    String hashValue;
    String hashingAlgorithmName;
}
