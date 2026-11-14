package dev.tweak.apothic_infinite_exponential_tweak.mixin;

import dev.shadowsoffire.apotheosis.ench.table.RealEnchantmentHelper;
import dev.tweak.apothic_infinite_exponential_tweak.util.EnchantmentCapHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mixin(value = RealEnchantmentHelper.class, priority = 2000, remap = false)
public abstract class RealEnchantmentHelperMixin {

    @Unique
    private static final ThreadLocal<Boolean> apoExp$inSelectWrapper = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @Inject(method = "selectEnchantment", at = @At("HEAD"), cancellable = true, remap = false)
    private static void apoExp$wrapSelectEnchantment(RandomSource rand, ItemStack stack, int level, float quanta, float arcana, float rectification, boolean treasure, Set<Enchantment> blacklist, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (!EnchantmentCapHelper.isEnabled() || Boolean.TRUE.equals(apoExp$inSelectWrapper.get())) {
            return;
        }

        apoExp$inSelectWrapper.set(Boolean.TRUE);
        try {
            List<EnchantmentInstance> result = RealEnchantmentHelper.selectEnchantment(rand, stack, level, quanta, arcana, rectification, treasure, blacklist);
            if (result == null || result.isEmpty()) {
                cir.setReturnValue(result);
                return;
            }

            boolean modified = false;
            List<EnchantmentInstance> capped = new ArrayList<>(result.size());
            for (EnchantmentInstance inst : result) {
                int cap = EnchantmentCapHelper.getApotheosisPlusTwoMaxLevel(inst.enchantment);
                if (inst.level > cap) {
                    capped.add(new EnchantmentInstance(inst.enchantment, cap));
                    modified = true;
                } else {
                    capped.add(inst);
                }
            }

            cir.setReturnValue(modified ? capped : result);
        } finally {
            apoExp$inSelectWrapper.set(Boolean.FALSE);
        }
    }
}
