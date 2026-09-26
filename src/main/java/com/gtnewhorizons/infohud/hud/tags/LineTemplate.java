package com.gtnewhorizons.infohud.hud.tags;

/**
 * Turns a user written line template into the final HUD text.
 * <ul>
 * <li>{@code {tag}} is replaced by the value of the tag</li>
 * <li>{@code &x} (x = 0-9, a-f, k-o, r) is replaced by the Minecraft formatting code {@code §x}</li>
 * </ul>
 */
public final class LineTemplate {

    public static final String FORMAT_CODES = "0123456789abcdefklmnor";

    private LineTemplate() {}

    /**
     * @return the rendered line, or {@code null} if it uses an unknown or currently unavailable tag
     */
    public static String render(String template) {
        return render(template, false);
    }

    /**
     * Preview mode never returns {@code null}: unknown tags are shown in red, unavailable tags in gray.
     */
    public static String renderPreview(String template) {
        return render(template, true);
    }

    private static String render(String template, boolean preview) {
        if (template == null) return preview ? "" : null;

        StringBuilder sb = new StringBuilder(template.length() + 16);
        int length = template.length();
        int i = 0;

        while (i < length) {
            char c = template.charAt(i);

            if (c == '&' && i + 1 < length) {
                char code = Character.toLowerCase(template.charAt(i + 1));
                if (FORMAT_CODES.indexOf(code) >= 0) {
                    sb.append('§')
                        .append(code);
                    i += 2;
                    continue;
                }
            }

            if (c == '{') {
                int end = template.indexOf('}', i + 1);
                if (end > i + 1) {
                    String name = template.substring(i + 1, end)
                        .trim();
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
