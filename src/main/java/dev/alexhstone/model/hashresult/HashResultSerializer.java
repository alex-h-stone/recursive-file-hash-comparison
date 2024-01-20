package dev.alexhstone.model.hashresult;

import com.google.gson.Gson;
import dev.alexhstone.model.GsonFactory;

public class HashResultSerializer {

    private final Gson gson = GsonFactory.getGsonInstance();

    public String toJson(HashResult hashResult) {
        return gson.toJson(hashResult);
    }
}
