package com.yutaka.apothic_infinite_exponential_tweak.mixin;

import dev.shadowsoffire.apotheosis.adventure.affix.AttributeAffix;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Mixin(value = AttributeAffix.ModifierInst.class, remap = false)
public abstract class ModifierInstMixin {

    @Inject(method = "getOrCreateUUID", at = @At("HEAD"), cancellable = true, remap = false)
    private static void apoExp$getDeterministicUUID(ItemStack stack, ResourceLocation id, CallbackInfoReturnable<UUID> cir) {
        if (id != null) {
            cir.setReturnValue(UUID.nameUUIDFromBytes(("affix:" + id.toString()).getBytes(StandardCharsets.UTF_8)));
        }
    }
}
