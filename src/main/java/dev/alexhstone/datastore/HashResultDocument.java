package dev.alexhstone.datastore;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document
@Getter
@Builder
@ToString
public class HashResultDocument {

    @Id
    @NonNull
    private String absolutePath;

    @Indexed(name = "relativePathToFileIndex")
    @NonNull
    private String relativePathToFile;

    @Indexed(name = "hashValueIndex")
    @NonNull
    private String hashValue;

    @Indexed(name = "partitionUuidIndex")
    private String partitionUuid;

    @NonNull
    private BigInteger sourceFileSizeInBytes;

    @NonNull
    private String hashResultJSON;
}
