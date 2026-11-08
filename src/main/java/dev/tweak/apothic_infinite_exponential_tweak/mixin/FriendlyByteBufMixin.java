package dev.tweak.apothic_infinite_exponential_tweak.mixin;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin {

    @Redirect(
            method = "writeItemStack(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/network/FriendlyByteBuf;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeByte(I)Lio/netty/buffer/ByteBuf;"),
            remap = true
    )
    private ByteBuf apoExp$writeItemCountVarInt(FriendlyByteBuf buf, int value) {
        return buf.writeVarInt(value);
    }

    @Inject(method = "readItem()Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true, remap = true)
    private void apoExp$readItemVarInt(CallbackInfoReturnable<ItemStack> cir) {
        FriendlyByteBuf buf = (FriendlyByteBuf) (Object) this;
        if (!buf.readBoolean()) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }
        Item item = buf.readById(BuiltInRegistries.ITEM);
        int count = buf.readVarInt();
        ItemStack stack = new ItemStack(item, count);
        stack.readShareTag(buf.readNbt());
        cir.setReturnValue(stack);
    }
}

