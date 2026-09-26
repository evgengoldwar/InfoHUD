package com.gtnewhorizons.infohud.hud.layout;

import java.util.UUID;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.gtnewhorizons.infohud.hud.tags.LineTemplate;

public class HudLine {

    public String id;
    public String defaultId;
    public String template = "";
    public String icon = "";
    public boolean enabled = true;
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
