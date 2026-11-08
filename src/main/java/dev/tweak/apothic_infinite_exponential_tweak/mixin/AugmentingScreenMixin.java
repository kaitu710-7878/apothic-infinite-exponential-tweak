package dev.tweak.apothic_infinite_exponential_tweak.mixin;

import com.maxwell.apothic_infnite.InfiniteConfig;
import dev.tweak.apothic_infinite_exponential_tweak.util.ExponentialCostHelper;
import dev.tweak.apothic_infinite_exponential_tweak.util.IFatTexButton;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.augmenting.AugmentingMenu;
import dev.shadowsoffire.apotheosis.adventure.affix.augmenting.AugmentingScreen;
import dev.shadowsoffire.apotheosis.adventure.client.SimpleTexButton;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = AugmentingScreen.class, priority = 10000)
public abstract class AugmentingScreenMixin {

    @Shadow(remap = false)
    protected ItemStack lastMainItem;

    @Shadow(remap = false)
    protected List<AffixInstance> currentItemAffixes;

    @Shadow(remap = false)
    protected SimpleTexButton upgradeBtn;

    @Shadow(remap = false)
    protected abstract int getSelectedAffix();

    @Inject(method = "updateCachedState", at = @At("TAIL"), remap = false)
    private void apoExp$onUpdateCachedState(CallbackInfo ci) {
        AugmentingScreen screen = (AugmentingScreen) (Object) this;
        int selected = this.getSelectedAffix();
        if (selected != -1 && this.currentItemAffixes != null && selected < this.currentItemAffixes.size()) {
            AffixInstance current = this.currentItemAffixes.get(selected);
            int cost = ExponentialCostHelper.computeAugmentUpgradeCost(this.lastMainItem, current);

            if (this.upgradeBtn instanceof IFatTexButton fatBtn) {
                Component sigilName = Component.translatable("item.apotheosis.sigil_of_enhancement").withStyle(ChatFormatting.YELLOW);
                fatBtn.apoExp$setCostMsg(Component.translatable("button.apotheosis.augmenting.upgrade.cost", cost, sigilName));
            }

            float maxLevel = Float.MAX_VALUE;
            try {
                if (InfiniteConfig.MAX_AFFIX_LEVEL != null) {
                    maxLevel = ((Double) InfiniteConfig.MAX_AFFIX_LEVEL.get()).floatValue();
                }
            } catch (Throwable ignored) {
            }

            if (current.level() >= maxLevel) {
                this.upgradeBtn.active = false;
                this.upgradeBtn.setInactiveMessage(Component.translatable("button.apotheosis.augmenting.max_level").withStyle(ChatFormatting.RED));
            } else {
                AugmentingMenu menu = (AugmentingMenu) screen.getMenu();
                int sigilCount = menu.getSigils().getCount();
                boolean isCreative = false;
                try {
                    if (net.minecraft.client.Minecraft.getInstance().player != null) {
                        isCreative = net.minecraft.client.Minecraft.getInstance().player.isCreative();
                    }
                } catch (Throwable ignored) {
                }

                if (!isCreative && sigilCount < cost) {
                    this.upgradeBtn.active = false;
                    this.upgradeBtn.setInactiveMessage(Component.translatable("Not Enough Sigils").withStyle(ChatFormatting.RED));
                } else {
                    this.upgradeBtn.active = true;
                }
            }
        }
    }
}

