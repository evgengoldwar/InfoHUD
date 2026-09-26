package com.gtnewhorizons.infohud.hud.tags;

public final class LineTemplate {

    public static final String FORMAT_CODES = "0123456789abcdefklmnor";
    public static final String ICON_PREFIX = "icon:";
    public static final char ICON_START = '\uE000';
    public static final char ICON_END = '\uE001';
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

                    InfoTag tag = TagRegistry.get(name);

                    if (tag == null) {
                        if (!preview) return null;
                        sb.append("§c{")
                            .append(name)
                            .append("}§r");
                    } else {
                        String value = tag.getValue();
                        if (value == null) {
                            if (!preview) return null;
                            if (!tag.condition) {
                                sb.append("§8{")
                                    .append(name)
                                    .append("}§r");
                            }
                        } else {
                            sb.append(value);
                        }
                    }

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
