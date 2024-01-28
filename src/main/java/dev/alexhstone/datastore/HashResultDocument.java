package dev.alexhstone.datastore;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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

    private String hashResultJSON;
}
