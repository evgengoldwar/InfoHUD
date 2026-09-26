package com.gtnewhorizons.infohud.hud.tags;

import java.util.ArrayList;
import java.util.List;

final class Conditions {

    private static final String[] OPERATORS = { "<=", ">=", "==", "!=", "<", ">" };

    private Conditions() {}

    static boolean test(String condition) {
        String text = condition.trim();

        List<String> any = split(text, " or ");
        if (any.size() > 1) {
            for (String part : any) {
                if (test(part)) return true;
            }
            return false;
        }

        List<String> all = split(text, " and ");
        if (all.size() > 1) {
            for (String part : all) {
                if (!test(part)) return false;
            }
            return true;
        }

        if (text.startsWith("not ")) {
            return !test(text.substring(4));
        }

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{' || c == '[') {
                int end = text.indexOf(c == '{' ? '}' : ']', i);
                if (end < 0) break;
                i = end;
                continue;
            }
            for (String operator : OPERATORS) {
                if (text.startsWith(operator, i)) {
                    return compare(
                        value(text.substring(0, i)),
                        value(text.substring(i + operator.length())),
                        operator);
                }
            }
        }

        return truthy(text);
    }

    private static List<String> split(String text, String keyword) {
        List<String> parts = new ArrayList<>();
        int from = 0;
        int index;
        while ((index = LineTemplate.findKeyword(text, keyword, from)) >= 0) {
            parts.add(text.substring(from, index));
            from = index + keyword.length();
        }
        parts.add(text.substring(from));
        return parts;
    }

    private static String value(String operand) {
        String text = operand.trim();
        if (text.length() >= 2 && (text.startsWith("\"") && text.endsWith("\"")
            || text.startsWith("'") && text.endsWith("'"))) {
            return text.substring(1, text.length() - 1);
        }
        return LineTemplate.render(text);
    }

    private static boolean truthy(String operand) {
        String text = operand.trim();

        if (text.startsWith("{") && text.endsWith("}") && text.indexOf('}') == text.length() - 1) {
            InfoTag tag = TagRegistry.get(
                text.substring(1, text.length() - 1)
                    .trim());
            if (tag != null && tag.condition) {
                return tag.getValue() != null;
            }
        }

        String value = value(text);
        if (value == null) return false;
        String plain = stripFormatting(value).trim();
        return !plain.isEmpty() && !plain.equals("0") && !plain.equalsIgnoreCase("false");
    }

    private static boolean compare(String left, String right, String operator) {
        if (left == null || right == null) return false;

        Double a = parseNumber(left);
        Double b = parseNumber(right);
        int result;

        if (a != null && b != null) {
            result = Double.compare(a, b);
        } else {
            result = stripFormatting(left).trim()
                .compareToIgnoreCase(
                    stripFormatting(right).trim());
        }

        return switch (operator) {
            case "<=" -> result <= 0;
            case ">=" -> result >= 0;
            case "==" -> result == 0;
            case "!=" -> result != 0;
            case "<" -> result < 0;
            default -> result > 0;
        };
    }

    private static Double parseNumber(String value) {
        String plain = stripFormatting(value).replace(",", "")
            .replace("%", "")
            .replace(" ", "")
            .replace(" ", "");
        if (plain.isEmpty()) return null;
        try {
            return Double.parseDouble(plain);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static String stripFormatting(String value) {
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '§' && i + 1 < value.length()) {
                i++;
                continue;
            }
            if (c == LineTemplate.ICON_START) {
                int end = value.indexOf(LineTemplate.ICON_END, i);
                if (end < 0) break;
                i = end;
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
