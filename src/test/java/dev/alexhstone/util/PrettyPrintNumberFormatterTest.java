package dev.alexhstone.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class PrettyPrintNumberFormatterTest {

    @ParameterizedTest
    @MethodSource("numberShouldBeFormattedToExpectedProvider")
    void numberShouldBeFormattedToExpected(Number numberToFormat,
                                           String expectedPrettyPrintFormattedNumber) {
        String actualFormattedNumber = PrettyPrintNumberFormatter.format(numberToFormat);

        assertThat(actualFormattedNumber, equalTo(expectedPrettyPrintFormattedNumber));
    }

    static Stream<Arguments> numberShouldBeFormattedToExpectedProvider() {
        return Stream.of(
                Arguments.of(20, "20 (2x10^1)"),
                Arguments.of(-16, "-16 (-1.6x10^1)"),
                Arguments.of(0, "0 (0x10^0)"),
                Arguments.of(1000, "1,000 (1x10^3)"),
                Arguments.of(-26546, "-26,546 (-2.7x10^4)"),
                Arguments.of(1234567890123456789L, "1,234,567,890,123,456,789 (1.2x10^18)"),
                Arguments.of(-5245.698, "-5,245.698 (-5.2x10^3)")
        );
    }

    @Test
    void shouldReturnNaNStringWhenPrettyPrintingANullNumber() {
        String actualFormattedNumber = PrettyPrintNumberFormatter.format(null);

        assertThat(actualFormattedNumber, equalTo("NaN"));
    }
}