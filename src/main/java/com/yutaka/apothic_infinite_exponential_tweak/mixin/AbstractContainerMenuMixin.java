package com.yutaka.apothic_infinite_exponential_tweak.mixin;

import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {

    @Redirect(
            method = "moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z",
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"),
            remap = true
    )
    private int apoExp$redirectMoveMin(int slotMax, int itemMax) {
        if (slotMax == Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return Math.min(slotMax, itemMax);
    }
}
