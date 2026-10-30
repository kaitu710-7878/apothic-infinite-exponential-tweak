package com.yutaka.apothic_infinite_exponential_tweak.mixin;

import com.maxwell.apothic_infnite.InfiniteConfig;
import com.yutaka.apothic_infinite_exponential_tweak.util.ExponentialCostHelper;
import com.yutaka.apothic_infinite_exponential_tweak.util.IExtendedSlot;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.augmenting.AugmentingMenu;
import dev.shadowsoffire.apotheosis.adventure.affix.augmenting.AugmentingTableTile;
import dev.shadowsoffire.placebo.menu.BlockEntityMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = AugmentingMenu.class, priority = 10000)
public abstract class AugmentingMenuMixin extends BlockEntityMenu<AugmentingTableTile> {

    protected AugmentingMenuMixin(int id, Inventory inv, BlockPos pos) {
        super(null, id, inv, pos);
    }

    @Shadow(remap = false)
    public abstract ItemStack getMainItem();

    @Shadow(remap = false)
    public abstract ItemStack getSigils();

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/core/BlockPos;)V", at = @At("RETURN"), remap = false)
    private void apoExp$enableInfiniteSigilSlot(int id, Inventory inv, BlockPos pos, CallbackInfo ci) {
        AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
        if (menu.slots.size() > 1) {
            Slot sigilSlot = menu.getSlot(1);
            if (sigilSlot instanceof IExtendedSlot ext) {
                ext.apoExp$setInfiniteStack(true);
            }
        }
    }

    @Inject(method = "clickMenuButton", at = @At("HEAD"), cancellable = true, remap = true)
    private void apoExp$onClickMenuButton(Player player, int id, CallbackInfoReturnable<Boolean> cir) {
        if ((id & 1) == 0) { // UPGRADE action
            int selected = id >> 1;
            ItemStack mainItem = this.getMainItem();
            if (mainItem.isEmpty()) {
                cir.setReturnValue(false);
                return;
            }

            List<AffixInstance> affixes = AugmentingMenu.computeItemAffixes(mainItem);
            if (affixes.isEmpty() || selected >= affixes.size()) {
                cir.setReturnValue(false);
                return;
            }

            AffixInstance inst = affixes.get(selected);
            float maxLevel = Float.MAX_VALUE;
            try {
                if (InfiniteConfig.MAX_AFFIX_LEVEL != null) {
                    maxLevel = ((Double) InfiniteConfig.MAX_AFFIX_LEVEL.get()).floatValue();
                }
            } catch (Throwable ignored) {
            }

            if (inst.level() >= maxLevel) {
                cir.setReturnValue(false);
                return;
            }

            int cost = ExponentialCostHelper.computeAugmentUpgradeCost(mainItem, inst);
            ItemStack sigils = this.getSigils();
            if (!player.isCreative()) {
                if (sigils.getCount() < cost) {
                    cir.setReturnValue(false);
                    return;
                }
                sigils.shrink(cost);
            }

            AffixInstance upgraded = inst.withNewLevel(inst.level() + 0.25F);
            AffixHelper.applyAffix(mainItem, upgraded);
            ExponentialCostHelper.recordAugmentSuccess(mainItem, inst);

            ((Slot) this.slots.get(0)).set(mainItem);

            if (this.tile != null) {
                this.tile.setChanged();
            }
            this.broadcastChanges();

            player.level().playSound(null, this.pos, SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 1.0F, player.level().random.nextFloat() * 0.25F + 1.0F);
            player.level().playSound(null, this.pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.34F, player.level().random.nextFloat() * 0.2F + 0.8F);
            player.level().playSound(null, this.pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.45F, player.level().random.nextFloat() * 0.75F + 0.5F);

            cir.setReturnValue(true);
        }
    }
}
