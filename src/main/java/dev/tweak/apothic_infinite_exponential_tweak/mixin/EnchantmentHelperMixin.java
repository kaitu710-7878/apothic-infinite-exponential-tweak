package dev.tweak.apothic_infinite_exponential_tweak.mixin;

import dev.tweak.apothic_infinite_exponential_tweak.util.EnchantmentCapHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(value = EnchantmentHelper.class, priority = 2000)
public abstract class EnchantmentHelperMixin {

    @Unique
    private static final ThreadLocal<Boolean> apoExp$inGetEnchantmentsWrapper = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @Unique
    private static final ThreadLocal<Boolean> apoExp$inGetItemLevelWrapper = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @Inject(method = "getEnchantments(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Map;", at = @At("HEAD"), cancellable = true)
    private static void apoExp$wrapGetEnchantments(ItemStack stack, CallbackInfoReturnable<Map<Enchantment, Integer>> cir) {
        if (!EnchantmentCapHelper.isEnabled() || Boolean.TRUE.equals(apoExp$inGetEnchantmentsWrapper.get())) {
            return;
        }

        apoExp$inGetEnchantmentsWrapper.set(Boolean.TRUE);
        try {
            Map<Enchantment, Integer> original = EnchantmentHelper.getEnchantments(stack);
            if (original == null || original.isEmpty()) {
                cir.setReturnValue(original);
                return;
            }

            boolean modified = false;
            Map<Enchantment, Integer> capped = new LinkedHashMap<>(original.size());
            for (Map.Entry<Enchantment, Integer> entry : original.entrySet()) {
                Enchantment ench = entry.getKey();
                int level = entry.getValue();
                int cap = EnchantmentCapHelper.getApotheosisPlusTwoMaxLevel(ench);
                int clamped = Math.min(level, cap);
                if (clamped != level) {
                    modified = true;
                }
                capped.put(ench, clamped);
            }

            cir.setReturnValue(modified ? capped : original);
        } finally {
            apoExp$inGetEnchantmentsWrapper.set(Boolean.FALSE);
        }
    }

    @Inject(method = "getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    private static void apoExp$wrapGetItemEnchantmentLevel(Enchantment ench, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (!EnchantmentCapHelper.isEnabled() || Boolean.TRUE.equals(apoExp$inGetItemLevelWrapper.get())) {
            return;
        }

        apoExp$inGetItemLevelWrapper.set(Boolean.TRUE);
        try {
            int level = EnchantmentHelper.getItemEnchantmentLevel(ench, stack);
            if (level <= 0) {
                cir.setReturnValue(level);
                return;
            }
            int cap = EnchantmentCapHelper.getApotheosisPlusTwoMaxLevel(ench);
            cir.setReturnValue(Math.min(level, cap));
        } finally {
            apoExp$inGetItemLevelWrapper.set(Boolean.FALSE);
        }
    }
}
