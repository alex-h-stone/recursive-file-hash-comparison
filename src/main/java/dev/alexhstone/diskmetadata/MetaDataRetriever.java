package dev.alexhstone.diskmetadata;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
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
    public List<PartitionMetaData> retrieveMetaDataFor(String logicalDriveLetter) {
        if (StringUtils.isAllBlank(logicalDriveLetter)) {
            String message = "Expected valid logicalDriveLetter e.g. C but found [%s]"
                    .formatted(logicalDriveLetter);
            log.warn(message);
            throw new IllegalArgumentException(message);
        }

        List<HWDiskStore> allDisks = retrieveDisks();

        List<PartitionAndDisk> allPartitionsEachWithAssociatedDisk = retrievePartitionsWithDisks(allDisks);

        String singleLetterLogicalDriveIdentifier = StringUtils.substring(logicalDriveLetter, 0, 1);
        List<PartitionAndDisk> matchingPartitions = allPartitionsEachWithAssociatedDisk
                .stream()
                .filter(o -> StringUtils.containsIgnoreCase(o.partition().getMountPoint(), singleLetterLogicalDriveIdentifier))
                .toList();

        if (matchingPartitions.isEmpty()) {
            log.info("Found no matching partitions for logical drive: [{}]", logicalDriveLetter);
            return Collections.emptyList();
        }

        return matchingPartitions.stream()
                .map(TO_PARTITION_META_DATA)
                .collect(Collectors.toList());
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


    private static final Function<PartitionAndDisk, PartitionMetaData> TO_PARTITION_META_DATA =
            partitionAndDisk -> {
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
            };


    private static String trim(String stringToBeTrimmed) {
        return StringUtils.trim(stringToBeTrimmed);
    }

    public String retrievePartitionUuid(String partitionMountPointLetter) {
        SortedSet<String> uniquePartitionUuids = retrieveMetaDataFor(partitionMountPointLetter)
                .stream()
                .map(PartitionMetaData::getUuid)
                .collect(Collectors.toCollection(TreeSet::new));

        if (uniquePartitionUuids.isEmpty()) {
            throw new IllegalStateException("Unknown Partition UUID for [%s]".formatted(partitionMountPointLetter));
        }

        if (uniquePartitionUuids.size() == 1) {
            return uniquePartitionUuids.first();
        }

        return String.join("_", uniquePartitionUuids);
    }

    private record PartitionAndDisk(HWPartition partition, HWDiskStore disk) {
    }
}
