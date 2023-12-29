package dev.alexhstone.model.datastore;

import com.google.gson.Gson;

public class HashResultDeserializer {

    private final Gson gson = GsonFactory.getGsonInstance();

    public HashResult fromJson(String hashResultJson) {
        return gson.fromJson(hashResultJson, HashResult.class);
    }
}
