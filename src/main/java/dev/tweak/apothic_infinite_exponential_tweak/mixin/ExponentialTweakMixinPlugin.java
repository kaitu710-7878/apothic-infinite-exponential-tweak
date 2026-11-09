package dev.tweak.apothic_infinite_exponential_tweak.mixin;

import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ExponentialTweakMixinPlugin implements IMixinConfigPlugin {

    private boolean isModLoaded(String modId) {
        try {
            return LoadingModList.get() != null && LoadingModList.get().getModFileById(modId) != null;
        } catch (Throwable t) {
            return false;
        }
    }

    private boolean isResourceAvailable(String resourcePath) {
        try {
            return getClass().getClassLoader().getResource(resourcePath) != null;
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith("AncientReforgingMenuMixin")) {
            return isModLoaded("ancientreforging")
                    || isResourceAvailable("com/ianm1647/ancientreforging/screen/AncientReforgingMenu.class");
        }
        if (mixinClassName.endsWith("EsotericReforgingMenuMixin")) {
            return isModLoaded("esotericreforging")
                    || isResourceAvailable("com/mira/esotericreforging/screen/EsotericReforgingMenu.class")
                    || isResourceAvailable("net/yirmiri/esotericreforging/common/menu/EsotericReforgingMenu.class");
        }
        if (mixinClassName.endsWith("AffixMergerMixin")) {
            return isModLoaded("apothic_infnite")
                    || isResourceAvailable("com/maxwell/apothic_infnite/adventure/util/AffixMerger.class");
        }
        if (mixinClassName.endsWith("FriendlyByteBufMixin")) {
            // BiggerStacks handles packet serialization for larger item counts with writeInt/readInt.
            // Applying our FriendlyByteBufMixin alongside BiggerStacks causes a @Redirect conflict and protocol mismatch.
            return !isModLoaded("biggerstacks")
                    && !isResourceAvailable("portb/biggerstacks/mixin/vanilla/FriendlyByteBufMixin.class");
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}

