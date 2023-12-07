package dev.alexhstone;

import dev.alexhstone.test.util.FileSystemUtils;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersistAsJsonToFileTest {

    @TempDir
    private Path temporaryDirectory;
    private PersistAsJsonToFile persistAsJsonToFile;

    @BeforeEach
    void setUp() {
        persistAsJsonToFile = new PersistAsJsonToFile(temporaryDirectory);
    }

    @Test
    void shouldFailIfFileAlreadyExists() {
        String fileName = "fileNameToPersistTo.json";
        new FileSystemUtils(temporaryDirectory).createFileWithContent(fileName, "File contents");
        Person person = new Person("Some One", 33);

        IllegalArgumentException expectedException = assertThrows(IllegalArgumentException.class,
                () -> persistAsJsonToFile.persist(person, fileName));

        String exceptionMessage = expectedException.getMessage();
        assertThat(exceptionMessage, containsString("Unable to persist to"));
        assertThat(exceptionMessage, containsString("as the file already exists"));
    }

    @Test
    void successfullyPersistToAFile() throws IOException, JSONException {
        String fileName = "fileNameToPersistTo.json";
        persistAsJsonToFile.persist(new Person("Some One", 33), fileName);

        String personAsJson = Files.readString(temporaryDirectory.resolve(fileName));
        assertThat(personAsJson, Matchers.not(Matchers.isEmptyOrNullString()));
        JSONAssert.assertEquals("{\"name\": \"Some One\", " +
                "\"age\": 33}", personAsJson, JSONCompareMode.NON_EXTENSIBLE);
    }

    private record Person(String name, Integer age) {
    }
}