package dev.tweak.apothic_infinite_exponential_tweak.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Inject(method = "removed", at = @At("TAIL"), remap = true)
    private void apoExp$onMenuRemoved(Player player, CallbackInfo ci) {
        if (!player.level().isClientSide && (Object) this instanceof BlockEntityMenuAccessor accessor) {
            try {
                BlockEntity tile = accessor.apoExp$getTile();
                if (tile != null) {
                    tile.setChanged();
                }
            } catch (Throwable ignored) {
            }
        }
    }
}

