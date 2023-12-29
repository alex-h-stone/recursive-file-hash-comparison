package dev.alexhstone.model.datastore;

import com.google.gson.Gson;

public class HashResultSerializer {

    private final Gson gson = GsonFactory.getGsonInstance();

    public String toJson(HashResult hashResult) {
        return gson.toJson(hashResult);
    }
}
