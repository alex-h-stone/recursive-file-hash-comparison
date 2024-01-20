package dev.alexhstone.diskmetadata;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static dev.alexhstone.util.HamcrestUtilities.containsStrings;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class MetaDataRetrieverTest {

    private MetaDataRetriever metaDataRetriever;

    @BeforeEach
    void setUp() {
        metaDataRetriever = new MetaDataRetriever();
    }

    @Test
    void shouldRetrievePartitionMetaDataForCDrive() {
        Optional<PartitionMetaData> partitionMetaDataOptional = metaDataRetriever.retrieveMetaDataFor("C");

        assertTrue(partitionMetaDataOptional.isPresent());
        PartitionMetaData actualPartitionMetaData = partitionMetaDataOptional.get();
        assertThat(actualPartitionMetaData.getMountPoint(), not(Matchers.isEmptyOrNullString()));
        assertThat(actualPartitionMetaData.getUuid(), not(Matchers.isEmptyOrNullString()));
    }

    @Test
    void shouldRetrievePartitionMetaDataForCDriveWithColonAndBackslash() {
        Optional<PartitionMetaData> partitionMetaDataOptional = metaDataRetriever.retrieveMetaDataFor("C:\\");

        assertTrue(partitionMetaDataOptional.isPresent());
        PartitionMetaData actualPartitionMetaData = partitionMetaDataOptional.get();
        assertThat(actualPartitionMetaData.getMountPoint(), not(Matchers.isEmptyOrNullString()));
        assertThat(actualPartitionMetaData.getUuid(), not(Matchers.isEmptyOrNullString()));
    }

    @ParameterizedTest
    @ValueSource(strings ={"", " ", "    "})
    void shouldThrowIllegalArgumentExceptionForNullEmptyOrBlankLogicalDriveLetter(String logicalDriveLetter) {
        IllegalArgumentException actualException = assertThrows(IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        metaDataRetriever.retrieveMetaDataFor(logicalDriveLetter);
                    }
                });

        assertThat(actualException.getMessage(), containsStrings("Expected valid logicalDriveLetter e.g. C",
                "[" + logicalDriveLetter + "]"));
    }
}