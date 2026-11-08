package dev.tweak.apothic_infinite_exponential_tweak.mixin;

import dev.shadowsoffire.apotheosis.adventure.affix.effect.DamageReductionAffix;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DamageReductionAffix.class, remap = false)
public abstract class DamageReductionAffixMixin {

    @Inject(method = "getTrueLevel", at = @At("RETURN"), cancellable = true, remap = false)
    private void apoExp$capDamageReduction(LootRarity rarity, float level, CallbackInfoReturnable<Float> cir) {
        float val = cir.getReturnValue();
        if (val > 0.99F) {
            cir.setReturnValue(0.99F);
        }
    }
}

