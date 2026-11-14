package dev.tweak.apothic_infinite_exponential_tweak.util;

import com.maxwell.apothic_infnite.InfiniteConfig;
import dev.shadowsoffire.apotheosis.ench.EnchModule;
import dev.shadowsoffire.apotheosis.ench.EnchantmentInfo;
import dev.tweak.apothic_infinite_exponential_tweak.config.ExponentialTweakConfig;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Helper for computing per-enchantment level caps based on Apotheosis's default limits (+2)
 * without duplicating any Apotheosis source code.
 */
public final class EnchantmentCapHelper {

    private static final int HARD_MAX_LEVEL = 255;
    private static final int POLLUTED_THRESHOLD = 100;
    private static final ThreadLocal<Integer> RESOLVING_DEPTH = ThreadLocal.withInitial(() -> 0);

    private EnchantmentCapHelper() {
    }

    public static boolean isEnabled() {
        return ExponentialTweakConfig.SPEC.isLoaded()
                ? ExponentialTweakConfig.LIMIT_ENCHANTMENT_LEVEL_TO_APOTHEOSIS_PLUS_TWO.get()
                : ExponentialTweakConfig.LIMIT_ENCHANTMENT_LEVEL_TO_APOTHEOSIS_PLUS_TWO.getDefault();
    }

    public static boolean isAllowHighLevelCurses() {
        try {
            return InfiniteConfig.SPEC.isLoaded()
                    ? InfiniteConfig.ALLOW_HIGH_LEVEL_CURSES.get()
                    : InfiniteConfig.ALLOW_HIGH_LEVEL_CURSES.getDefault();
        } catch (Throwable t) {
            return false;
        }
    }

    public static void beginResolve() {
        RESOLVING_DEPTH.set(RESOLVING_DEPTH.get() + 1);
    }

    public static void endResolve() {
        int depth = RESOLVING_DEPTH.get();
        RESOLVING_DEPTH.set(depth > 1 ? depth - 1 : 0);
    }

    public static boolean isResolvingApotheosisInfo() {
        return RESOLVING_DEPTH.get() > 0;
    }

    public static int getApotheosisPlusTwoMaxLevel(Enchantment ench) {
        return computeCappedLevel(ench, false);
    }

    public static int getApotheosisPlusTwoMaxLootLevel(Enchantment ench) {
        return computeCappedLevel(ench, true);
    }

    private static int computeCappedLevel(Enchantment ench, boolean loot) {
        if (ench == null) {
            return 1;
        }
        if (ench.isCurse() && !isAllowHighLevelCurses()) {
            return 1;
        }
        int baseMax = resolveApotheosisBaseLevel(ench, loot);
        return Math.min(Math.max(1, baseMax + 2), HARD_MAX_LEVEL);
    }

    private static int resolveApotheosisBaseLevel(Enchantment ench, boolean loot) {
        beginResolve();
        try {
            EnchantmentInfo info = EnchModule.getEnchInfo(ench);
            int val = info != null ? (loot ? info.getMaxLootLevel() : info.getMaxLevel()) : 1;
            if (val >= POLLUTED_THRESHOLD) {
                // Recover from enchantments.cfg values previously polluted by Apothic-Infinite's 100 cap
                val = loot ? ench.getMaxLevel() : EnchModule.getDefaultMax(ench);
            }
            return val;
        } finally {
            endResolve();
        }
    }
}
