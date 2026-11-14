package dev.tweak.apothic_infinite_exponential_tweak.mixin;

import dev.shadowsoffire.apotheosis.ench.EnchModule;
import dev.shadowsoffire.apotheosis.ench.EnchantmentInfo;
import dev.tweak.apothic_infinite_exponential_tweak.util.EnchantmentCapHelper;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EnchModule.class, remap = false)
public abstract class EnchModuleMixin {

    @Inject(method = "getEnchInfo", at = @At("HEAD"), remap = false)
    private static void apoExp$beginResolveEnchInfo(Enchantment ench, CallbackInfoReturnable<EnchantmentInfo> cir) {
        EnchantmentCapHelper.beginResolve();
    }

    @Inject(method = "getEnchInfo", at = @At("RETURN"), remap = false)
    private static void apoExp$endResolveEnchInfo(Enchantment ench, CallbackInfoReturnable<EnchantmentInfo> cir) {
        EnchantmentCapHelper.endResolve();
    }

    @Inject(method = "getDefaultMax", at = @At("HEAD"), remap = false)
    private static void apoExp$beginResolveDefaultMax(Enchantment ench, CallbackInfoReturnable<Integer> cir) {
        EnchantmentCapHelper.beginResolve();
    }

    @Inject(method = "getDefaultMax", at = @At("RETURN"), remap = false)
    private static void apoExp$endResolveDefaultMax(Enchantment ench, CallbackInfoReturnable<Integer> cir) {
        EnchantmentCapHelper.endResolve();
    }
}
