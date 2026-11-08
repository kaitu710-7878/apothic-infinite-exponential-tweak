package dev.tweak.apothic_infinite_exponential_tweak.mixin;

import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ReforgingMenu.class, remap = false)
public interface ReforgingMenuAccessor {

    @Invoker("updateSeed")
    void apoExp$callUpdateSeed();
}

