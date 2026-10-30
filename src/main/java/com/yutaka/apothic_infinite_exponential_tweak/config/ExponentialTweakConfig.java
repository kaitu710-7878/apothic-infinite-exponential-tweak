package com.yutaka.apothic_infinite_exponential_tweak.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ExponentialTweakConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue OVERRIDE_MAX_AFFIX_LEVEL_TO_MAX;
    public static final ForgeConfigSpec.DoubleValue CUSTOM_MAX_AFFIX_LEVEL;

    public static final ForgeConfigSpec.DoubleValue BASE_EXPONENT;
    public static final ForgeConfigSpec.BooleanValue SCALE_SIGIL_COST;
    public static final ForgeConfigSpec.BooleanValue SCALE_MATERIAL_COST;
    public static final ForgeConfigSpec.IntValue MAX_COST_CAP;
    public static final ForgeConfigSpec.BooleanValue ENABLE_INVENTORY_CONSUMPTION;
    public static final ForgeConfigSpec.BooleanValue RESET_COUNT_ON_RARITY_CHANGE;
    public static final ForgeConfigSpec.BooleanValue SHOW_REFORGE_COUNT_TOOLTIP;

    static {
        BUILDER.comment("==================================================",
                " Apothic Infinite Exponential Tweak Configuration ",
                "==================================================",
                "").push("General");

        BUILDER.push("Infinite Reforging Limit");
        OVERRIDE_MAX_AFFIX_LEVEL_TO_MAX = BUILDER.comment(
                        "If true, overrides Apothic-Infinite's maximum duplicate reforge limit (max_affix_level)",
                        "to the maximum possible value, enabling truly unlimited reforge stacking by default.")
                .define("override_max_affix_level_to_max", true);

        CUSTOM_MAX_AFFIX_LEVEL = BUILDER.comment(
                        "The maximum affix level multiplier to use if override_max_affix_level_to_max is true.",
                        "Default is 1.0E300 (practically infinite).")
                .defineInRange("custom_max_affix_level", 1.0E300, 1.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Exponential Cost Scaling");
        BASE_EXPONENT = BUILDER.comment(
                        "The base of the exponential cost calculation.",
                        "Formula: cost_multiplier = base^(reforge_count - 1)",
                        "Default is 2.0 (1st reforge: 1x, 2nd: 2x, 3rd: 4x, 4th: 8x, etc.)")
                .defineInRange("base_exponent", 2.0, 1.0, 10.0);

        SCALE_SIGIL_COST = BUILDER.comment(
                        "If true, the required Sigils of Reforging (Sigil of Rebirth) will scale exponentially on repeated reforges.")
                .define("scale_sigil_cost", true);

        SCALE_MATERIAL_COST = BUILDER.comment(
                        "If true, the required rarity materials will scale exponentially on repeated reforges.")
                .define("scale_material_cost", true);

        MAX_COST_CAP = BUILDER.comment(
                        "The maximum item cost cap per reforge operation to prevent unintended overflow.",
                        "Default is 64000.")
                .defineInRange("max_cost_cap", 64000, 1, Integer.MAX_VALUE);

        ENABLE_INVENTORY_CONSUMPTION = BUILDER.comment(
                        "If true, when required material or sigil count exceeds the reforge slot stack size (e.g. > 64),",
                        "the remaining items will be consumed automatically from the player's inventory.")
                .define("enable_inventory_consumption", true);

        RESET_COUNT_ON_RARITY_CHANGE = BUILDER.comment(
                        "If true, reforging with a different rarity tier resets the reforge count.",
                        "If false (default), reforge counts are tracked and preserved separately per rarity tier.")
                .define("reset_count_on_rarity_change", false);
        BUILDER.pop();

        BUILDER.push("Display & Tooltips");
        SHOW_REFORGE_COUNT_TOOLTIP = BUILDER.comment(
                        "If true, displays the reforge count and next cost multiplier on item tooltips.")
                .define("show_reforge_count_tooltip", true);
        BUILDER.pop();

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
