package dev.tweak.apothic_infinite_exponential_tweak.util;

import dev.tweak.apothic_infinite_exponential_tweak.config.ExponentialTweakConfig;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ExponentialCostHelper {
    public static final String TAG_ROOT = "ApothicExponential";
    public static final String TAG_CURRENT_COUNT = "apo_exp_current_count";
    public static final String TAG_CURRENT_RARITY = "apo_exp_current_rarity";
    public static final String TAG_RARITY_PREFIX = "apo_exp_count_";
    public static final String TAG_RARITY_COUNTS = "RarityCounts";

    /**
     * Normalizes a rarity ID string (e.g. "common" -> "apotheosis:common").
     */
    public static String normalizeRarityId(String id) {
        if (id == null || id.isEmpty()) return "";
        id = id.trim().toLowerCase();
        if (!id.contains(":")) {
            return "apotheosis:" + id;
        }
        return id;
    }

    /**
     * Resolves the normalized rarity ID for a LootRarity instance.
     */
    public static String getRarityId(LootRarity rarity) {
        if (rarity == null) return "";
        try {
            DynamicHolder<LootRarity> holder = RarityRegistry.INSTANCE.holder(rarity);
            if (holder != null && holder.isBound()) {
                return normalizeRarityId(holder.getId().toString());
            }
        } catch (Throwable ignored) {
        }
        ResourceLocation key = RarityRegistry.INSTANCE.getKey(rarity);
        if (key != null) {
            return normalizeRarityId(key.toString());
        }
        return "";
    }

    /**
     * Directly extracts the rarity ID of an ItemStack from affix_data NBT.
     */
    public static String getItemRarityId(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return "";

        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains(AffixHelper.AFFIX_DATA)) {
                CompoundTag afxData = tag.getCompound(AffixHelper.AFFIX_DATA);
                if (afxData.contains(AffixHelper.RARITY)) {
                    String r = afxData.getString(AffixHelper.RARITY);
                    if (!r.isEmpty()) {
                        return normalizeRarityId(r);
                    }
                }
            }
        }

        try {
            DynamicHolder<LootRarity> holder = AffixHelper.getRarity(stack);
            if (holder != null && holder.isBound()) {
                return normalizeRarityId(holder.getId().toString());
            }
        } catch (Throwable ignored) {
        }

        return "";
    }

    /**
     * Checks if an item currently has the same rarity tier as targetRarity.
     */
    public static boolean isSameRarity(ItemStack stack, LootRarity targetRarity) {
        if (stack == null || stack.isEmpty() || targetRarity == null) return false;

        String itemRarity = getItemRarityId(stack);
        String targetId = getRarityId(targetRarity);

        if (!itemRarity.isEmpty() && !targetId.isEmpty()) {
            if (itemRarity.equals(targetId)) return true;
        }

        try {
            DynamicHolder<LootRarity> currentHolder = AffixHelper.getRarity(stack);
            if (currentHolder != null && currentHolder.isBound()) {
                LootRarity current = currentHolder.get();
                if (current != null) {
                    if (current == targetRarity || current.ordinal() == targetRarity.ordinal()) {
                        return true;
                    }
                    if (current.getMaterial() != null && targetRarity.getMaterial() != null) {
                        if (current.getMaterial() == targetRarity.getMaterial()) {
                            return true;
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        return false;
    }

    /**
     * Gets how many times this item has already been reforged at the given target rarity tier.
     */
    public static int getReforgeCountForRarity(ItemStack stack, LootRarity rarity) {
        if (stack == null || stack.isEmpty() || rarity == null) return 0;

        String targetRarityId = getRarityId(rarity);
        boolean matchesRarity = isSameRarity(stack, rarity);

        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                // 1. Direct per-rarity tag inside affix_data
                if (tag.contains(AffixHelper.AFFIX_DATA)) {
                    CompoundTag afxData = tag.getCompound(AffixHelper.AFFIX_DATA);
                    if (!targetRarityId.isEmpty() && afxData.contains(TAG_RARITY_PREFIX + targetRarityId)) {
                        return afxData.getInt(TAG_RARITY_PREFIX + targetRarityId);
                    }
                    if (matchesRarity && afxData.contains(TAG_CURRENT_COUNT)) {
                        return afxData.getInt(TAG_CURRENT_COUNT);
                    }
                }

                // 2. Direct per-rarity tag inside ApothicExponential
                if (tag.contains(TAG_ROOT)) {
                    CompoundTag expTag = tag.getCompound(TAG_ROOT);
                    if (expTag.contains(TAG_RARITY_COUNTS)) {
                        CompoundTag counts = expTag.getCompound(TAG_RARITY_COUNTS);
                        if (!targetRarityId.isEmpty() && counts.contains(targetRarityId)) {
                            return counts.getInt(targetRarityId);
                        }
                    }
                    if (matchesRarity && expTag.contains(TAG_CURRENT_COUNT)) {
                        return expTag.getInt(TAG_CURRENT_COUNT);
                    }
                }
            }
        }

        // Fallback: If item currently has this rarity tier but has no count recorded,
        // it has already been reforged at least 1 time!
        if (matchesRarity) {
            // Count existing affixes as lower-bound heuristic if available
            try {
                int afxCount = AffixHelper.getAffixes(stack).size();
                return Math.max(1, afxCount);
            } catch (Throwable ignored) {
                return 1;
            }
        }

        return 0;
    }

    /**
     * Calculates the exponential cost multiplier for the next reforge operation.
     */
    public static double getCostMultiplier(ItemStack input, LootRarity targetRarity) {
        if (input == null || input.isEmpty() || targetRarity == null) {
            return 1.0;
        }

        int previousCount = getReforgeCountForRarity(input, targetRarity);
        if (previousCount <= 0) {
            return 1.0;
        }

        double base = ExponentialTweakConfig.BASE_EXPONENT.get();
        return Math.pow(base, previousCount);
    }

    /**
     * Computes the scaled Sigil of Reforging (Sigil of Rebirth) cost.
     */
    public static int computeSigilCost(int baseCost, ItemStack input, LootRarity rarity) {
        if (!ExponentialTweakConfig.SCALE_SIGIL_COST.get() || input == null || input.isEmpty() || rarity == null) {
            return baseCost;
        }

        int previousCount = getReforgeCountForRarity(input, rarity);
        if (previousCount <= 0) {
            // First reforge of this rarity tier: keep base recipe cost (0 for common)
            return baseCost;
        }

        // For 2nd and subsequent reforges of the same rarity tier:
        // If base cost was 0 (Common), treat effective base as 1 Sigil so 2nd reforge requires 1, 3rd requires 2, etc.
        int effectiveBase = (baseCost <= 0) ? 1 : baseCost;
        double mult = Math.pow(ExponentialTweakConfig.BASE_EXPONENT.get(), previousCount - (baseCost <= 0 ? 1 : 0));
        long scaled = Math.round(effectiveBase * mult);
        int cap = ExponentialTweakConfig.MAX_COST_CAP.get();
        return (int) Math.min((long) cap, Math.max(effectiveBase, scaled));
    }

    /**
     * Computes the scaled Rarity Material cost.
     */
    public static int computeMatCost(int baseCost, ItemStack input, LootRarity rarity) {
        if (baseCost <= 0) return 0;
        if (!ExponentialTweakConfig.SCALE_MATERIAL_COST.get() || input == null || input.isEmpty() || rarity == null) {
            return baseCost;
        }
        double mult = getCostMultiplier(input, rarity);
        long scaled = Math.round(baseCost * mult);
        int cap = ExponentialTweakConfig.MAX_COST_CAP.get();
        return (int) Math.min((long) cap, Math.max(baseCost, scaled));
    }

    /**
     * Records the new reforge count onto the output ItemStack's NBT.
     */
    public static void recordReforgeSuccess(ItemStack output, LootRarity rarity, int newCount) {
        if (output == null || output.isEmpty() || rarity == null) return;

        String rarityId = getRarityId(rarity);

        // 1. Write to Apotheosis native affix_data compound
        CompoundTag afxData = output.getOrCreateTagElement(AffixHelper.AFFIX_DATA);
        afxData.putInt(TAG_CURRENT_COUNT, newCount);
        afxData.putString(TAG_CURRENT_RARITY, rarityId);
        if (!rarityId.isEmpty()) {
            afxData.putInt(TAG_RARITY_PREFIX + rarityId, newCount);
        }

        // 2. Write to ApothicExponential tag
        CompoundTag expTag = output.getOrCreateTagElement(TAG_ROOT);
        expTag.putInt(TAG_CURRENT_COUNT, newCount);
        expTag.putString(TAG_CURRENT_RARITY, rarityId);
        if (!rarityId.isEmpty()) {
            CompoundTag counts = expTag.getCompound(TAG_RARITY_COUNTS);
            counts.putInt(rarityId, newCount);
            expTag.put(TAG_RARITY_COUNTS, counts);
        }
    }

    /**
     * Calculates the new count from input and writes it onto output.
     */
    public static void recordReforgeSuccess(ItemStack output, ItemStack input, LootRarity rarity) {
        if (output == null || output.isEmpty() || rarity == null) return;
        int previousCount = getReforgeCountForRarity(input, rarity);
        recordReforgeSuccess(output, rarity, previousCount + 1);
    }

    /**
     * Checks if player has enough items directly in the slot.
     */
    public static boolean hasEnoughItems(Player player, ItemStack slotStack, int totalCost) {
        if (player.isCreative()) return true;
        if (totalCost <= 0) return true;
        if (slotStack.isEmpty()) return false;
        return slotStack.getCount() >= totalCost;
    }

    /**
     * Consumes items directly from the slot.
     */
    public static void consumeItems(Player player, Slot slot, int totalCost) {
        if (player.isCreative() || totalCost <= 0) return;
        ItemStack slotStack = slot.getItem();
        if (!slotStack.isEmpty()) {
            slotStack.shrink(totalCost);
        }
    }

    // ==========================================
    // Augmenting Table Tweak Methods
    // ==========================================
    public static final String TAG_AUGMENT_PREFIX = "apo_exp_aug_";

    /**
     * Resolves an affix identifier string (e.g., "apotheosis:crit_chance").
     */
    public static String getAffixId(DynamicHolder<? extends Affix> affixHolder) {
        if (affixHolder == null) return "";
        ResourceLocation id = affixHolder.getId();
        return id != null ? id.toString() : "";
    }

    /**
     * Gets how many times this specific affix has been augmented beyond the default level (1.0F).
     * Strictly reads from the recorded NBT tag, never estimating from current affix level
     * so that items reforged/merged via Apothic-Infinite are not falsely penalized.
     */
    public static int getAugmentOverCount(ItemStack stack, AffixInstance inst) {
        if (stack == null || stack.isEmpty() || inst == null) return 0;
        String affixId = getAffixId(inst.affix());
        if (affixId.isEmpty()) return 0;

        String key = TAG_AUGMENT_PREFIX + affixId.replace(':', '_');

        // Check affix_data
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                if (tag.contains(AffixHelper.AFFIX_DATA)) {
                    CompoundTag afxData = tag.getCompound(AffixHelper.AFFIX_DATA);
                    if (afxData.contains(key)) {
                        return Math.max(0, afxData.getInt(key));
                    }
                }
                if (tag.contains(TAG_ROOT)) {
                    CompoundTag expTag = tag.getCompound(TAG_ROOT);
                    if (expTag.contains(key)) {
                        return Math.max(0, expTag.getInt(key));
                    }
                }
            }
        }

        return 0;
    }

    /**
     * Computes the Sigil of Enhancement cost to upgrade an affix.
     * Base vanilla cost is 2 sigils.
     * When current level < 1.0F: 2 sigils (normal augment within default bounds).
     * When current level >= 1.0F: 2 * (base ^ (overCount + 1)), e.g. 4, 8, 16, 32...
     * (1st upgrade beyond default costs 4, doubling with each subsequent beyond-default augment).
     */
    public static int computeAugmentUpgradeCost(ItemStack stack, AffixInstance inst) {
        if (inst == null || inst.level() < 1.0F - 1e-4F) {
            return 2; // Vanilla base cost
        }

        if (!ExponentialTweakConfig.SCALE_AUGMENT_COST.get()) {
            return 2;
        }

        int overCount = getAugmentOverCount(stack, inst);
        double exponent = ExponentialTweakConfig.AUGMENT_BASE_EXPONENT.get();
        double mult = Math.pow(exponent, overCount + 1);
        long scaled = Math.round(2.0 * mult);
        int cap = ExponentialTweakConfig.MAX_COST_CAP.get();
        return (int) Math.min((long) cap, Math.max(2L, scaled));
    }

    /**
     * Records a successful augment upgrade onto the ItemStack's NBT.
     * Only increments the beyond-default augment count if the affix was already at or above 1.0F
     * prior to this upgrade.
     */
    public static void recordAugmentSuccess(ItemStack stack, AffixInstance prevInst) {
        if (stack == null || stack.isEmpty() || prevInst == null) return;
        if (prevInst.level() < 1.0F - 1e-4F) {
            // Normal augment up to the default maximum level (1.0F). Not a beyond-default augment.
            return;
        }

        String affixId = getAffixId(prevInst.affix());
        if (affixId.isEmpty()) return;

        int newCount = getAugmentOverCount(stack, prevInst) + 1;
        String key = TAG_AUGMENT_PREFIX + affixId.replace(':', '_');

        CompoundTag afxData = stack.getOrCreateTagElement(AffixHelper.AFFIX_DATA);
        afxData.putInt(key, newCount);

        CompoundTag expTag = stack.getOrCreateTagElement(TAG_ROOT);
        expTag.putInt(key, newCount);
    }

    private static final ThreadLocal<Boolean> REFORGING_ACTIVE = ThreadLocal.withInitial(() -> false);

    public static boolean isReforgingActive() {
        return REFORGING_ACTIVE.get();
    }

    public static void setReforgingActive(boolean active) {
        REFORGING_ACTIVE.set(active);
    }
}

