package dev.alexhstone.diskmetadata;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PartitionMetaData {

    String diskName;
    String diskModel;
    String diskSerialNumber;
    String mountPoint;
    String type;
    String name;
    String identifier;
    String uuid;
}
