package com.yutaka.apothic_infinite_exponential_tweak.mixin;

import com.yutaka.apothic_infinite_exponential_tweak.util.ExponentialCostHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingMenu;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = ReforgingMenu.class, priority = 10000)
public abstract class ReforgingMenuMixin {

    @Shadow(remap = false)
    protected InternalItemHandler itemInv;

    @Shadow(remap = false)
    protected InternalItemHandler choicesInv;

    @Shadow(remap = false)
    @Nullable
    public abstract LootRarity getRarity();

    private ItemStack apoExp$getInputItem() {
        try {
            AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
            ItemStack stack = menu.getSlot(0).getItem();
            if (!stack.isEmpty()) return stack;
        } catch (Throwable ignored) {
        }
        return this.itemInv != null ? this.itemInv.getStackInSlot(0) : ItemStack.EMPTY;
    }

    @Inject(method = "getSigilCost", at = @At("RETURN"), cancellable = true, remap = false)
    private void apoExp$scaleSigilCost(int slot, CallbackInfoReturnable<Integer> cir) {
        int originalCost = cir.getReturnValue();
        ItemStack input = apoExp$getInputItem();
        LootRarity rarity = this.getRarity();
        int scaledCost = ExponentialCostHelper.computeSigilCost(originalCost, input, rarity);
        cir.setReturnValue(scaledCost);
    }

    @Inject(method = "getMatCost", at = @At("RETURN"), cancellable = true, remap = false)
    private void apoExp$scaleMatCost(int slot, CallbackInfoReturnable<Integer> cir) {
        int originalCost = cir.getReturnValue();
        ItemStack input = apoExp$getInputItem();
        LootRarity rarity = this.getRarity();
        int scaledCost = ExponentialCostHelper.computeMatCost(originalCost, input, rarity);
        cir.setReturnValue(scaledCost);
    }

    @Inject(method = "slotsChanged", at = @At("TAIL"), remap = true)
    private void apoExp$onSlotsChanged(Container pContainer, CallbackInfo ci) {
        ItemStack input = apoExp$getInputItem();
        LootRarity rarity = this.getRarity();
        if (input.isEmpty() || rarity == null || this.choicesInv == null) return;

        for (int i = 0; i < 3; i++) {
            ItemStack output = this.choicesInv.getStackInSlot(i);
            if (!output.isEmpty()) {
                ExponentialCostHelper.recordReforgeSuccess(output, input, rarity);
                this.choicesInv.setStackInSlot(i, output);
            }
        }
    }
}
