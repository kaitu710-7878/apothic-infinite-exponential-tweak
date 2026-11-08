package dev.tweak.apothic_infinite_exponential_tweak.event;

import dev.tweak.apothic_infinite_exponential_tweak.config.ExponentialTweakConfig;
import dev.tweak.apothic_infinite_exponential_tweak.util.ExponentialCostHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemTooltipEventHandler {

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;

        if (ExponentialTweakConfig.SHOW_REFORGE_COUNT_TOOLTIP.get()) {
            String rarityId = ExponentialCostHelper.getItemRarityId(stack);
            if (!rarityId.isEmpty()) {
                LootRarity rarity = AffixHelper.getRarity(stack).getOptional().orElse(null);
                if (rarity == null) {
                    try {
                        rarity = dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry.byLegacyId(rarityId).getOptional().orElse(null);
                    } catch (Throwable ignored) {
                    }
                }
                if (rarity != null) {
                    int count = ExponentialCostHelper.getReforgeCountForRarity(stack, rarity);
                    if (count > 0) {
                        double nextMult = ExponentialCostHelper.getCostMultiplier(stack, rarity);
                        String formattedMult = (nextMult == Math.floor(nextMult))
                                ? String.valueOf((long) nextMult)
                                : String.format("%.1f", nextMult);

                        event.getToolTip().add(Component.empty());
                        event.getToolTip().add(Component.translatable(
                                "tooltip.apothic_infinite_exponential_tweak.reforge_info",
                                rarity.toComponent(),
                                Component.literal(String.valueOf(count)).withStyle(ChatFormatting.GOLD),
                                Component.literal(formattedMult + "x").withStyle(ChatFormatting.RED)
                        ).withStyle(ChatFormatting.GRAY));
                    }
                }
            }
        }

        if (ExponentialTweakConfig.SHOW_AUGMENT_TOOLTIP.get()) {
            java.util.Map<dev.shadowsoffire.placebo.reload.DynamicHolder<? extends dev.shadowsoffire.apotheosis.adventure.affix.Affix>, AffixInstance> affixes = AffixHelper.getAffixes(stack);
            if (!affixes.isEmpty()) {
                int totalOverAugments = 0;
                for (AffixInstance inst : affixes.values()) {
                    if (inst != null && inst.level() >= 1.0F - 1e-4F) {
                        int over = ExponentialCostHelper.getAugmentOverCount(stack, inst);
                        totalOverAugments += over;
                    }
                }
                if (totalOverAugments > 0) {
                    event.getToolTip().add(Component.translatable(
                            "tooltip.apothic_infinite_exponential_tweak.augment_total_info",
                            Component.literal(String.valueOf(totalOverAugments)).withStyle(ChatFormatting.AQUA)
                    ).withStyle(ChatFormatting.GRAY));
                }
            }
        }
    }
}

