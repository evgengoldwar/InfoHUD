package com.gtnewhorizons.infohud.hud.tags;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

final class Filters {

    private static final double[] SHORT_STEPS = { 1e12, 1e9, 1e6, 1e3 };
    private static final String[] SHORT_SUFFIXES = { "T", "B", "M", "K" };

    private Filters() {}

    static String apply(String value, String filter) {
        String name = filter.trim();
        String argument = null;
        int colon = name.indexOf(':');
        if (colon >= 0) {
            argument = name.substring(colon + 1)
                .trim();
            name = name.substring(0, colon)
                .trim();
        }

        switch (name.toLowerCase(Locale.ROOT)) {
            case "round": {
                Double number = LineTemplate.parseNumber(value);
                if (number == null) return value;
                return round(number, intArgument(argument, 0));
            }
            case "short": {
                Double number = LineTemplate.parseNumber(value);
                if (number == null) return value;
                return shorten(number, intArgument(argument, 1));
            }
            case "pad":
                return spacer(value, intArgument(argument, 0)) + value;
            case "padr":
                return value + spacer(value, intArgument(argument, 0));
            case "upper":
                return changeCase(value, true);
            case "lower":
                return changeCase(value, false);
            default:
                throw new Expressions.EvalException(true);
        }
    }

    private static int intArgument(String argument, int fallback) {
        if (argument == null || argument.isEmpty()) return fallback;
        try {
            return Math.max(0, Math.min(10, Integer.parseInt(argument)));
        } catch (NumberFormatException e) {
            throw new Expressions.EvalException(true);
        }
    }

    static String round(double value, int decimals) {
        return BigDecimal.valueOf(value)
            .setScale(decimals, RoundingMode.HALF_UP)
            .toPlainString();
    }

    private static String shorten(double value, int decimals) {
        for (int i = 0; i < SHORT_STEPS.length; i++) {
            if (Math.abs(value) >= SHORT_STEPS[i]) {
                return trimZeros(round(value / SHORT_STEPS[i], decimals)) + SHORT_SUFFIXES[i];
            }
        }
        return LineTemplate.formatNumber(value);
    }

    private static String trimZeros(String number) {
        if (number.indexOf('.') < 0) return number;
        number = number.replaceAll("0+$", "");
        return number.endsWith(".") ? number.substring(0, number.length() - 1) : number;
    }

    private static String changeCase(String value, boolean upper) {
        StringBuilder sb = new StringBuilder(value.length());
        int i = 0;
        while (i < value.length()) {
            int start = value.indexOf(LineTemplate.ICON_START, i);
            if (start < 0) start = value.length();
            String text = value.substring(i, start);
            sb.append(upper ? text.toUpperCase() : text.toLowerCase());
            if (start >= value.length()) break;
            int end = value.indexOf(LineTemplate.ICON_END, start);
            if (end < 0) end = value.length() - 1;
            sb.append(value, start, end + 1);
            i = end + 1;
        }
        return sb.toString();
    }

    private static String spacer(String value, int width) {
        int missing = width - LineTemplate.stripFormatting(value)
            .length();
        if (missing <= 0) return "";
        return LineTemplate.ICON_START + LineTemplate.SPACER_PREFIX + missing + LineTemplate.ICON_END;
    }
}
