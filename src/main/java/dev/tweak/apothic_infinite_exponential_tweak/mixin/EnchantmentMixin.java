package dev.tweak.apothic_infinite_exponential_tweak.mixin;

import dev.tweak.apothic_infinite_exponential_tweak.util.EnchantmentCapHelper;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Enchantment.class, priority = 500)
public abstract class EnchantmentMixin {

    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    private void apoExp$overrideVanillaMaxLevel(CallbackInfoReturnable<Integer> cir) {
        if (EnchantmentCapHelper.isResolvingApotheosisInfo()) {
            cir.setReturnValue(1);
            return;
        }
        if (!EnchantmentCapHelper.isEnabled()) {
            return;
        }
        Enchantment self = (Enchantment) (Object) this;
        cir.setReturnValue(EnchantmentCapHelper.getApotheosisPlusTwoMaxLevel(self));
    }
}
