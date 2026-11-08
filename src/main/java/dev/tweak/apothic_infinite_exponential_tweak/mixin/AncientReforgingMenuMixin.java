package dev.tweak.apothic_infinite_exponential_tweak.mixin;

import dev.tweak.apothic_infinite_exponential_tweak.util.ExponentialCostHelper;
import dev.tweak.apothic_infinite_exponential_tweak.util.IExtendedSlot;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Pseudo
@Mixin(targets = "com.ianm1647.ancientreforging.screen.AncientReforgingMenu", priority = 10000, remap = false)
public abstract class AncientReforgingMenuMixin {

    @Shadow(remap = false)
    protected InternalItemHandler itemInv;

    @Shadow(remap = false)
    protected InternalItemHandler choicesInv;

    @Shadow(remap = false)
    @Nullable
    public abstract LootRarity getRarity();

    @Dynamic
    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/core/BlockPos;)V", at = @At("RETURN"), require = 0)
    private void apoExp$enableAncientInfiniteSlots(int id, Inventory pInv, BlockPos pos, CallbackInfo ci) {
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

    @org.spongepowered.asm.mixin.Unique
    private ItemStack apoExp$lastAncientInput = ItemStack.EMPTY;

    private ItemStack apoExp$getAncientInputItem() {
        try {
            AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
            ItemStack stack = menu.getSlot(0).getItem();
            if (!stack.isEmpty()) {
                this.apoExp$lastAncientInput = stack.copy();
                return stack;
            }
        } catch (Throwable ignored) {
        }
        if (this.itemInv != null) {
            ItemStack stack = this.itemInv.getStackInSlot(0);
            if (!stack.isEmpty()) {
                this.apoExp$lastAncientInput = stack.copy();
                return stack;
            }
        }
        return this.apoExp$lastAncientInput;
    }

    @Dynamic
    @Inject(method = "getSigilCost", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private void apoExp$scaleAncientSigilCost(int slot, CallbackInfoReturnable<Integer> cir) {
        int originalCost = cir.getReturnValue();
        ItemStack input = apoExp$getAncientInputItem();
        LootRarity rarity = this.getRarity();
        int scaledCost = ExponentialCostHelper.computeSigilCost(originalCost, input, rarity);
        cir.setReturnValue(scaledCost);
    }

    @Dynamic
    @Inject(method = "getMatCost", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private void apoExp$scaleAncientMatCost(int slot, CallbackInfoReturnable<Integer> cir) {
        int originalCost = cir.getReturnValue();
        ItemStack input = apoExp$getAncientInputItem();
        LootRarity rarity = this.getRarity();
        int scaledCost = ExponentialCostHelper.computeMatCost(originalCost, input, rarity);
        cir.setReturnValue(scaledCost);
    }

    @Dynamic
    @Inject(method = {"slotsChanged", "m_6199_"}, at = @At("HEAD"), require = 0)
    private void apoExp$onAncientSlotsChangedHead(Container pContainer, CallbackInfo ci) {
        ExponentialCostHelper.setReforgingActive(true);
    }

    @Dynamic
    @Inject(method = {"slotsChanged", "m_6199_"}, at = @At("TAIL"), require = 0)
    private void apoExp$onAncientSlotsChangedTail(Container pContainer, CallbackInfo ci) {
        ExponentialCostHelper.setReforgingActive(false);

        ItemStack input = apoExp$getAncientInputItem();
        LootRarity rarity = this.getRarity();
        if (input.isEmpty() || rarity == null || this.choicesInv == null) {
            if (input.isEmpty()) {
                this.apoExp$lastAncientInput = ItemStack.EMPTY;
            }
            return;
        }

        this.apoExp$lastAncientInput = input.copy();

        for (int i = 0; i < 3; i++) {
            ItemStack output = this.choicesInv.getStackInSlot(i);
            if (!output.isEmpty()) {
                ExponentialCostHelper.recordReforgeSuccess(output, input, rarity);
                this.choicesInv.setStackInSlot(i, output);
            }
        }
    }
}
