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

    /**
     * Cleanroom post-processor: Intercepts the return value of getAffixesImpl to restore
     * per-affix original rarities and preserve tier compatibility without duplicating any base mod code.
     */
    @Inject(method = "getAffixesImpl", at = @At("RETURN"), cancellable = true, remap = false)
    private static void apoExp$adjustAffixRarities(ItemStack stack, CallbackInfoReturnable<Map<DynamicHolder<? extends Affix>, AffixInstance>> cir) {
        if (stack.isEmpty()) return;

        CompoundTag afxData = stack.getTagElement("affix_data");
        if (afxData == null || !afxData.contains("affix_rarities")) return;

        CompoundTag raritiesTag = afxData.getCompound("affix_rarities");
        CompoundTag affixesTag = afxData.contains("affixes") ? afxData.getCompound("affixes") : null;
        if (affixesTag == null) return;

        Map<DynamicHolder<? extends Affix>, AffixInstance> originalMap = cir.getReturnValue();
        Map<DynamicHolder<? extends Affix>, AffixInstance> resultMap = new HashMap<>(originalMap);
        LootCategory cat = LootCategory.forItem(stack);
        boolean modified = false;

        // 1. Ensure existing instances in the map use their recorded affix rarity
        for (Map.Entry<DynamicHolder<? extends Affix>, AffixInstance> entry : originalMap.entrySet()) {
            DynamicHolder<? extends Affix> holder = entry.getKey();
            AffixInstance inst = entry.getValue();
            if (holder != null && holder.isBound() && inst != null) {
                String affixIdStr = holder.getId().toString();
                if (raritiesTag.contains(affixIdStr)) {
                    try {
                        ResourceLocation rKey = new ResourceLocation(raritiesTag.getString(affixIdStr));
                        DynamicHolder<LootRarity> targetRarity = RarityRegistry.INSTANCE.holder(rKey);
                        if (targetRarity != null && targetRarity.isBound() && !targetRarity.equals(inst.rarity())) {
                            resultMap.put(holder, new AffixInstance(holder, stack, targetRarity, inst.level()));
                            modified = true;
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }
        }

        // 2. Recover any affixes that were dropped by vanilla getAffixesImpl due to global rarity change
        for (String key : affixesTag.getAllKeys()) {
            try {
                ResourceLocation affixId = new ResourceLocation(key);
                DynamicHolder<Affix> holder = AffixRegistry.INSTANCE.holder(affixId);
                if (holder == null || !holder.isBound() || resultMap.containsKey(holder)) continue;

                if (raritiesTag.contains(key)) {
                    ResourceLocation rKey = new ResourceLocation(raritiesTag.getString(key));
                    DynamicHolder<LootRarity> targetRarity = RarityRegistry.INSTANCE.holder(rKey);
                    if (targetRarity != null && targetRarity.isBound()) {
                        if (holder.get().canApplyTo(stack, cat, targetRarity.get())) {
                            float lvl = affixesTag.getFloat(key);
                            resultMap.put(holder, new AffixInstance(holder, stack, targetRarity, lvl));
                            modified = true;
                        }
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        if (modified) {
            cir.setReturnValue(Collections.unmodifiableMap(resultMap));
        }
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
