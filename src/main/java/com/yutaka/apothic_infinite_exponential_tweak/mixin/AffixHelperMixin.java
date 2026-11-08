package com.yutaka.apothic_infinite_exponential_tweak.mixin;

import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Mixin(value = AffixHelper.class, remap = false)
public abstract class AffixHelperMixin {

    @Inject(method = "getAffixesImpl", at = @At("HEAD"), cancellable = true, remap = false)
    private static void apoExp$getAffixesImpl(ItemStack stack, CallbackInfoReturnable<Map<DynamicHolder<? extends Affix>, AffixInstance>> cir) {
        if (stack.isEmpty()) {
            cir.setReturnValue(Collections.emptyMap());
            return;
        }

        CompoundTag afxData = stack.getTagElement("affix_data");
        if (afxData == null || !afxData.contains("affixes")) {
            cir.setReturnValue(Collections.emptyMap());
            return;
        }

        CompoundTag affixes = afxData.getCompound("affixes");
        DynamicHolder<LootRarity> globalRarity = AffixHelper.getRarity(afxData);
        if (!globalRarity.isBound()) {
            globalRarity = RarityRegistry.getMinRarity();
        }

        CompoundTag raritiesTag = afxData.contains("affix_rarities") ? afxData.getCompound("affix_rarities") : null;
        LootCategory cat = LootCategory.forItem(stack);
        Map<DynamicHolder<? extends Affix>, AffixInstance> map = new HashMap<>();

        for (String key : affixes.getAllKeys()) {
            try {
                ResourceLocation affixId = new ResourceLocation(key);
                DynamicHolder<Affix> affix = AffixRegistry.INSTANCE.holder(affixId);
                if (!affix.isBound()) continue;

                DynamicHolder<LootRarity> affixRarity = globalRarity;
                if (raritiesTag != null && raritiesTag.contains(key)) {
                    ResourceLocation rKey = new ResourceLocation(raritiesTag.getString(key));
                    DynamicHolder<LootRarity> rHolder = RarityRegistry.INSTANCE.holder(rKey);
                    if (rHolder != null && rHolder.isBound()) {
                        affixRarity = rHolder;
                    }
                }

                if (affix.get().canApplyTo(stack, cat, affixRarity.get())) {
                    float lvl = affixes.getFloat(key);
                    map.put(affix, new AffixInstance(affix, stack, affixRarity, lvl));
                }
            } catch (Throwable ignored) {
            }
        }

        cir.setReturnValue(Collections.unmodifiableMap(map));
    }

    @Inject(method = "setAffixes", at = @At("RETURN"), remap = false)
    private static void apoExp$onSetAffixes(ItemStack stack, Map<DynamicHolder<? extends Affix>, AffixInstance> affixes, CallbackInfo ci) {
        if (stack.isEmpty() || affixes == null) return;
        CompoundTag afxData = stack.getOrCreateTagElement("affix_data");
        CompoundTag raritiesTag = afxData.contains("affix_rarities") ? afxData.getCompound("affix_rarities") : new CompoundTag();
        for (AffixInstance inst : affixes.values()) {
            if (inst != null && inst.affix() != null && inst.affix().isBound() && inst.rarity() != null && inst.rarity().isBound()) {
                ResourceLocation rKey = RarityRegistry.INSTANCE.getKey(inst.rarity().get());
                if (rKey != null) {
                    raritiesTag.putString(inst.affix().getId().toString(), rKey.toString());
                }
            }
        }
        afxData.put("affix_rarities", raritiesTag);
    }
}
