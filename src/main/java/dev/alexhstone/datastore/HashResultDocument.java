package dev.alexhstone.datastore;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

// TODO replace "hashResults" collection name with either config driven or default
// class name driven collection name
@Document(collection = "hashResults")
@Getter
@Builder
public class HashResultDocument {

    @Id
    private String absolutePath;

    @Indexed
    private String relativePathToFile;
    @Indexed
    private String hashValue;
    @Indexed
    private String partitionUuid;

    private BigInteger sourceFileSizeInBytes;

    private String hashResultJSON;
}
