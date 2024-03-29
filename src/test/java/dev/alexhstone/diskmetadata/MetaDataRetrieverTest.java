package dev.alexhstone.diskmetadata;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MetaDataRetrieverTest {

    private MetaDataRetriever metaDataRetriever;

    @BeforeEach
    void setUp() {
        metaDataRetriever = new MetaDataRetriever();
    }

    @Test
    void shouldRetrievePartitionMetaDataForCDrive() {
        List<PartitionMetaData> partitionMetaDataList = metaDataRetriever.retrieveMetaDataFor("C");

        assertThat(partitionMetaDataList, Matchers.hasSize(Matchers.greaterThanOrEqualTo(1)));
        PartitionMetaData actualMetaData = partitionMetaDataList.getFirst();

        Assertions.assertAll(
                () -> assertThat(actualMetaData.getDiskName(), isNotEmptyOrNull()),
                () -> assertThat(actualMetaData.getDiskModel(), isNotEmptyOrNull()),
                () -> assertThat(actualMetaData.getDiskSerialNumber(), isNotEmptyOrNull()),
                () -> assertThat(actualMetaData.getMountPoint(), isNotEmptyOrNull()),
                () -> assertThat(actualMetaData.getType(), isNotEmptyOrNull()),
                () -> assertThat(actualMetaData.getName(), isNotEmptyOrNull()),
                () -> assertThat(actualMetaData.getIdentifier(), isNotEmptyOrNull()),
                () -> assertThat(actualMetaData.getUuid(), isNotEmptyOrNull())
        );
    }

    @Test
    void shouldRetrievePartitionMetaDataForCDriveWithColonAndBackslash() {
        List<PartitionMetaData> partitionMetaDataList = metaDataRetriever.retrieveMetaDataFor("C:\\");

        assertThat(partitionMetaDataList, Matchers.hasSize(Matchers.greaterThanOrEqualTo(1)));
        PartitionMetaData actualMetaData = partitionMetaDataList.getFirst();

        Assertions.assertAll(
                () -> assertThat(actualMetaData.getDiskName(), isNotEmptyOrNull()),
                () -> assertThat(actualMetaData.getDiskModel(), isNotEmptyOrNull()),
                () -> assertThat(actualMetaData.getDiskSerialNumber(), isNotEmptyOrNull()),
                () -> assertThat(actualMetaData.getMountPoint(), isNotEmptyOrNull()),
                () -> assertThat(actualMetaData.getType(), isNotEmptyOrNull()),
                () -> assertThat(actualMetaData.getName(), isNotEmptyOrNull()),
                () -> assertThat(actualMetaData.getIdentifier(), isNotEmptyOrNull()),
                () -> assertThat(actualMetaData.getUuid(), isNotEmptyOrNull())
        );
    }

    private Matcher<String> isNotEmptyOrNull() {
        return not(Matchers.isEmptyOrNullString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "    "})
    void shouldThrowIllegalArgumentExceptionForNullEmptyOrBlankLogicalDriveLetter(String logicalDriveLetter) {
        IllegalArgumentException actualException = assertThrows(IllegalArgumentException.class,
                () -> metaDataRetriever.retrieveMetaDataFor(logicalDriveLetter));

        String expectedMessage = "Expected valid logicalDriveLetter e.g. C but found [" + logicalDriveLetter + "]";
        assertThat(actualException.getMessage(), equalTo(expectedMessage));
    }
}