package com.yutaka.apothic_infinite_exponential_tweak.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStackHandler.class, remap = false)
public abstract class ItemStackHandlerMixin {

    @Shadow
    protected NonNullList<ItemStack> stacks;

    @Inject(method = "serializeNBT()Lnet/minecraft/nbt/CompoundTag;", at = @At("RETURN"), remap = false)
    private void apoExp$saveExtendedCount(CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag nbt = cir.getReturnValue();
        if (nbt != null && nbt.contains("Items", 9)) {
            ListTag tagList = nbt.getList("Items", 10);
            for (int i = 0; i < tagList.size(); i++) {
                CompoundTag itemTags = tagList.getCompound(i);
                int slot = itemTags.getInt("Slot");
                if (slot >= 0 && slot < this.stacks.size()) {
                    ItemStack stack = this.stacks.get(slot);
                    if (!stack.isEmpty()) {
                        int count = stack.getCount();
                        if (count > 64) {
                            itemTags.putInt("ExtendedCount", count);
                            // バニラロード時に count <= 0 で EMPTY 判定され破棄されないよう正の数を保証
                            itemTags.putByte("Count", (byte) 1);
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "deserializeNBT(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"), remap = false)
    private void apoExp$loadExtendedCount(CompoundTag nbt, CallbackInfo ci) {
        if (nbt != null && nbt.contains("Items", 9)) {
            ListTag tagList = nbt.getList("Items", 10);
            for (int i = 0; i < tagList.size(); i++) {
                CompoundTag itemTags = tagList.getCompound(i);
                int slot = itemTags.getInt("Slot");
                if (slot >= 0 && slot < this.stacks.size() && itemTags.contains("ExtendedCount", 3)) {
                    int extendedCount = itemTags.getInt("ExtendedCount");
                    ItemStack stack = this.stacks.get(slot);
                    if (stack != null && !stack.is(Items.AIR) && stack != ItemStack.EMPTY) {
                        stack.setCount(extendedCount);
                    } else {
                        // 万が一 stack が EMPTY だった場合でも安全タグから再生成して復元
                        CompoundTag safeTag = itemTags.copy();
                        safeTag.putByte("Count", (byte) 1);
                        ItemStack restored = ItemStack.of(safeTag);
                        if (!restored.isEmpty()) {
                            restored.setCount(extendedCount);
                            this.stacks.set(slot, restored);
                        }
                    }
                }
            }
        }
    }
}
