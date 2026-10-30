package com.yutaka.apothic_infinite_exponential_tweak.mixin;

import com.yutaka.apothic_infinite_exponential_tweak.util.IExtendedSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlotItemHandler.class)
public abstract class SlotItemHandlerMixin implements IExtendedSlot {

    @Unique
    private boolean apoExp$infiniteStack = false;

    @Override
    public void apoExp$setInfiniteStack(boolean infinite) {
        this.apoExp$infiniteStack = infinite;
    }

    @Override
    public boolean apoExp$isInfiniteStack() {
        return this.apoExp$infiniteStack;
    }

    @Inject(method = "getMaxStackSize()I", at = @At("HEAD"), cancellable = true)
    private void apoExp$onGetMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        if (this.apoExp$infiniteStack) {
            cir.setReturnValue(Integer.MAX_VALUE);
        }
    }

    @Inject(method = "getMaxStackSize(Lnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    private void apoExp$onGetMaxStackSizeStack(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (this.apoExp$infiniteStack) {
            cir.setReturnValue(Integer.MAX_VALUE);
        }
    }
}
