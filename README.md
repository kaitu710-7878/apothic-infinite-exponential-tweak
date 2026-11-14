# Apothic Infinite Exponential Tweak

[English](#english) | [日本語](#japanese)

---

<a name="english"></a>
## English

An addon for **Apotheosis** and **Apothic Infinite** (Minecraft 1.20.1 Forge) that introduces exponential cost scaling for repeated reforges, enables multi-tier affix coexistence and simultaneous application, fixes socket doubling bugs, and unlocks the duplicate reforge limit by default.

### Key Features

1. **Multi-Tier Affix Coexistence & Simultaneous Application**
   - **Cross-Tier Preservation:** Reforging an item across different rarity tiers (Common through Mythic, as well as addon tiers such as Esoteric, Heirloom, and Artifact from *Apotheotic Additions*) no longer wipes out existing affixes. Affixes from previous tiers (Attribute modifiers, Potion effects, etc.) are fully inherited and applied simultaneously.
   - **Per-Affix Rarity Isolation:** Each affix persistently tracks the exact rarity tier it was rolled under (`affix_rarities` NBT). This prevents stat calculation mismatch where affixes evaluate using the wrong tier's step functions.
   - **Abnormal Inflation Protection:** Completely prevents cross-tier stat explosion bugs (e.g. an Esoteric reach affix jumping from +9 to +23 when reforging into Mythic). When reforging into a different tier, existing affixes retain their original levels and tiers safely.

2. **Controlled Multi-Reforge & Tooltip Management**
   - **New Tooltip Entries on Re-roll:** When reforging within the same tier multiple times, newly rolled affixes generate fresh tooltip entries without tampering with untouched existing Attribute buffs.
   - **Legitimate Duplicate Stacking:** If the exact same affix is re-rolled within the same tier, legitimate duplicate enhancement (level addition) is permitted.
   - **Selective Integration:** Durability exemption (`DurableAffix`) and armor damage reductions (`DamageReductionAffix`) are automatically unified upon duplicate rolls.
   - **99% Damage Reduction Hard Cap:** Specific damage reduction affixes are strictly capped at 99% (0.99F) across incoming damage calculation, item tooltips, and augmenting screens, preserving game balance.

3. **True Additive Socket Scaling (Eliminating Doubling & Freezing Bugs)**
   - **Root-Cause Fix:** Fixes the severe bug where reforging an item with existing sockets (e.g., 16 sockets) caused Apothic-Infinite to double the socket count on each reforge (16 -> 32 -> 50 / cap) due to Apotheosis preserving input sockets inside `input.copy()`.
   - **Same-Tier & Cross-Tier Pure Addition ($S_{\text{input}} + S_{\text{roll}}$):** Whether repeatedly reforging within the exact same tier (such as Esoteric, which guarantees 5 sockets per roll, advancing $5 \to 10 \to 15 \to 20 \dots$) or cross-reforging between different tiers (Common through Mythic and addon tiers), socket counts strictly scale additively.
   - **Pre-Roll Socket Decoupling:** Newly rolled sockets are dynamically decoupled during candidate creation, ensuring that rolled sockets are purely added on top of existing sockets across standard, Ancient, and Esoteric Reforging Tables.

4. **Default Maximum Duplicate Reforge Limit Bypass**
   - Automatically unlocks Apothic-Infinite's default limit on duplicate reforges (`max_affix_level`), setting it to the maximum allowable value (`1.0E300` / practically infinite).
   - Allows infinite stacking and scaling of equipment stats without manually editing configuration files.

5. **Exponential Reforge Cost Scaling**
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

6. **Infinite Stack Reforging Slots (No Inventory Drain)**
   - To completely eliminate accidental material consumption from player inventories, automatic inventory draining has been removed.
   - Instead, the stack limit for **Sigils of Reforging** and **Rarity Materials** slots across all 4 reforging tables (Simple Reforging Table, Reforging Table, Ancient Reforging Table, and Esoteric Reforging Table) is unlocked to `Integer.MAX_VALUE`.
   - Players can stack hundreds or thousands of materials directly inside the table slots. Reforging operations strictly consume items placed in the table slots only.

7. **Augmenting Table Over-Cap Scaling & Unlimited Slot Limit**
   - When upgrading an affix beyond the vanilla Apotheosis limit (1.0F) at the **Augmenting Table**, the required amount of **Sigils of Enhancement** doubles exponentially each upgrade:
     - **Normal upgrades ($\le 1.0\text{F}$):** 2 Sigils (Vanilla cost)
     - **1st Breakthrough ($1.00\text{F} \rightarrow 1.25\text{F}$):** $2 \times 2^1 = 4$ Sigils
     - **2nd Breakthrough ($1.25\text{F} \rightarrow 1.50\text{F}$):** $2 \times 2^2 = 8$ Sigils
     - **3rd Breakthrough ($1.50\text{F} \rightarrow 1.75\text{F}$):** $2 \times 2^3 = 16$ Sigils
     - **$n$-th Breakthrough:** $2 \times 2^n$ Sigils
   - The Sigil of Enhancement slot stack limit in the Augmenting Table is also unlocked to `Integer.MAX_VALUE`.
   - The screen button tooltips and gray-out conditions are completely synchronized in real time with the actual required costs.

8. **Item Tooltips & Visual Feedback**
   - Shows the previous reforge count and next cost multiplier on item tooltips.
   - Reforge table interface clearly displays current scaled costs in real-time.

9. **Cross-Mod Compatibility & Cleanroom License Compliance**
   - Full soft compatibility with addon tables such as *Ancient Reforging*, *Esoteric Reforging*, and *Apotheotic Additions*.
   - Strictly respects Apotheosis's All Rights Reserved (ARR) license through 100% cleanroom Mixin interception, containing zero copied base code.

10. **Optional Apotheosis Default + 2 Enchantment Level Cap**
    - Includes an optional configuration (`limit_enchantment_level_to_apotheosis_plus_two`, disabled `false` by default) to replace Apothic-Infinite's uniform enchantment level cap (default 100) with each enchantment's Apotheosis default maximum level + 2.
    - Consistently enforces this per-enchantment cap across enchanting tables (including Apothic-Infinite's additive re-enchanting), anvils, loot generation, and item enchantment reading, while respecting Apothic-Infinite's `allow_high_level_curses` setting for curses.

### Requirements
- Minecraft 1.20.1
- Minecraft Forge (47.1.0 or higher recommended)
- [Apotheosis](https://www.curseforge.com/minecraft/mc-mods/apotheosis) (7.0.0+)
- [Apothic Infinite](https://www.curseforge.com/minecraft/mc-mods/apothic-infinite-apotheosis-unofficial-addon)

---

<a name="japanese"></a>
## 日本語

Minecraft 1.20.1 Forge環境向け、**Apotheosis** および **Apothic Infinite** のアドオンMODです。
複数ティアにわたるアフィックスの同時付与・同時適用、ソケット倍増バグの解消、重複鍛造回数上限の開放、および同一レアリティでの再鍛造に対する指数関数的なコスト増加を提供します。

### 主な特徴

1. **複数ティア（異なるレアリティ）のアフィックス同時保持・同時適用**
   - **ティア間の効果引き継ぎ:** 通常のコモン〜ミシックだけでなく、*Apotheotic Additions* 等の拡張MODで追加される上位ティア（Esoteric、Heirloom、Artifact など）を含む異なるティアで再鍛造を行っても、過去のティアで付与されたアフィックス（Attributeバフ、ポーション効果等）が消滅せず、アイテムに引き継がれて同時に適用されます。
   - **アフィックス個別レアリティ管理:** 各アフィックスが付与された当時のレアリティ情報（`affix_rarities` NBT）を永続保持するため、異なるティアが混在しても各アフィックス本来の計算テーブル・ステップ関数に従って正確に威力を発揮します。
   - **異常インフレの防止:** 異なるティア同士での再鍛造時に計算テーブルの差異によって数値が異常に跳ね上がる不具合（例: エソテリックのリーチがミシック付与時に 9 $\to$ 23 へ跳ね上がる現象）を防止。既存アフィックスの旧レアリティとレベルを安全に保護します。

2. **同一ティア再鍛造の制御とツールチップ管理**
   - **再抽選時の新規項目生成:** 同一ティアで2回目以降の再鍛造を行った場合、新しくロールされたアフィックスはツールチップ上部の新規項目として追加され、今回抽選されなかった既存のAttributeバフ項目が勝手に伸びることはありません。
   - **同一アフィックスの重複強化:** 同一ティア内で同じアフィックスが重複抽選された場合は、正当な重複強化枠としてレベル加算（強化）が適用されます。
   - **特定項目の自動統合:** 耐久力免除（`DurableAffix`）および防具の特定属性ダメージ軽減（`DamageReductionAffix`）は重複時に自動的に統合されます。
   - **ダメージ軽減 99% 上限キャップ:** 特定属性ダメージ軽減は、重複強化を重ねても最大 99% (0.99F) にクランプされ、完全無敵化を防ぎつつ実質的な最大耐性を確保します（ダメージ計算、ツールチップ、補強画面すべてに反映）。

3. **ジェムスロット（ソケット）の純粋加算化（倍増・停止バグの完全解消）**
   - **倍増バグの根絶:** Apotheosis がスロット0の `input.copy()`（既存ソケット数を保持した状態）をベースに再鍛造候補をロールするため、既存ソケットがあるアイテムを再鍛造すると Apothic-Infinite の加算処理によってソケット数が 16 $\to$ 32 $\to$ 50 と倍増（掛け算）してしまう致命的な不具合を解消しました。
   - **同一ティア連続・異種ティア間の完全純粋加算 ($S_{\text{input}} + S_{\text{roll}}$):** エソテリック等の高ソケット確定ティア（1回あたり5ソケット確定ロール）で別ティアを挟まず同一ティアだけで連続再鍛造した場合（$5 \to 10 \to 15 \to 20 \dots$）でも、ミシック等の確率ロールティア（$+0 \sim 4$個）や他ティアを跨いだ再鍛造でも、常にロール結果が既存ソケット数に着実に純粋加算されます。
   - **事前ソケット分離機構:** 候補アイテム生成時に事前のソケット数分離を行うことで、倍増（掛け算）バグを根絶するとともに、ロール増分が消去される誤判定トラブルを完全解消。通常、Ancient、Esoteric の全再鍛造台に対応しています。

4. **重複鍛造回数のデフォルト最大値化 (上限完全開放)**
   - Apothic-Infiniteの重複鍛造上限（`max_affix_level`、デフォルト10.0）を自動的に最大値（実質無限大）へバイパスします。
   - コンフィグを手動で編集することなく、導入するだけでアフィックス効果を限界なく重複強化できるようになります。

5. **指数関数的な鍛造コスト増加 (Exponential Cost)**
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

6. **再鍛造テーブルスロットのスタック上限無制限化（インベントリ誤消費の根絶）**
   - インベントリからの意図しない素材消費事故を完全に防ぐため、インベントリからの自動消費仕様を廃止しました。
   - 代わりに、全4種の再鍛造台（簡易再鍛造台、再鍛造台、古代の再鍛造台、秘術の再鍛造台）において、「再鍛造の紋章」スロットおよび「レアリティマテリアル」スロットのスタック上限を `Integer.MAX_VALUE`（実質無制限）に拡張しました。
   - プレイヤーはスロット内に64個を超えて何百・何千個もの素材を直接スタックして投入でき、鍛造コストはスロット内の素材からのみ安全に消費されます。

7. **補強テーブル（Augmenting Table）の上限突破指数コスト＆スロット上限開放**
   - **補強テーブル**において、バニラApotheosisの上限（1.0F）を超えた値の強化を行うたびに、要求される「**補強の紋章 (Sigil of Enhancement)**」の必要数が倍増します：
     - **通常強化（1.0F以下）:** 2個（バニラ通り）
     - **上限突破 1回目（$1.00\text{F} \rightarrow 1.25\text{F}$）:** $2 \times 2^1 = 4$個
     - **上限突破 2回目（$1.25\text{F} \rightarrow 1.50\text{F}$）:** $2 \times 2^2 = 8$個
     - **上限突破 3回目（$1.50\text{F} \rightarrow 1.75\text{F}$）:** $2 \times 2^3 = 16$個
     - **上限突破 $n$回目:** $2 \times 2^n$個
   - 補強テーブルの「補強の紋章」スロット上限も `Integer.MAX_VALUE`（実質無制限）に開放され、数百個以上の紋章を直接投入可能です。
   - 画面上のボタンツールチップ表示および不足時の非活性化判定も動的コストと100%リアルタイム連動します。

8. **ツールチップ＆UI情報表示**
   - アイテムのツールチップに、現在のレアリティにおける鍛造回数と次回鍛造時のコスト倍率を表示します。
   - 再鍛造台のGUI上でも正確な必要個数が赤色/灰色でリアルタイム表示されます。

9. **高いアドオン親和性＆クリーンルーム著作権遵守**
   - *Ancient Reforging*、*Esoteric Reforging*、*Apotheotic Additions* などの主要アドオンと高水準で連動。
   - Apotheosis 本体の著作権（All Rights Reserved）を厳格に遵守し、ベースMODのコードコピーを一切含まない 100% クリーンルーム実装を確立しています。

10. **エンチャント上限の Apotheosis デフォルト + 2 制限オプション**
    - Apothic-Infinite の一律エンチャントレベル上限（デフォルト 100）を、各エンチャントの Apotheosis デフォルト上限 + 2 に変更するオプション設定（`limit_enchantment_level_to_apotheosis_plus_two`、デフォルト `false`）を搭載しています。
    - エンチャントテーブル（Apothic-Infinite の重ねがけ加算を含む）、金床、ルート生成、およびアイテムのエンチャント取得処理のすべてで各エンチャント上限 + 2 を一貫して適用し、呪いエンチャントについては Apothic-Infinite の `allow_high_level_curses` 設定を尊重します。

### 導入要件
- Minecraft 1.20.1
- Minecraft Forge (47.1.0 以上推奨)
- [Apotheosis](https://www.curseforge.com/minecraft/mc-mods/apotheosis) (7.0.0+)
- [Apothic Infinite](https://www.curseforge.com/minecraft/mc-mods/apothic-infinite-apotheosis-unofficial-addon)
