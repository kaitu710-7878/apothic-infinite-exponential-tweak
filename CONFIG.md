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
| `base_exponent` | `Double` | `2.0` | The base of the exponential cost calculation ($M = \text{base}^{\text{count}-1}$). Default is 2.0 (1x, 2x, 4x, 8x...). |
| `scale_sigil_cost` | `Boolean` | `true` | Whether the Sigils of Reforging (Sigil of Rebirth) cost scales exponentially on repeated reforges. |
| `scale_material_cost` | `Boolean` | `true` | Whether the rarity materials cost scales exponentially on repeated reforges. |
| `max_cost_cap` | `Integer` | `64000` | The maximum cost cap for items per reforge operation to prevent unintended overflow. |
| `enable_inventory_consumption` | `Boolean` | `true` | If true, when the required item count exceeds the table slot stack size (64), remaining items will be consumed from the player's inventory. |
| `reset_count_on_rarity_change` | `Boolean` | `false` | If true, reforging with a different rarity tier resets the reforge count. If false (default), reforge counts are tracked separately per rarity tier. |
| `show_reforge_count_tooltip` | `Boolean` | `true` | Whether to display the reforge count and next cost multiplier on item tooltips. |

---

<a name="japanese"></a>
## 日本語

サーバー設定ファイルは `config/apothic_infinite_exponential_tweak-server.toml`（またはセーブデータフォルダ内の `world/serverconfig/apothic_infinite_exponential_tweak-server.toml`）に生成されます。

| 設定項目名 | データ型 | デフォルト値 | 挙動・説明 |
| :--- | :--- | :--- | :--- |
| `override_max_affix_level_to_max` | `Boolean` | `true` | trueの場合、Apothic-Infiniteの重複鍛造回数上限（`max_affix_level`）をデフォルトで最大値に開放し、無限の重複強化を可能にします。 |
| `custom_max_affix_level` | `Double` | `1.0E300` | 上記がtrueの場合に適用されるアフィックスレベルの最大倍率（実質無制限）。 |
| `base_exponent` | `Double` | `2.0` | 指数コスト計算の底（基数）。計算式: $2^{(\text{回数}-1)}$。デフォルト2.0（1倍、2倍、4倍、8倍…）。 |
| `scale_sigil_cost` | `Boolean` | `true` | trueの場合、再鍛造の紋章（Sigil of Rebirth）の必要数を指数関数的に増加させます。 |
| `scale_material_cost` | `Boolean` | `true` | trueの場合、レアリティマテリアルの必要数を指数関数的に増加させます。 |
| `max_cost_cap` | `Integer` | `64000` | 1回の鍛造で要求される素材数の最大上限値（整数オーバーフローや極端な過大請求を防止）。 |
| `enable_inventory_consumption` | `Boolean` | `true` | trueの場合、必要コストがスロットのスタック上限（64個）を超えた際に、不足分をプレイヤーのインベントリから自動消費します。 |
| `reset_count_on_rarity_change` | `Boolean` | `false` | trueの場合、異なるレアリティで鍛造すると回数がリセットされます。false（推奨）の場合、レアリティごとに個別に鍛造回数が保持されます。 |
| `show_reforge_count_tooltip` | `Boolean` | `true` | trueの場合、アイテムのツールチップに鍛造回数と次回コスト倍率を表示します。 |