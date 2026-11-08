# Apothic Infinite Exponential Tweak

[English](#english) | [日本語](#japanese)

---

<a name="english"></a>
## English

An addon for **Apotheosis** and **Apothic Infinite** (Minecraft 1.20.1 Forge) that introduces exponential cost scaling for repeated reforges, cross-tier multi-affix coexistence, smart duplicate slot management, and unlimited material stacking.

### Key Features

1. **Cross-Tier Multi-Affix Coexistence & Simultaneous Application**
   - **Simultaneous Application Across Tiers:** Reforging an item into a different rarity tier (e.g., Mythic -> Esoteric, Heirloom, Artifact) preserves all previously acquired affixes instead of overwriting or deleting them. Attribute modifiers, potion effects, and unique passive abilities from different tiers apply concurrently.
   - **Full Apotheotic Additions Tier Support:** Fully compatible with extended rarity tiers from *Apotheotic Additions* (Esoteric, Heirloom, Artifact, etc.).
   - **Per-Affix Rarity Isolation (`affix_rarities`):** Each affix retains its own original tier and step-function calculation table. Reforging cross-tier will never cause abnormal stat spikes (such as weapon reach unexpectedly exploding from 9 to 23), ensuring fair and balanced progression across diverse tiers.

2. **Same-Tier Multi-Reforging & Controlled Duplicate Growth**
   - **New Tooltip Entries on Re-roll:** When reforging within the same tier, newly rolled affixes generate distinct tooltip entries and modifiers.
   - **Unintended Buff Inflation Prevention:** Existing Attribute modifiers that were not rolled during the current reforge remain unaltered, preventing unintended automatic scaling of untouched stats.
   - **Selective Stacking:** Durability exemption (`DurableAffix`) and armor-specific damage reductions (`DamageReductionAffix`) are automatically consolidated and merged into unified modifiers upon duplicate roll.

3. **Damage Reduction 99% Cap**
   - Specific damage reduction affixes (physical, fire, explosion, projectile, etc.) are strictly capped at **99% (0.99F)** across actual in-game damage calculations, item tooltip descriptions, and the Augmenting Table interface.

4. **True Socket Addition (No Exponential Doubling)**
   - Corrected Apothic-Infinite's socket calculation from unintended multiplication ($16 \rightarrow 32 \rightarrow 50$) to **pure mathematical addition**:
     $$S_{\text{new}} = S_{\text{input}} + S_{\text{roll}}$$
   - Sockets increase strictly by the amount rolled in the current reforge (e.g., $16 + 2 = 18$), fully working across standard, Ancient, and Esoteric Reforging Tables.

5. **Default Maximum Duplicate Reforge Limit Bypass**
   - Automatically unlocks Apothic-Infinite's default limit on duplicate reforges (`max_affix_level`), setting it to the maximum allowable value (`1.0E300` / practically infinite).
   - Allows infinite stacking and scaling of equipment stats without manually editing configuration files.

6. **Exponential Reforge Cost Scaling**
   - When repeatedly reforging an item with the **same rarity tier**, the required amount of **Sigils of Reforging (Sigil of Rebirth)** and **Rarity Materials** increases exponentially:
     $$\text{Cost Multiplier} = 2^{(\text{Reforge Count} - 1)}$$
     - **1st Reforge:** $2^0 = 1\times$ (Standard cost)
     - **2nd Reforge:** $2^1 = 2\times$
     - **3rd Reforge:** $2^2 = 4\times$
     - **4th Reforge:** $2^3 = 8\times$
     - **$n$-th Reforge:** $2^{n-1}\times$
   - **Common Affix Reforging Mechanics:**
     - While vanilla Apotheosis does not consume Sigils of Reforging for Common affixes (base cost: 0), this mod establishes an effective base requirement of 1 Sigil when **re-reforging** an existing Common item (2nd reforge onwards) to ensure consistent exponential progression:
       - **1st Reforge (Initial):** 0 Sigils, 1 Common Material
       - **2nd Reforge:** 1 Sigil, 2 Common Materials
       - **3rd Reforge:** 2 Sigils, 4 Common Materials
       - **4th Reforge:** 4 Sigils, 8 Common Materials

7. **Infinite Stack Reforging Slots (No Inventory Drain)**
   - To completely eliminate accidental material consumption from player inventories, automatic inventory draining has been removed.
   - Instead, the stack limit for **Sigils of Reforging** and **Rarity Materials** slots across all 4 reforging tables (Simple Reforging Table, Reforging Table, Ancient Reforging Table, and Esoteric Reforging Table) is unlocked to `Integer.MAX_VALUE`.
   - Players can stack hundreds or thousands of materials directly inside the table slots. Reforging operations strictly consume items placed in the table slots only.

8. **Augmenting Table Over-Cap Scaling & Unlimited Slot Limit**
   - When upgrading an affix beyond the vanilla Apotheosis limit (1.0F) at the **Augmenting Table**, the required amount of **Sigils of Enhancement** doubles exponentially each upgrade:
     - **Normal upgrades ($\le 1.0\text{F}$):** 2 Sigils (Vanilla cost)
     - **1st Breakthrough ($1.00\text{F} \rightarrow 1.25\text{F}$):** $2 \times 2^1 = 4$ Sigils
     - **2nd Breakthrough ($1.25\text{F} \rightarrow 1.50\text{F}$):** $2 \times 2^2 = 8$ Sigils
     - **3rd Breakthrough ($1.50\text{F} \rightarrow 1.75\text{F}$):** $2 \times 2^3 = 16$ Sigils
     - **$n$-th Breakthrough:** $2 \times 2^n$ Sigils
   - The Sigil of Enhancement slot stack limit in the Augmenting Table is also unlocked to `Integer.MAX_VALUE`.
   - The screen button tooltips and gray-out conditions are completely synchronized in real time with the actual required costs.

9. **Item Tooltips & Visual Feedback**
   - Shows the previous reforge count and next cost multiplier on item tooltips.
   - Reforge table interface clearly displays current scaled costs in real-time.

10. **Cross-Mod Compatibility**
    - Seamless built-in support for addon tables including *Ancient Reforging*, *Esoteric Reforging*, and *Apotheotic Additions*.

### Requirements
- Minecraft 1.20.1
- Minecraft Forge (47.1.0 or higher recommended)
- [Apotheosis](https://www.curseforge.com/minecraft/mc-mods/apotheosis) (7.0.0+)
- [Apothic Infinite](https://www.curseforge.com/minecraft/mc-mods/apothic-infinite-apotheosis-unofficial-addon)

---

<a name="japanese"></a>
## 日本語

Minecraft 1.20.1 Forge環境向け、**Apotheosis** および **Apothic Infinite** のアドオンMODです。
複数ティアにわたるアフィックスの同時付与・同時適用、重複鍛造上限の開放、同一レアリティでの再鍛造に対する指数関数的コスト増加、ジェムソケットの正規足し算化、素材スロットのスタック上限無制限化を包括的に提供します。

### 主な特徴

1. **複数ティア（レアリティ）の同時付与・同時適用 (Cross-Tier Multi-Affix Coexistence)**
   - **異ティア効果の引き継ぎと同時発動:** 既存アイテムを異なるレアリティ（例: ミシック $\rightarrow$ エソテリック、エアルーム、アーティファクト等）で再鍛造しても、以前のティアで付与されたアフィックスが消滅せずすべて共存・適用されます。Attribute属性バフ、ポーション効果、特殊能力など、各ティアの強みが同時に発揮されます。
   - **Apotheotic Additions の全ティア完全対応:** *Apotheotic Additions* の独自レアリティ（Esoteric, Heirloom, Artifact 等）に完全対応しています。
   - **アフィックス固有レアリティの厳密保持 (`affix_rarities`):** 各アフィックスは付与された当時の計算テーブル（StepFunction）を内部タグで保持します。これにより、ティア跨ぎの再鍛造時に計算テーブルの不整合による異常な数値跳ね上がり（例: エソテリックで伸ばしたリーチがミシック付与時に 9 $\rightarrow$ 23 などに暴走する問題）を確実に防止します。

2. **同一ティアでの2回目以降の再鍛造と枠の制御**
   - **新規アフィックスの独立項目化:** 同一ティアで再鍛造を行った場合、新しくロールされたアフィックスはツールチップ上部の新規項目（独立したAttribute修正子）として追加されます。
   - **未選択バフの意図しない肥大化を防止:** 今回の再鍛造で抽選されていない既存のAttributeバフ項目が勝手に加算・増大してしまう不具合を解消しました。
   - **特定アフィックスのスマート統合:** 耐久力免除（`DurableAffix`）および防具の特定属性ダメージ軽減（`DamageReductionAffix`）は、重複時に自動的に1つの項目へ統合（合算）されます。

3. **特定属性ダメージ軽減の最大割合 99% キャップ規定**
   - 防具の特定属性ダメージ軽減（物理、火炎、爆発、落下、飛び道具等）の最大割合を **99% (0.99F)** に制限・規定しました。実際のダメージ計算、ツールチップ説明文、補強画面の表示すべてに上限キャップが適用されます。

4. **ジェムソケット（スロット）の純粋な足し算化 (掛け算の撤廃)**
   - 再鍛造時にソケット数が指数関数的・掛け算（$16 \rightarrow 32 \rightarrow 50$）に倍増するバグを根本解決しました。
   - 今回のリフォージでロールされた純粋なソケット枠数（$S_{\text{roll}}$）のみを既存ソケット数（$S_{\text{input}}$）へ加算する**純粋な足し算**を実現：
     $$S_{\text{new}} = S_{\text{input}} + S_{\text{roll}}$$
   - 通常の再鍛造台、古代の再鍛造台（Ancient）、秘術の再鍛造台（Esoteric）のすべてで安定して動作します。

5. **重複鍛造回数のデフォルト最大値化 (上限完全開放)**
   - Apothic-Infiniteの重複鍛造上限（`max_affix_level`、デフォルト10.0）を自動的に最大値（実質無限大）へバイパスします。
   - コンフィグを手動で編集することなく、導入するだけでアフィックス効果を限界なく重複強化できるようになります。

6. **指数関数的な鍛造コスト増加 (Exponential Cost)**
   - アイテムを**同一レアリティ**で繰り返し再鍛造する場合、「**再鍛造の紋章 (Sigil of Rebirth)**」および「**レアリティマテリアル**」の必要数が $2^{(\text{回数}-1)}$ 倍に増加します：
     - **1回目:** $2^0 = 1$倍（通常コスト）
     - **2回目:** $2^1 = 2$倍
     - **3回目:** $2^2 = 4$倍
     - **4回目:** $2^3 = 8$倍
     - **$n$回目:** $2^{n-1}$倍
   - **コモンアフィックス再鍛造時の仕様:**
     - 通常のApotheosisではコモンの基本レシピで「再鍛造の紋章」を消費しません（基本コスト: 0個）が、本MODでは同一レアリティのバランス統一のため、**既存のコモンアイテムを再鍛造する際（2回目以降）**、基本紋章コストを1個として指数スケールを適用します：
       - **1回目（初回付与）:** 紋章 0個、コモンマテリアル 1個
       - **2回目（再鍛造）:** 紋章 **1個**、コモンマテリアル **2個**
       - **3回目（再鍛造）:** 紋章 **2個**、コモンマテリアル **4個**
       - **4回目（再鍛造）:** 紋章 **4個**、コモンマテリアル **8個**

7. **再鍛造テーブルスロットのスタック上限無制限化（インベントリ誤消費の根絶）**
   - インベントリからの意図しない素材消費事故を完全に防ぐため、インベントリからの自動消費仕様を廃止しました。
   - 代わりに、全4種の再鍛造台（簡易再鍛造台、再鍛造台、古代の再鍛造台、秘術の再鍛造台）において、「再鍛造の紋章」スロットおよび「レアリティマテリアル」スロットのスタック上限を `Integer.MAX_VALUE`（実質無制限）に拡張しました。
   - プレイヤーはスロット内に64個を超えて何百・何千個もの素材を直接スタックして投入でき、鍛造コストはスロット内の素材からのみ安全に消費されます。

8. **補強テーブル（Augmenting Table）の上限突破指数コスト＆スロット上限開放**
   - **補強テーブル**において、バニラApotheosisの上限（1.0F）を超えた値の強化を行うたびに、要求される「**補強の紋章 (Sigil of Enhancement)**」の必要数が倍増します：
     - **通常強化（1.0F以下）:** 2個（バニラ通り）
     - **上限突破 1回目（$1.00\text{F} \rightarrow 1.25\text{F}$）:** $2 \times 2^1 = 4$個
     - **上限突破 2回目（$1.25\text{F} \rightarrow 1.50\text{F}$）:** $2 \times 2^2 = 8$個
     - **上限突破 3回目（$1.50\text{F} \rightarrow 1.75\text{F}$）:** $2 \times 2^3 = 16$個
     - **上限突破 $n$回目:** $2 \times 2^n$個
   - 補強テーブルの「補強の紋章」スロット上限も `Integer.MAX_VALUE`（実質無制限）に開放され、数百個以上の紋章を直接投入可能です。
   - 画面上のボタンツールチップ表示および不足時の非活性化判定も動的コストと100%リアルタイム連動します。

9. **ツールチップ＆UI情報表示**
   - アイテムのツールチップに、現在のレアリティにおける鍛造回数と次回鍛造時のコスト倍率を表示します。
   - 再鍛造台のGUI上でも正確な必要個数が赤色/灰色でリアルタイム表示されます。

10. **高いアドオン親和性**
    - *Ancient Reforging*、*Esoteric Reforging*、*Apotheotic Additions* などの追加MODとシームレスに連携します。

### 導入要件
- Minecraft 1.20.1
- Minecraft Forge (47.1.0 以上推奨)
- [Apotheosis](https://www.curseforge.com/minecraft/mc-mods/apotheosis) (7.0.0+)
- [Apothic Infinite](https://www.curseforge.com/minecraft/mc-mods/apothic-infinite-apotheosis-unofficial-addon)
