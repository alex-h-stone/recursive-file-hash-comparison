package dev.alexhstone.util;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HamcrestUtilities {

    public static Matcher<String> containsStrings(String... substrings) {
        List<Matcher<? super String>> containsEachString = Arrays.stream(substrings)
                .map(CoreMatchers::containsString)
                .collect(Collectors.toList());

        return Matchers.allOf(containsEachString);
    }
}
