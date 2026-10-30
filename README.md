# Apothic Infinite Exponential Tweak

[English](#english) | [日本語](#japanese)

---

<a name="english"></a>
## English

An addon for **Apotheosis** and **Apothic Infinite** (Minecraft 1.20.1 Forge) that introduces exponential cost scaling for repeated reforges and removes the duplicate reforge limit by default.

### Key Features
1. **Default Maximum Duplicate Reforge Limit Bypass**
   - Automatically unlocks Apothic-Infinite's default limit on duplicate reforges (`max_affix_level`), setting it to the maximum allowable value (`1.0E300` / practically infinite).
   - Allows infinite stacking and scaling of equipment stats without manually editing configuration files.
2. **Exponential Reforge Cost Scaling**
   - When repeatedly reforging an item with the **same rarity tier**, the required amount of **Sigils of Reforging (Sigil of Rebirth)** and **Rarity Materials** increases exponentially:
     $$\text{Cost Multiplier} = 2^{(\text{Reforge Count} - 1)}$$
     - **1st Reforge:** $2^0 = 1\times$ (Standard cost)
     - **2nd Reforge:** $2^1 = 2\times$
     - **3rd Reforge:** $3\text{rd} \rightarrow 2^2 = 4\times$
     - **4th Reforge:** $4\text{th} \rightarrow 2^3 = 8\times$
     - **$n$-th Reforge:** $2^{n-1}\times$
   - **Common Affix Reforging Mechanics:**
     - While vanilla Apotheosis does not consume Sigils of Reforging for Common affixes (base cost: 0), this mod establishes an effective base requirement of 1 Sigil when **re-reforging** an existing Common item (2nd reforge onwards) to ensure consistent exponential progression:
       - **1st Reforge (Initial):** 0 Sigils, 1 Common Material
       - **2nd Reforge:** 1 Sigil, 2 Common Materials
       - **3rd Reforge:** 2 Sigils, 4 Common Materials
       - **4th Reforge:** 4 Sigils, 8 Common Materials
3. **Infinite Stack Reforging Slots (No Inventory Drain)**
   - To completely eliminate accidental material consumption from player inventories, automatic inventory draining has been removed.
   - Instead, the stack limit for **Sigils of Reforging** and **Rarity Materials** slots across all 4 reforging tables (Simple Reforging Table, Reforging Table, Ancient Reforging Table, and Esoteric Reforging Table) is unlocked to `Integer.MAX_VALUE`.
   - Players can stack hundreds or thousands of materials directly inside the table slots. Reforging operations strictly consume items placed in the table slots only.
4. **Item Tooltips & Visual Feedback**
   - Shows the previous reforge count and next cost multiplier on item tooltips.
   - Reforge table interface clearly displays current scaled costs in real-time.
5. **Cross-Mod Compatibility**
   - Built-in soft compatibility with addon tables such as *Ancient Reforging* and *Esoteric Reforging*.

### Requirements
- Minecraft 1.20.1
- Minecraft Forge (47.1.0 or higher recommended)
- [Apotheosis](https://www.curseforge.com/minecraft/mc-mods/apotheosis) (7.0.0+)
- [Apothic Infinite](https://www.curseforge.com/minecraft/mc-mods/apothic-infinite-apotheosis-unofficial-addon)

---

<a name="japanese"></a>
## 日本語

Minecraft 1.20.1 Forge環境向け、**Apotheosis** および **Apothic Infinite** のアドオンMODです。
重複鍛造回数の上限をデフォルトで最大値に開放するとともに、同一レアリティでの2回目以降の再鍛造に指数関数的なコスト増加を適用し、インフレとゲームバランスを両立します。

### 主な特徴
1. **重複鍛造回数のデフォルト最大値化 (上限完全開放)**
   - Apothic-Infiniteの重複鍛造上限（`max_affix_level`、デフォルト10.0）を自動的に最大値（実質無限大）へバイパスします。
   - コンフィグを手動で編集することなく、導入するだけでアフィックス効果を限界なく重複強化できるようになります。
2. **指数関数的な鍛造コスト増加 (Exponential Cost)**
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
3. **再鍛造テーブルスロットのスタック上限無制限化（インベントリ誤消費の根絶）**
   - インベントリからの意図しない素材消費事故を完全に防ぐため、インベントリからの自動消費仕様を廃止しました。
   - 代わりに、全4種の再鍛造台（簡易再鍛造台、再鍛造台、古代の再鍛造台、秘術の再鍛造台）において、「再鍛造の紋章」スロットおよび「レアリティマテリアル」スロットのスタック上限を `Integer.MAX_VALUE`（実質無制限）に拡張しました。
   - プレイヤーはスロット内に64個を超えて何百・何千個もの素材を直接スタックして投入でき、鍛造コストはスロット内の素材からのみ安全に消費されます。
4. **ツールチップ＆UI情報表示**
   - アイテムのツールチップに、現在のレアリティにおける鍛造回数と次回鍛造時のコスト倍率を表示します。
   - 再鍛造台のGUI上でも正確な必要個数が赤色/灰色でリアルタイム表示されます。
5. **高いアドオン親和性**
   - *Ancient Reforging* や *Esoteric Reforging* などの追加リフォージ台MODにも柔軟に対応しています。

### 導入要件
- Minecraft 1.20.1
- Minecraft Forge (47.1.0以上推奨)
- [Apotheosis](https://www.curseforge.com/minecraft/mc-mods/apotheosis) (7.0.0以上)
- [Apothic Infinite](https://www.curseforge.com/minecraft/mc-mods/apothic-infinite-apotheosis-unofficial-addon)
