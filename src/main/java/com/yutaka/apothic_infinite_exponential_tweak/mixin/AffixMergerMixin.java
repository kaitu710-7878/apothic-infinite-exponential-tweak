package com.yutaka.apothic_infinite_exponential_tweak.mixin;

import com.maxwell.apothic_infnite.adventure.util.NameBuilder;
import com.yutaka.apothic_infinite_exponential_tweak.util.ExponentialCostHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.effect.DamageReductionAffix;
import dev.shadowsoffire.apotheosis.adventure.affix.effect.DurableAffix;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Pseudo
@Mixin(targets = "com.maxwell.apothic_infnite.adventure.util.AffixMerger", remap = false)
public abstract class AffixMergerMixin {

    @Dynamic
    @Inject(method = "mergeAffixes", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void apoExp$mergeAffixes(ItemStack input, ItemStack output, long rand, CallbackInfo ci) {
        if (input == null || output == null || input.isEmpty() || output.isEmpty()) {
            return;
        }

        try {
            // エンチャント本の BlockEntityTag 引継ぎ
            if (input.is(Items.ENCHANTED_BOOK) && input.hasTag() && input.getTag().contains("BlockEntityTag")) {
                output.getOrCreateTag().put("BlockEntityTag", input.getTag().getCompound("BlockEntityTag").copy());
            }

            Map<DynamicHolder<? extends Affix>, AffixInstance> oldMap = AffixHelper.getAffixes(input);
            Map<DynamicHolder<? extends Affix>, AffixInstance> newMap = AffixHelper.getAffixes(output);
            HashMap<DynamicHolder<? extends Affix>, AffixInstance> merged = new HashMap<>(oldMap);

            newMap.forEach((holder, newInst) -> {
                if (holder != null && holder.isBound()) {
                    merged.compute((DynamicHolder<? extends Affix>) holder, (h, oldInst) -> {
                        if (oldInst == null) {
                            return newInst;
                        }

                        Affix affix = h.get();
                        // 耐久力免除 (DurableAffix) および 特定属性ダメージ軽減 (DamageReductionAffix) は統合する
                        if (affix instanceof DurableAffix || affix instanceof DamageReductionAffix) {
                            return new AffixInstance(h, output, newInst.rarity(), oldInst.level() + newInst.level());
                        }

                        // それ以外（AttributeAffix, PotionAffix等）は既存バフが勝手に伸びないように旧インスタンスを維持する
                        return oldInst;
                    });
                }
            });

            // レアリティの決定（output または input）
            LootRarity rarity = AffixHelper.getRarity(output).getOptional()
                    .orElse(AffixHelper.getRarity(input).getOptional().orElse(null));
            if (rarity != null) {
                AffixHelper.setRarity(output, rarity);
            }

            // アフィックスごとのレアリティ保持 (affix_rarities)
            CompoundTag afxData = output.getOrCreateTagElement("affix_data");
            CompoundTag raritiesTag = afxData.contains("affix_rarities") ? afxData.getCompound("affix_rarities") : new CompoundTag();

            if (input.hasTag() && input.getTag().contains("affix_data")) {
                CompoundTag oldAfxData = input.getTag().getCompound("affix_data");
                if (oldAfxData.contains("affix_rarities")) {
                    raritiesTag.merge(oldAfxData.getCompound("affix_rarities"));
                }
            }

            for (AffixInstance inst : merged.values()) {
                if (inst != null && inst.affix() != null && inst.affix().isBound() && inst.rarity() != null && inst.rarity().isBound()) {
                    ResourceLocation rKey = RarityRegistry.INSTANCE.getKey(inst.rarity().get());
                    if (rKey != null) {
                        raritiesTag.putString(inst.affix().getId().toString(), rKey.toString());
                    }
                }
            }
            afxData.put("affix_rarities", raritiesTag);

            // アフィックス適用
            AffixHelper.setAffixes(output, merged);

            // Infinite 名前の生成・適用
            NameBuilder.buildAndApplyInfiniteName(output, rarity, rand);

            // 指数コスト計算用の再鍛造成功記録
            if (rarity == null) {
                String rId = ExponentialCostHelper.getItemRarityId(output);
                if (!rId.isEmpty()) {
                    try {
                        rarity = RarityRegistry.byLegacyId(rId).getOptional().orElse(null);
                    } catch (Throwable ignored) {
                    }
                }
            }
            if (rarity != null) {
                ExponentialCostHelper.recordReforgeSuccess(output, input, rarity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ci.cancel();
    }
}
