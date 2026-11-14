package dev.tweak.apothic_infinite_exponential_tweak.mixin;

import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import dev.tweak.apothic_infinite_exponential_tweak.util.EnchantmentCapHelper;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EnchHooks.class, priority = 2000, remap = false)
public abstract class EnchHooksMixin {

    @Inject(method = "getMaxLevel", at = @At("RETURN"), cancellable = true, remap = false)
    private static void apoExp$overrideMaxLevel(Enchantment ench, CallbackInfoReturnable<Integer> cir) {
        if (!EnchantmentCapHelper.isEnabled() || ench == null) {
            return;
        }
        cir.setReturnValue(EnchantmentCapHelper.getApotheosisPlusTwoMaxLevel(ench));
    }

    @Inject(method = "getMaxLootLevel", at = @At("RETURN"), cancellable = true, remap = false)
    private static void apoExp$overrideMaxLootLevel(Enchantment ench, CallbackInfoReturnable<Integer> cir) {
        if (!EnchantmentCapHelper.isEnabled() || ench == null) {
            return;
        }
        cir.setReturnValue(EnchantmentCapHelper.getApotheosisPlusTwoMaxLootLevel(ench));
    }
}
