package dev.alexhstone.util;

import lombok.experimental.UtilityClass;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

@UtilityClass
public class PrettyPrintNumberFormatter {

    private static final String UNABLE_TO_FORMAT_NUMBER = "NaN";
    private static final Locale UK_LOCALE = Locale.UK;

    /**
     * Returns a formatted number of the form "-26,546 (-2.7x10^4)"
     *
     * @param numberToFormat Number
     * @return A number formatted for display
     */
    public static String format(Number numberToFormat) {
        if (Objects.isNull(numberToFormat)) {
            return UNABLE_TO_FORMAT_NUMBER;
        }

        String formattedNumber = toFormattedNumber(numberToFormat);
        String scientificFormattedNumber = toScientificFormattedNumber(numberToFormat);

        return formattedNumber +
                " (" + scientificFormattedNumber + ")";
    }

    private static String toScientificFormattedNumber(Number numberToFormat) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(UK_LOCALE);
        decimalFormatSymbols.setExponentSeparator("x10^");

        DecimalFormat scientificFormat = new DecimalFormat("0.#E0", decimalFormatSymbols);

        return scientificFormat.format(numberToFormat);
    }

    private static String toFormattedNumber(Number numberToFormat) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(UK_LOCALE);

        return numberFormat.format(numberToFormat);
    }
}
