# Configuration Guide / コンフィグ説明書

[English](#english) | [日本語](#japanese)

The server configuration file is saved at `config/apothic_infinite_exponential_tweak-server.toml` (or `world/serverconfig/apothic_infinite_exponential_tweak-server.toml`).

---

<a name="english"></a>
## English

| Parameter | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `override_max_affix_level_to_max` | `Boolean` | `true` | If true, overrides Apothic-Infinite's duplicate reforge limit (`max_affix_level`) to the maximum possible value, enabling unlimited affix stacking by default. |
| `custom_max_affix_level` | `Double` | `1.0E300` | The custom maximum affix level multiplier to use if `override_max_affix_level_to_max` is true. |
| `limit_enchantment_level_to_apotheosis_plus_two` | `Boolean` | `false` | If true, overrides Apothic-Infinite's global enchantment level cap (default 100) and limits each enchantment to its Apotheosis default maximum level + 2. |
| `base_exponent` | `Double` | `2.0` | The base of the exponential cost calculation ($M = \text{base}^{\text{count}-1}$). Default is 2.0 (1x, 2x, 4x, 8x...). |
| `scale_sigil_cost` | `Boolean` | `true` | Whether the Sigils of Reforging (Sigil of Rebirth) cost scales exponentially on repeated reforges. |
| `scale_material_cost` | `Boolean` | `true` | Whether the rarity materials cost scales exponentially on repeated reforges. |
| `max_cost_cap` | `Integer` | `64000` | The maximum cost cap for items per reforge operation to prevent unintended overflow. |
| `reset_count_on_rarity_change` | `Boolean` | `false` | If true, reforging with a different rarity tier resets the reforge count. If false (default), reforge counts are tracked separately per rarity tier. |
| `scale_augment_cost` | `Boolean` | `true` | If true, upgrades beyond the default maximum level (1.0F) at the Augmenting Table exponentially scale the required Sigils of Enhancement. |
| `augment_base_exponent` | `Double` | `2.0` | The exponential multiplier for Augmenting Table upgrades beyond 1.0F ($2 \times \text{base}^{\text{over\_count}+1}$, e.g. 4, 8, 16, 32...). |
| `show_reforge_count_tooltip` | `Boolean` | `true` | Whether to display the reforge count and next cost multiplier on item tooltips. |
| `show_augment_tooltip` | `Boolean` | `true` | Whether to display the over-level augment upgrade count on item tooltips. |

---

<a name="japanese"></a>
## 日本語

サーバー設定ファイルは `config/apothic_infinite_exponential_tweak-server.toml`（またはセーブデータフォルダ内の `world/serverconfig/apothic_infinite_exponential_tweak-server.toml`）に生成されます。

| 設定項目名 | データ型 | デフォルト値 | 挙動・説明 |
| :--- | :--- | :--- | :--- |
| `override_max_affix_level_to_max` | `Boolean` | `true` | trueの場合、Apothic-Infiniteの重複鍛造回数上限（`max_affix_level`）をデフォルトで最大値に開放し、無限の重複強化を可能にします。 |
| `custom_max_affix_level` | `Double` | `1.0E300` | 上記がtrueの場合に適用されるアフィックスレベルの最大倍率（実質無制限）。 |
| `limit_enchantment_level_to_apotheosis_plus_two` | `Boolean` | `false` | trueの場合、Apothic-Infiniteの一律エンチャント上限（デフォルト100）を上書きし、各エンチャントの上限をApotheosisデフォルト上限+2に制限します。 |
| `base_exponent` | `Double` | `2.0` | 指数コスト計算の底（基数）。計算式: $2^{(\text{回数}-1)}$。デフォルト2.0（1倍、2倍、4倍、8倍…）。 |
| `scale_sigil_cost` | `Boolean` | `true` | trueの場合、再鍛造の紋章（Sigil of Rebirth）の必要数を指数関数的に増加させます。 |
| `scale_material_cost` | `Boolean` | `true` | trueの場合、レアリティマテリアルの必要数を指数関数的に増加させます。 |
| `max_cost_cap` | `Integer` | `64000` | 1回の鍛造で要求される素材数の最大上限値（整数オーバーフローや極端な過大請求を防止）。 |
| `reset_count_on_rarity_change` | `Boolean` | `false` | trueの場合、異なるレアリティで鍛造すると回数がリセットされます。false（推奨）の場合、レアリティごとに個別に鍛造回数が保持されます。 |
| `scale_augment_cost` | `Boolean` | `true` | trueの場合、補強テーブルでデフォルト最大値（1.0F）を超えた値の強化時に「補強の紋章」の必要数を指数関数的に倍加させます。 |
| `augment_base_exponent` | `Double` | `2.0` | 補強テーブルにおける1.0F超え強化の指数倍率（計算式: $2 \times 2^{\text{突破回数}+1}$、例: 4個、8個、16個、32個…）。 |
| `show_reforge_count_tooltip` | `Boolean` | `true` | trueの場合、アイテムのツールチップに鍛造回数と次回コスト倍率を表示します。 |
| `show_augment_tooltip` | `Boolean` | `true` | trueの場合、アイテムのツールチップに補強上限突破回数を表示します。 |
