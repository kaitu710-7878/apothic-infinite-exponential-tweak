package dev.tweak.apothic_infinite_exponential_tweak.mixin;

import dev.tweak.apothic_infinite_exponential_tweak.util.ExponentialCostHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingMenu;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingRecipe;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingTableTile;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.util.ApothMiscUtil;
import dev.shadowsoffire.placebo.util.EnchantmentUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ReforgingMenu.ReforgingResultSlot.class)
public abstract class ReforgingResultSlotMixin extends SlotItemHandler {

    public ReforgingResultSlotMixin(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true, remap = true)
    private void apoExp$checkPickupSlotOnly(Player playerIn, CallbackInfoReturnable<Boolean> cir) {
        if (!(playerIn.containerMenu instanceof ReforgingMenu menu)) {
            return;
        }

        ItemStack input = menu.getSlot(0).getItem();
        LootRarity rarity = menu.getRarity();
        if (rarity == null || input.isEmpty()) {
            cir.setReturnValue(false);
            return;
        }

        BlockEntityMenuAccessor beAccessor = (BlockEntityMenuAccessor) menu;
        ReforgingTableTile tile = (ReforgingTableTile) beAccessor.apoExp$getTile();
        if (tile == null) {
            cir.setReturnValue(false);
            return;
        }

        ReforgingRecipe recipe = tile.getRecipeFor(rarity);
        if (recipe == null) {
            cir.setReturnValue(false);
            return;
        }

        if (playerIn.isCreative()) {
            cir.setReturnValue(super.mayPickup(playerIn));
            return;
        }

        int sigilCost = menu.getSigilCost(this.getSlotIndex());
        int matCost = menu.getMatCost(this.getSlotIndex());
        int levelCost = menu.getLevelCost(this.getSlotIndex());

        if (playerIn.experienceLevel < levelCost) {
            cir.setReturnValue(false);
            return;
        }

        ItemStack matStack = menu.getSlot(1).getItem();
        ItemStack sigilStack = menu.getSlot(2).getItem();

        boolean hasMats = matCost <= 0 || (!matStack.isEmpty() && matStack.getCount() >= matCost);
        boolean hasSigils = sigilCost <= 0 || (!sigilStack.isEmpty() && sigilStack.getCount() >= sigilCost);

        if (!hasSigils || !hasMats) {
            cir.setReturnValue(false);
            return;
        }

        cir.setReturnValue(super.mayPickup(playerIn));
    }

    @Inject(method = "onTake", at = @At("HEAD"), cancellable = true, remap = true)
    private void apoExp$onTakeReforge(Player player, ItemStack stack, CallbackInfo ci) {
        if (!(player.containerMenu instanceof ReforgingMenu menu)) {
            return;
        }

        ItemStack input = menu.getSlot(0).getItem();
        LootRarity rarity = menu.getRarity();
        if (rarity != null && !stack.isEmpty()) {
            int existing = ExponentialCostHelper.getReforgeCountForRarity(stack, rarity);
            if (existing <= 0) {
                if (!input.isEmpty()) {
                    ExponentialCostHelper.recordReforgeSuccess(stack, input, rarity);
                } else {
                    ExponentialCostHelper.recordReforgeSuccess(stack, rarity, 1);
                }
            }
        }

        if (!player.level().isClientSide) {
            if (!player.isCreative()) {
                int sigilCost = menu.getSigilCost(this.getSlotIndex());
                int matCost = menu.getMatCost(this.getSlotIndex());
                int levelCost = menu.getLevelCost(this.getSlotIndex());

                if (matCost > 0) {
                    menu.getSlot(1).getItem().shrink(matCost);
                }
                if (sigilCost > 0) {
                    menu.getSlot(2).getItem().shrink(sigilCost);
                }

                EnchantmentUtils.chargeExperience(player, ApothMiscUtil.getExpCostForSlot(levelCost, this.getSlotIndex()));

                try {
                    BlockEntityMenuAccessor beAccessor = (BlockEntityMenuAccessor) menu;
                    net.minecraft.world.level.block.entity.BlockEntity tile = beAccessor.apoExp$getTile();
                    if (tile != null) {
                        tile.setChanged();
                    }
                } catch (Throwable ignored) {
                }
                menu.broadcastChanges();
            }
            menu.getSlot(0).set(ItemStack.EMPTY);

            player.getPersistentData().putInt(ReforgingMenu.REFORGE_SEED, player.getRandom().nextInt());
            ReforgingMenuAccessor accessor = (ReforgingMenuAccessor) menu;
            accessor.apoExp$callUpdateSeed();
        }

        player.playSound(SoundEvents.EVOKER_CAST_SPELL, 0.99F, player.level().random.nextFloat() * 0.25F + 1F);
        player.playSound(SoundEvents.AMETHYST_CLUSTER_STEP, 0.34F, player.level().random.nextFloat() * 0.2F + 0.8F);
        player.playSound(SoundEvents.SMITHING_TABLE_USE, 0.45F, player.level().random.nextFloat() * 0.5F + 0.75F);

        ci.cancel();
    }
}

