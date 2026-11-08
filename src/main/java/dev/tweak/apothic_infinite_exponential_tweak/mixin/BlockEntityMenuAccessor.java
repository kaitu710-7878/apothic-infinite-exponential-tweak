package dev.tweak.apothic_infinite_exponential_tweak.mixin;

import dev.shadowsoffire.placebo.menu.BlockEntityMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BlockEntityMenu.class, remap = false)
public interface BlockEntityMenuAccessor {

    @Accessor("tile")
    BlockEntity apoExp$getTile();
}

