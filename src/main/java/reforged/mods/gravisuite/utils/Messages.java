package reforged.mods.gravisuite.utils;

import mods.vintage.core.platform.lang.Translator;

public class Messages {

    public static String energyValue(int current, int max, int tier) {
        return Translations.ENERGY_VALUE.format(current, max, Translations.TIER.format(Translator.YELLOW.literal("" + tier)));
    }

    public enum Translations {
        // Energy value
        ENERGY_VALUE("message.info.energy", Translator.AQUA),
        TIER("message.info.energy.tier", Translator.DARK_GRAY),
        // Energy & Jetpack
        ENERGY_LEVEL("message.text.energy_level", Translator.WHITE),
        JETPACK_ENGINE("message.text.jetpack.engine", Translator.YELLOW),
        JETPACK_HOVER("message.text.jetpack.hover", Translator.YELLOW),
        BOOST_MODE("message.text.jetpack.boost.stat", Translator.YELLOW),

        // Status
        STATUS_ON("message.text.on", Translator.GREEN),
        STATUS_OFF("message.text.off", Translator.RED),
        STATUS_LOW("message.text.low_energy", Translator.RED),
        STATUS_SHUTDOWN("message.text.shutdown", Translator.RED),

        // Gravitation
        GRAVITATION_ENGINE("message.gravitation.engine", Translator.AQUA),
        GRAVITATION_LEVITATION("message.gravitation.levitation", Translator.AQUA),

        // Efficiency Tool Modes
        EFF_TOOL_MODE("message.tool.eff.mode", Translator.YELLOW),
        EFF_TOOL_MODE_NORMAL("message.tool.eff.mode.normal", Translator.BLUE),
        EFF_TOOL_MODE_LOW("message.tool.eff.mode.low_power", Translator.GREEN),
        EFF_TOOL_MODE_FINE("message.tool.eff.mode.fine", Translator.AQUA),

        // Tool Modes
        TOOL_MODE_RELOCATOR("message.tool.mode.relocator", Translator.YELLOW),
        TOOL_MODE_MAGNET("message.tool.mode.magnet", Translator.YELLOW),
        TOOL_MODE_SHEAR("message.tool.mode.shear", Translator.YELLOW),
        TOOL_MODE_CAPITATOR("message.tool.mode.capitator", Translator.YELLOW),
        TOOL_MODE("message.tool.mode", Translator.YELLOW),
        TOOL_VEIN_MODE("message.tool.mode.vein", Translator.YELLOW),

        TOOL_MODE_AOE("message.tool.aoe", Translator.YELLOW),
        TOOL_MODE_AOE_NORMAL("message.tool.aoe.normal", Translator.BLUE),
        TOOL_MODE_AOE_BIG_HOLES("message.tool.aoe.3x3", Translator.LIGHT_PURPLE),

        TOOL_MODE_VEIN_OFF("message.tool.mode.vein.off", Translator.RED),
        TOOL_MODE_VEIN_ORES("message.tool.mode.vein.ores", Translator.AQUA),
        TOOL_MODE_VEIN_EXTENDED("message.tool.mode.vein.extended", Translator.LIGHT_PURPLE),

        TOOL_MODE_SILK("message.tool.mode.silk", Translator.GREEN),
        TOOL_MODE_FORTUNE("message.tool.mode.fortune", Translator.AQUA),

        TOOL_MODE_HOE("message.tool.mode.hoe", Translator.GREEN),
        TOOL_MODE_TREETAP("message.tool.mode.treetap", Translator.GOLD),
        TOOL_MODE_WRENCH("message.tool.mode.wrench", Translator.AQUA),
        TOOL_MODE_SCREWDRIVER("message.tool.mode.screwdriver", Translator.LIGHT_PURPLE),

        TOOL_RELOCATOR_MODE("message.tool.mode.relocator", Translator.YELLOW),

        // Enchant Mode
        ENCH_MODE("message.ench.mode", Translator.YELLOW),

        // Keys
        KEY_TOGGLE_DESC("key.toggle.engine.desc", Translator.WHITE),
        KEY_MAGNET_TOGGLE_DESC("key.toggle.magnet.desc", Translator.WHITE),
        KEY_SNEAK("key.sneak", Translator.RESET),

        // Misc
        VEIN_MINER("message.vein.active", Translator.GOLD),
        QUICK_CHARGE("message.info.quick_change", Translator.GREEN),

        // Stats
        JETPACK_ENGINE_STAT(JETPACK_ENGINE.getKey() + ".stat", Translator.YELLOW),
        JETPACK_HOVER_STAT(JETPACK_HOVER.getKey() + ".stat", Translator.YELLOW),
        SHEARS_STAT(TOOL_MODE_SHEAR.getKey() + ".stat", Translator.YELLOW),
        CAPITATOR_STAT(TOOL_MODE_CAPITATOR.getKey() + ".stat", Translator.YELLOW),
        AOE_STAT(TOOL_MODE_AOE.getKey() + ".stat", Translator.YELLOW),
        EFF_MODE_STAT(EFF_TOOL_MODE.getKey() + ".stat", Translator.YELLOW),
        ENCH_MODE_STAT(ENCH_MODE.getKey() + ".stat", Translator.YELLOW),
        VEIN_MODE_STAT(TOOL_VEIN_MODE.getKey() + ".stat", Translator.YELLOW),
        MAGNET_MODE_STAT(TOOL_MODE_MAGNET.getKey() + ".stat", Translator.YELLOW),
        TOOL_MODE_STAT(TOOL_MODE.getKey() + ".stat", Translator.YELLOW),
        TOOL_RELOCATOR_STAT(TOOL_RELOCATOR_MODE.getKey() + ".stat", Translator.YELLOW),

        GRAVITATION_ENGINE_STAT(GRAVITATION_ENGINE.getKey() + ".stat", Translator.YELLOW),
        LEVITATION_STAT(GRAVITATION_LEVITATION.getKey() + ".stat", Translator.YELLOW);

        private final String key;
        private final Translator color;

        Translations(String key, Translator color) {
            this.key = key;
            this.color = color;
        }

        public String getKey() {
            return key;
        }

        public String format() {
            return color.format(key);
        }

        public String format(Object... args) {
            return color.format(key, args);
        }

        /**
         * Wraps this enum in a TooltipEntry that always uses GOLD color.
         */
        public TooltipEntry toTooltip() {
            return new TooltipEntry(this);
        }

        /**
         * Wrapper for fluent tooltip formatting.
         */
        public static class TooltipEntry {
            private final Translations base;

            private TooltipEntry(Translations base) {
                this.base = base;
            }

            public String format(Object... args) {
                return Translator.GOLD.format(base.key, args);
            }
        }
    }
}
