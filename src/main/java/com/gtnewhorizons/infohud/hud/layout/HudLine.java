package com.gtnewhorizons.infohud.hud.layout;

import java.util.UUID;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.gtnewhorizons.infohud.hud.tags.LineTemplate;

/**
 * One configurable HUD line. Stored in {@code config/InfoHUD/Layout.json}.
 */
public class HudLine {

    /** Unique id of this line. */
    public String id;
    /** Id of the default line this one was created from, {@code null} for user lines. */
    public String defaultId;
    /** Text of the line with {tags} and &amp;color codes. */
    public String template = "";
    /** Item used as icon, e.g. {@code minecraft:emerald} or {@code minecraft:golden_apple/1}. Empty = no icon. */
    public String icon = "";
    public boolean enabled = true;
    /** {@code true} if the line is part of the group, otherwise it is placed freely at {@link #x}/{@link #y}. */
    public boolean inGroup = true;
    public int x;
    public int y;

    private transient String cachedIconName;
    private transient ItemStack cachedIcon;

    public HudLine() {}

    public HudLine(String defaultId, String template, String icon) {
        this.id = newId();
        this.defaultId = defaultId;
        this.template = template;
        this.icon = icon;
    }

    public static String newId() {
        return UUID.randomUUID()
            .toString()
            .substring(0, 8);
    }

    public HudLine copy() {
        HudLine line = new HudLine();
        line.id = id;
        line.defaultId = defaultId;
        line.template = template;
        line.icon = icon;
        line.enabled = enabled;
        line.inGroup = inGroup;
        line.x = x;
        line.y = y;
        return line;
    }

    public boolean isDefault() {
        return defaultId != null;
    }

    /**
     * @return text for the HUD or {@code null} if the line should not be shown right now
     */
    public String render() {
        return LineTemplate.render(template);
    }

    public String renderPreview() {
        return LineTemplate.renderPreview(template);
    }

    public ItemStack getIconStack() {
        String name = icon == null ? "" : icon.trim();

        if (name.equals(cachedIconName)) {
            return cachedIcon;
        }

        cachedIconName = name;
        cachedIcon = parseItem(name);
        return cachedIcon;
    }

    public static ItemStack parseItem(String name) {
        if (name == null || name.isEmpty()) return null;

        try {
            String[] parts = name.split("/");
            int meta = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 0;
            Object item = Item.itemRegistry.getObject(parts[0].trim());
            return item instanceof Item ? new ItemStack((Item) item, 1, meta) : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    public static String itemToString(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return "";
        Object name = Item.itemRegistry.getNameForObject(stack.getItem());
        if (name == null) return "";
        int meta = stack.getItemDamage();
        return meta != 0 ? name + "/" + meta : String.valueOf(name);
    }
}
