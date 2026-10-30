package com.yutaka.apothic_infinite_exponential_tweak.mixin;

import com.yutaka.apothic_infinite_exponential_tweak.util.IFatTexButton;
import dev.shadowsoffire.apotheosis.adventure.affix.augmenting.AugmentingScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = AugmentingScreen.FatTexButton.class, remap = false)
public abstract class FatTexButtonMixin implements IFatTexButton {

    @Shadow
    @Final
    @Mutable
    protected Component costMsg;

    @Override
    public void apoExp$setCostMsg(Component msg) {
        this.costMsg = msg;
    }

    @Override
    public Component apoExp$getCostMsg() {
        return this.costMsg;
    }
}
