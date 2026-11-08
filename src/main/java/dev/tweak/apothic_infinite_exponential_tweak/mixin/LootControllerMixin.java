package dev.tweak.apothic_infinite_exponential_tweak.mixin;

import dev.tweak.apothic_infinite_exponential_tweak.util.ExponentialCostHelper;
import dev.shadowsoffire.apotheosis.adventure.loot.LootController;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LootController.class, remap = false)
public abstract class LootControllerMixin {

    /**
     * When reforging an item (isReforgingActive() == true), redirect SocketHelper.getSockets(stack)
     * inside createLootItem to return 0.
     * This prevents Apotheosis from preserving the input stack's sockets via Math.max(socketsRolled, inputSockets),
     * allowing createLootItem to return purely the newly rolled sockets (S_roll).
     * Apothic-Infinite's subsequent addition (inputSockets + choiceSockets) then evaluates to:
     * S_input + S_roll (pure addition, no multiplication/doubling).
     */
    @Redirect(
            method = "createLootItem(Lnet/minecraft/world/item/ItemStack;Ldev/shadowsoffire/apotheosis/adventure/loot/LootCategory;Ldev/shadowsoffire/apotheosis/adventure/loot/LootRarity;Lnet/minecraft/util/RandomSource;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(value = "INVOKE", target = "Ldev/shadowsoffire/apotheosis/adventure/socket/SocketHelper;getSockets(Lnet/minecraft/world/item/ItemStack;)I"),
            remap = false
    )
    private static int apoExp$redirectGetSocketsDuringReforging(ItemStack stack) {
        if (ExponentialCostHelper.isReforgingActive()) {
            return 0;
        }
        return SocketHelper.getSockets(stack);
    }
}
