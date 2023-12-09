package dev.alexhstone.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alexhstone.calculator.HashCalculator;
import dev.alexhstone.calculator.Location;
import dev.alexhstone.model.FileHashResult;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;

@Slf4j
public class FileHashResultRepository {

    public static final String DATABASE_FILE_NAME = "databaseFileStore.dat";
    private final HTreeMap<String, String> leftMap;
    private final HTreeMap<String, String> rightMap;
    private final Gson gson;
    private final HashCalculator hashCalculator;

    public FileHashResultRepository(Path repositoryStorageDirectory) {
        File file = repositoryStorageDirectory.resolve(DATABASE_FILE_NAME).toFile();
        DB db = DBMaker.fileDB(file).make();

        this.leftMap = db.hashMap(Location.LEFT.name(),
                Serializer.STRING, // TODO replace with with a more sophisticated hashing independent
                // of equals hashCode
                Serializer.STRING).createOrOpen();

        this.rightMap = db.hashMap(Location.RIGHT.name(),
                Serializer.STRING,
                Serializer.STRING).createOrOpen();
        this.gson = new GsonBuilder().create();
        hashCalculator = new HashCalculator();
    }

    public void put(Location schema, FileHashResult fileHashResult) {
        String json = gson.toJson(fileHashResult);
        final String hashCode = toHash(fileHashResult);
        switch (schema) {
            case LEFT -> {
                if (leftMap.containsKey(hashCode)) {
                    log.warn("Found hash collision in left map for [{}] as [{}] is already in the map",
                            fileHashResult, leftMap.get(hashCode));
                }
                leftMap.put(toHash(fileHashResult), json);
            }
            case RIGHT -> {
                if (rightMap.containsKey(hashCode)) {
                    log.warn("Found hash collision in right map for [{}] as [{}] is already in the map",
                            fileHashResult, rightMap.get(hashCode));
                }
                rightMap.put(toHash(fileHashResult), json);
            }
        }
    }

    //public boolean isAlreadyPresent()

    private String toHash(FileHashResult fileHashResult) {
        return hashCalculator.calculateHashFor(fileHashResult.getRelativePathToFile());
    }

    public Set<String> getLeftKeys() {
        return leftMap.getKeys();
    }

    public Set<String> getRightKeys() {
        return rightMap.getKeys();
    }
}
