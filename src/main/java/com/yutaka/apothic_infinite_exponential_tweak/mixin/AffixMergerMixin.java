package com.yutaka.apothic_infinite_exponential_tweak.mixin;

import com.yutaka.apothic_infinite_exponential_tweak.util.ExponentialCostHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.maxwell.apothic_infnite.adventure.util.AffixMerger", remap = false)
public abstract class AffixMergerMixin {

    @Dynamic
    @Inject(method = "mergeAffixes", at = @At("RETURN"), remap = false, require = 0)
    private static void apoExp$onMergeAffixes(ItemStack input, ItemStack choice, long seed, CallbackInfo ci) {
        if (input == null || input.isEmpty() || choice == null || choice.isEmpty()) return;

        LootRarity rarity = AffixHelper.getRarity(choice).getOptional().orElse(null);
        if (rarity == null) {
            String rId = ExponentialCostHelper.getItemRarityId(choice);
            if (!rId.isEmpty()) {
                try {
                    rarity = dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry.byLegacyId(rId).getOptional().orElse(null);
                } catch (Throwable ignored) {
                }
            }
        }

        if (rarity != null) {
            ExponentialCostHelper.recordReforgeSuccess(choice, input, rarity);
        }
    }
}
