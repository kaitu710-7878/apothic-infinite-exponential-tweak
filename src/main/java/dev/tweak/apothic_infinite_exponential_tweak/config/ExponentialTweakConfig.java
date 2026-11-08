package dev.tweak.apothic_infinite_exponential_tweak.config;

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
    public static final ForgeConfigSpec.BooleanValue RESET_COUNT_ON_RARITY_CHANGE;
    public static final ForgeConfigSpec.BooleanValue SCALE_AUGMENT_COST;
    public static final ForgeConfigSpec.DoubleValue AUGMENT_BASE_EXPONENT;
    public static final ForgeConfigSpec.BooleanValue SHOW_REFORGE_COUNT_TOOLTIP;
    public static final ForgeConfigSpec.BooleanValue SHOW_AUGMENT_TOOLTIP;

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

        RESET_COUNT_ON_RARITY_CHANGE = BUILDER.comment(
                        "If true, reforging with a different rarity tier resets the reforge count.",
                        "If false (default), reforge counts are tracked and preserved separately per rarity tier.")
                .define("reset_count_on_rarity_change", false);
        BUILDER.pop();

        BUILDER.push("Augmenting Table Scaling");
        SCALE_AUGMENT_COST = BUILDER.comment(
                        "If true, upgrades beyond the default max level (1.0F) at the Augmenting Table",
                        "will exponentially increase the required Sigils of Enhancement.")
                .define("scale_augment_cost", true);

        AUGMENT_BASE_EXPONENT = BUILDER.comment(
                        "The exponential multiplier for Augmenting Table upgrades beyond 1.0F.",
                        "Formula: cost = 2 * (base ^ (over_level_count + 1)), e.g. 4, 8, 16, 32, 64...",
                        "Default is 2.0 (doubles each upgrade).")
                .defineInRange("augment_base_exponent", 2.0, 1.0, 10.0);
        BUILDER.pop();

        BUILDER.push("Display & Tooltips");
        SHOW_REFORGE_COUNT_TOOLTIP = BUILDER.comment(
                        "If true, displays the reforge count and next cost multiplier on item tooltips.")
                .define("show_reforge_count_tooltip", true);

        SHOW_AUGMENT_TOOLTIP = BUILDER.comment(
                        "If true, displays the over-level augment upgrade count on item tooltips.")
                .define("show_augment_tooltip", true);
        BUILDER.pop();

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}

