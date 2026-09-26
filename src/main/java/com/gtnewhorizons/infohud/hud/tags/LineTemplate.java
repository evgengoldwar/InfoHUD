package com.gtnewhorizons.infohud.hud.tags;

import java.util.regex.Pattern;

public final class LineTemplate {

    public static final String FORMAT_CODES = "0123456789abcdefklmnor";
    public static final String ICON_PREFIX = "icon:";
    public static final char ICON_START = '\uE000';
    public static final char ICON_END = '\uE001';
    public static final String SPACER_PREFIX = "#";
    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");
    static final String IF = " if ";
    static final String ELSE = " else ";

    private LineTemplate() {}

    public static String iconTag(String item) {
        return "{" + ICON_PREFIX + item + "}";
    }

    public static String render(String template) {
        return render(template, false);
    }

    public static String renderPreview(String template) {
        return render(template, true);
    }

    private static String render(String template, boolean preview) {
        if (template == null) return preview ? "" : null;
        return evaluate(template, preview);
    }

    private static String evaluate(String text, boolean preview) {
        int ifPos = findKeyword(text, IF, 0);
        if (ifPos < 0) return renderPlain(text, preview);

        String whenTrue = text.substring(0, ifPos);
        String rest = text.substring(ifPos + IF.length());
        int elsePos = findKeyword(rest, ELSE, 0);
        String condition = elsePos < 0 ? rest : rest.substring(0, elsePos);
        String whenFalse = elsePos < 0 ? "" : rest.substring(elsePos + ELSE.length());

        return evaluate(Conditions.test(condition) ? whenTrue : whenFalse, preview);
    }

    static int findKeyword(String text, String keyword, int from) {
        int depth = 0;
        for (int i = from; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{' || c == '[') {
                depth++;
            } else if ((c == '}' || c == ']') && depth > 0) {
                depth--;
            } else if (depth == 0 && text.startsWith(keyword, i)) {
                return i;
            }
        }
        return -1;
    }

    private static int findClosing(String text, int start, char open, char close) {
        int depth = 0;
        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == open) {
                depth++;
            } else if (c == close && --depth == 0) {
                return i;
            }
        }
        return -1;
    }

    private static boolean appendTag(StringBuilder sb, String content, boolean preview) {
        String[] parts = content.split("\\|");
        String expression = parts[0].trim();
        String value;

        try {
            if (IDENTIFIER.matcher(expression)
                .matches()) {
                InfoTag tag = TagRegistry.get(expression);
                if (tag == null) throw new Expressions.EvalException(true);

                value = tag.getValue();
                if (value == null) {
                    if (!preview) return false;
                    if (!tag.condition) {
                        sb.append("§8{")
                            .append(content)
                            .append("}§r");
                    }
                    return true;
                }
            } else {
                value = formatNumber(Expressions.evaluate(expression));
            }

            for (int i = 1; i < parts.length; i++) {
                value = Filters.apply(value, parts[i]);
            }
        } catch (Expressions.EvalException e) {
            if (!preview) return false;
            sb.append(e.unknown ? "§c{" : "§8{")
                .append(content)
                .append("}§r");
            return true;
        }

        sb.append(value);
        return true;
    }

    static String formatNumber(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) throw new Expressions.EvalException(false);
        if (value == Math.rint(value) && Math.abs(value) < 1e15) return String.valueOf((long) value);
        String rounded = Filters.round(value, 2);
        rounded = rounded.replaceAll("0+$", "");
        return rounded.endsWith(".") ? rounded.substring(0, rounded.length() - 1) : rounded;
    }

    static Double parseNumber(String value) {
        String plain = stripFormatting(value).replace(",", "")
            .replace("%", "")
            .replace("\u00a0", "")
            .replace("\u202f", "")
            .replace(" ", "");
        if (plain.isEmpty()) return null;
        try {
            return Double.parseDouble(plain);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    static String stripFormatting(String value) {
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '§' && i + 1 < value.length()) {
                i++;
                continue;
            }
            if (c == ICON_START) {
                int end = value.indexOf(ICON_END, i);
                if (end < 0) break;
                i = end;
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static String renderPlain(String text, boolean preview) {
        StringBuilder sb = new StringBuilder(text.length() + 16);
        int length = text.length();
        int i = 0;

        while (i < length) {
            char c = text.charAt(i);

            if (c == '&' && i + 1 < length) {
                char code = Character.toLowerCase(text.charAt(i + 1));
                if (FORMAT_CODES.indexOf(code) >= 0) {
                    sb.append('§')
                        .append(code);
                    i += 2;
                    continue;
                }
            }

            if (c == '[') {
                int end = findClosing(text, i, '[', ']');
                if (end > i) {
                    String inner = text.substring(i + 1, end);
                    if (findKeyword(inner, IF, 0) >= 0) {
                        String value = evaluate(inner, preview);
                        if (value == null) {
                            if (!preview) return null;
                            value = "";
                        }
                        sb.append(value);
                        i = end + 1;
                        continue;
                    }
                }
            }

            if (c == '{') {
                int end = text.indexOf('}', i + 1);
                if (end > i + 1) {
                    String name = text.substring(i + 1, end)
                        .trim();

                    if (name.regionMatches(true, 0, ICON_PREFIX, 0, ICON_PREFIX.length())) {
                        sb.append(ICON_START)
                            .append(
                                name.substring(ICON_PREFIX.length())
                                    .trim())
                            .append(ICON_END);
                        i = end + 1;
                        continue;
                    }

                    if (!appendTag(sb, name, preview)) return null;
                    i = end + 1;
                    continue;
                }
            }

            sb.append(c);
            i++;
        }

        return sb.toString();
    }
}
