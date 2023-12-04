package dev.alexhstone;


import dev.alexhstone.model.DiffResults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {
        Application application = new Application("F:\\tmp",
                "F:\\tmp",
                "F:");

        DiffResults diffResults = application.execute();
        System.out.println("diffResults: " + diffResults);
    }
}