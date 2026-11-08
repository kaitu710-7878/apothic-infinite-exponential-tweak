package com.yutaka.apothic_infinite_exponential_tweak.mixin;

import com.yutaka.apothic_infinite_exponential_tweak.util.ExponentialCostHelper;
import com.yutaka.apothic_infinite_exponential_tweak.util.IExtendedSlot;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingMenu;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/core/BlockPos;)V", at = @At("RETURN"), remap = false)
    private void apoExp$enableInfiniteSlots(int id, Inventory pInv, BlockPos pos, CallbackInfo ci) {
        AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
        if (menu.slots.size() > 2) {
            Slot matSlot = menu.getSlot(1);
            Slot sigilSlot = menu.getSlot(2);
            if (matSlot instanceof IExtendedSlot ext) {
                ext.apoExp$setInfiniteStack(true);
            }
            if (sigilSlot instanceof IExtendedSlot ext) {
                ext.apoExp$setInfiniteStack(true);
            }
        }
    }

    @Unique
    private ItemStack apoExp$lastInput = ItemStack.EMPTY;

    private ItemStack apoExp$getInputItem() {
        try {
            AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
            ItemStack stack = menu.getSlot(0).getItem();
            if (!stack.isEmpty()) {
                this.apoExp$lastInput = stack.copy();
                return stack;
            }
        } catch (Throwable ignored) {
        }
        if (this.itemInv != null) {
            ItemStack stack = this.itemInv.getStackInSlot(0);
            if (!stack.isEmpty()) {
                this.apoExp$lastInput = stack.copy();
                return stack;
            }
        }
        return this.apoExp$lastInput;
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
        if (input.isEmpty() || rarity == null || this.choicesInv == null) {
            if (input.isEmpty()) {
                this.apoExp$lastInput = ItemStack.EMPTY;
            }
            return;
        }

        this.apoExp$lastInput = input.copy();

        for (int i = 0; i < 3; i++) {
            ItemStack output = this.choicesInv.getStackInSlot(i);
            if (!output.isEmpty()) {
                int inputSockets = dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper.getSockets(input);
                int currentChoiceSockets = dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper.getSockets(output);
                int rollSockets = Math.max(0, currentChoiceSockets - inputSockets);
                int correctSockets = Math.max(inputSockets, rollSockets);
                dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper.setSockets(output, correctSockets);

                ExponentialCostHelper.recordReforgeSuccess(output, input, rarity);
                this.choicesInv.setStackInSlot(i, output);
            }
        }
    }
}
