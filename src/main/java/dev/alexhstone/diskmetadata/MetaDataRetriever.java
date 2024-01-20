package dev.alexhstone.diskmetadata;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class MetaDataRetriever {

    private static final List<HWDiskStore> EMPTY_LIST = List.of();

    private static final Function<HWDiskStore, Stream<PartitionAndDisk>> TO_PARTITION_AND_DISK =
            disk -> disk.getPartitions().stream()
                    .map(partition -> new PartitionAndDisk(partition, disk));

    /**
     * Retrieve disk meta data for a partition, including disk name, model etc.
     *
     * @param logicalDriveLetter Single letter logical drive/partition identifier
     *                           e.g. for C:\ the logicalDriveLetter is C
     * @return PartitionMetaData
     */
    public Optional<PartitionMetaData> retrieveMetaDataFor(String logicalDriveLetter) {
        if (StringUtils.isAllBlank(logicalDriveLetter)) {
            String message = "Expected valid logicalDriveLetter e.g. C but [%s] was provided"
                    .formatted(logicalDriveLetter);
            throw new IllegalArgumentException(message);
        }
        String singleLetterLogicalDriveIdentifier = StringUtils.substring(logicalDriveLetter, 0, 1);

        List<HWDiskStore> diskStores = retrieveDisks();

        List<PartitionAndDisk> partitionsAndDisks = retrievePartitionsWithDisks(diskStores);

        List<PartitionAndDisk> matchingPartitions = partitionsAndDisks
                .stream()
                .filter(o ->
                {
                    String mountPoint = o.getPartition().getMountPoint();
                    return StringUtils.containsIgnoreCase(mountPoint,singleLetterLogicalDriveIdentifier);
                })
                .collect(Collectors.toList());

        if (matchingPartitions.isEmpty()) {
            return Optional.empty();
        }

        PartitionAndDisk hwPartition = matchingPartitions.get(0);

        return Optional.of(createPartitionMetaData(hwPartition));
    }

    private List<PartitionAndDisk> retrievePartitionsWithDisks(List<HWDiskStore> diskStores) {
        return diskStores.stream()
                .flatMap(TO_PARTITION_AND_DISK)
                .collect(Collectors.toList());
    }

    private List<HWDiskStore> retrieveDisks() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        if (Objects.isNull(hardware)) {
            return EMPTY_LIST;
        }

        List<HWDiskStore> diskStores = hardware.getDiskStores();
        if (Objects.isNull(diskStores)) {
            return EMPTY_LIST;
        }

        return diskStores;
    }

    private PartitionMetaData createPartitionMetaData(PartitionAndDisk partitionAndDisk) {
        HWDiskStore diskStore = partitionAndDisk.disk;
        HWPartition partition = partitionAndDisk.partition;
        return PartitionMetaData.builder()
                .diskName(trim(diskStore.getName()))
                .diskModel(trim(diskStore.getModel()))
                .diskSerialNumber(trim(diskStore.getSerial()))
                .mountPoint(trim(partition.getMountPoint()))
                .type(trim(partition.getType()))
                .name(trim(partition.getName()))
                .identifier(trim(partition.getIdentification()))
                .uuid(trim(partition.getUuid()))
                .build();
    }

    private String trim(String stringToBeTrimmed) {
        return StringUtils.trim(stringToBeTrimmed);
    }

    @Value
    private static class PartitionAndDisk {
        HWPartition partition;
        HWDiskStore disk;
    }
}
