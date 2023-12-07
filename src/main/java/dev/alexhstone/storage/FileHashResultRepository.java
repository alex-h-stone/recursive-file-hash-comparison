package dev.alexhstone.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alexhstone.calculator.Location;
import dev.alexhstone.model.FileHashResult;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

public class FileHashResultRepository {


    private final HTreeMap<Integer, String> leftMap;
    private final HTreeMap<Integer, String> rightMap;
    private final Gson gson;

    public FileHashResultRepository() {
        this.gson = new GsonBuilder().create();
        try (DB db = DBMaker.tempFileDB().make()) {
            ;
            this.leftMap = db.hashMap(Location.LEFT.name(),
                    Serializer.INTEGER,
                    Serializer.STRING).createOrOpen();
        }
        try (DB db = DBMaker.tempFileDB().make()) {
            this.rightMap = db.hashMap(Location.RIGHT.name(),
                    Serializer.INTEGER,
                    Serializer.STRING).createOrOpen();
        }
    }

    public void put(Location schema, FileHashResult fileHashResult) {
        String json = gson.toJson(fileHashResult);
        switch (schema) {
            case LEFT -> {
                leftMap.put(fileHashResult.hashCode(), json);
            }
            case RIGHT -> {
                rightMap.put(fileHashResult.hashCode(), json);
            }
        }
    }
}
