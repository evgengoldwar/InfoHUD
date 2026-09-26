package com.gtnewhorizons.infohud.hud.layout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Complete HUD configuration edited through the HUD editor GUI.
 */
public class HudLayout {

    public static final float MIN_SCALE = 0.5F;
    public static final float MAX_SCALE = 2.0F;
    public static final int DEFAULT_GROUP_X = 0;
    /** Below the potion effects and their label, so the labels do not overlap in the editor. */
    public static final int DEFAULT_GROUP_Y = 56;
    private static final int LEGACY_GROUP_Y = 40;
    private static final int CURRENT_VERSION = 2;
    public static final int DEFAULT_POTION_X = 5;
    public static final int DEFAULT_POTION_Y = 2;

    public int version = CURRENT_VERSION;
    public boolean hudDisabled = false;
    public float scale = 1.0F;

    public boolean groupEnabled = true;
    public int groupX = DEFAULT_GROUP_X;
    public int groupY = DEFAULT_GROUP_Y;

    public boolean potionsEnabled = true;
    public boolean potionTime = true;
    public boolean potionLevel = true;
    public int potionX = DEFAULT_POTION_X;
    public int potionY = DEFAULT_POTION_Y;

    public boolean countItemEnabled = true;
    /** -1 means "next to the hotbar". */
    public int countItemX = -1;
    public int countItemY = -1;

    /** All lines. The order of the grouped lines in this list is their order inside the group. */
    public List<HudLine> lines = new ArrayList<>();

    public static HudLayout createDefault() {
        HudLayout layout = new HudLayout();
        for (DefaultLines.Preset preset : DefaultLines.available()) {
            layout.lines.add(preset.create());
        }
        return layout;
    }

    public HudLayout copy() {
        HudLayout copy = new HudLayout();
        copy.version = version;
        copy.hudDisabled = hudDisabled;
        copy.scale = scale;
        copy.groupEnabled = groupEnabled;
        copy.groupX = groupX;
        copy.groupY = groupY;
        copy.potionsEnabled = potionsEnabled;
        copy.potionTime = potionTime;
        copy.potionLevel = potionLevel;
        copy.potionX = potionX;
        copy.potionY = potionY;
        copy.countItemEnabled = countItemEnabled;
        copy.countItemX = countItemX;
        copy.countItemY = countItemY;
        for (HudLine line : lines) {
            copy.lines.add(line.copy());
        }
        return copy;
    }

    /**
     * Repairs data loaded from a hand edited or outdated file.
     */
    public HudLayout sanitize() {
        if (lines == null) {
            lines = new ArrayList<>();
        }

        Set<String> ids = new HashSet<>();
        Iterator<HudLine> it = lines.iterator();
        while (it.hasNext()) {
            HudLine line = it.next();
            if (line == null) {
                it.remove();
                continue;
            }
            if (line.id == null || line.id.isEmpty() || !ids.add(line.id)) {
                line.id = HudLine.newId();
                ids.add(line.id);
            }
            if (line.template == null) line.template = "";
            if (line.icon == null) line.icon = "";
        }

        if (version < 2 && groupX == DEFAULT_GROUP_X && groupY == LEGACY_GROUP_Y) {
            // the group was never moved: shift it to the new default position
            groupY = DEFAULT_GROUP_Y;
        }
        version = CURRENT_VERSION;

        setScale(scale);
        return this;
    }

    public void setScale(float value) {
        if (Float.isNaN(value)) value = 1.0F;
        value = Math.round(value * 10) / 10.0F;
        scale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, value));
    }

    public List<HudLine> getGroupLines() {
        List<HudLine> result = new ArrayList<>();
        for (HudLine line : lines) {
            if (line.inGroup) result.add(line);
        }
        return result;
    }

    public List<HudLine> getDetachedLines() {
        List<HudLine> result = new ArrayList<>();
        for (HudLine line : lines) {
            if (!line.inGroup) result.add(line);
        }
        return result;
    }

    public HudLine findById(String id) {
        for (HudLine line : lines) {
            if (line.id.equals(id)) return line;
        }
        return null;
    }

    public boolean hasDefault(String defaultId) {
        for (HudLine line : lines) {
            if (defaultId.equals(line.defaultId)) return true;
        }
        return false;
    }

    /** @return 1-based position of the line inside the group, 0 if it is not grouped */
    public int getGroupPosition(HudLine line) {
        return getGroupLines().indexOf(line) + 1;
    }

    /**
     * Moves a grouped line up (negative delta) or down (positive delta) inside the group.
     */
    public boolean moveInGroup(HudLine line, int delta) {
        List<HudLine> group = getGroupLines();
        int index = group.indexOf(line);
        int target = index + delta;
        if (index < 0 || target < 0 || target >= group.size()) return false;

        HudLine other = group.get(target);
        int a = lines.indexOf(line);
        int b = lines.indexOf(other);
        lines.set(a, other);
        lines.set(b, line);
        return true;
    }

    /**
     * Puts a line back into the group at the given group position (clamped).
     */
    public void attachToGroup(HudLine line, int groupIndex) {
        lines.remove(line);
        line.inGroup = true;

        List<HudLine> group = getGroupLines();
        if (groupIndex < 0 || groupIndex >= group.size()) {
            if (group.isEmpty()) {
                lines.add(line);
            } else {
                lines.add(lines.indexOf(group.get(group.size() - 1)) + 1, line);
            }
        } else {
            lines.add(lines.indexOf(group.get(groupIndex)), line);
        }
    }

    public void detach(HudLine line, int x, int y) {
        line.inGroup = false;
        line.x = x;
        line.y = y;
    }
}
