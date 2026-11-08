# Changelog / 変更履歴

[English](#english) | [日本語](#japanese)

---

<a name="english"></a>
## English

### [1.2.4] - 2026-11-08
#### Fixed
- Fixed an issue where repeatedly reforging an item within the same high-socket tier (such as Esoteric, which rolls 5 sockets guaranteed) without switching tiers failed to increase sockets. Eliminated the miscalculated fail-safe clamp in reforging menus that erroneously flagged legitimate same-tier socket additions ($S_{\text{roll}} \ge S_{\text{input}}$) as duplication bugs.
- Added pre-roll socket reset in `LootControllerMixin` (`@At("HEAD")`), ensuring that reforged item copies strictly start with 0 sockets before candidate generation, completely decoupling old sockets from candidate rolls.

### [1.2.3] - 2026-11-08
#### Fixed
- Fixed socket doubling multiplication bug (16 -> 32 -> 50) occurring in Esoteric and Ancient Reforging Tables by ensuring injection into obfuscated SRG method signatures (`m_6199_`) under `remap = false`.
- Introduced `LootControllerMixin` with a clean `@Redirect` on `SocketHelper.getSockets(stack)` during active reforges (`isReforgingActive()`), preventing Apotheosis's `createLootItem` from overriding freshly rolled socket counts ($S_{\text{roll}}$) with existing input socket counts via `Math.max(socketsRolled, inputSockets)`. As a result, Apothic-Infinite's socket accumulation evaluates strictly to a mathematically pure addition ($S_{\text{input}} + S_{\text{roll}}$) across all table types.
- Added fail-safe clamp in all reforging menus to guarantee sockets never multiply under any edge-case conditions.
#### Refactored
- Overhauled package namespace from account-based identifier to neutral `dev.tweak.apothic_infinite_exponential_tweak` in strict compliance with project guidelines.
- Verified 100% clean UTF-8 (without BOM) across all codebase files.

### [1.2.2] - 2026-11-08
#### Fixed
- Fixed socket doubling multiplication bug (16 -> 32 -> 50) where Apothic-Infinite added input sockets to choice sockets that already inherited input sockets from Apotheosis's `input.copy()`. Corrected to true addition ($S_{\text{input}} + S_{\text{roll}}$), ensuring sockets increment steadily by roll amount rather than doubling.

### [1.2.1] - 2026-11-08
#### Fixed
- Fixed exponential socket multiplication caused by Apothic-Infinite adding input and choice sockets on every slot update. Socket counts are now strictly corrected to `Math.max(inputSockets, rollSockets)`, completely stopping socket ballooning.
- Overhauled affix merging to allow same-tier duplicate rolls to enhance affix levels, while strictly isolating cross-tier duplicate additions to prevent stat explosion (such as reach 9 -> 23).
- Eliminated all base mod source code duplication in `AffixHelperMixin` to strictly respect Apotheosis's All Rights Reserved (ARR) license, rewriting it as a 100% cleanroom return-value post-processor.

### [1.2.0] - 2026-11-08
#### Added
- Added per-affix rarity persistence (`affix_rarities` tag), ensuring affixes from different tiers (including Apotheotic Additions tiers: Esoteric, Heirloom, Artifact, etc.) retain their original tier calculation tables and do not vanish when reforged into other tiers.
- Added multi-reforge affix merging control for repeated reforges of the same tier: newly rolled affixes generate new tooltip entries, while existing Attribute and Potion affixes maintain their level without unintended inflation.
- Added selective merging: only durability exemption (`DurableAffix`) and armor-specific damage reduction (`DamageReductionAffix`) are merged and summed upon duplicate roll.
- Added a 99% (0.99F) cap to damage reduction from `DamageReductionAffix` across gameplay damage calculation, tooltip descriptions, and augmenting text.
#### Fixed
- Fixed an issue where reforging an item with an Esoteric affix (such as reach) to Mythic caused reach to scale abnormally high (e.g. 9 -> 23) due to evaluation in Mythic's step functions and unbounded level addition.
- Fixed an issue where repeated reforges caused existing Attribute buff values to unexpectedly increase even when a new Attribute buff was generated.
- Fixed `AttributeAffix` modifier UUID generation to be deterministic and affix-unique, preventing UUID collisions and allowing distinct affixes with the same attribute to apply simultaneously.

### [1.1.1] - 2026-10-31
#### Fixed
- Fixed an issue where items reforged/merged via Apothic-Infinite (having affix levels >= 1.0F from reforges) were falsely penalized with exorbitant augment costs upon first entering the Augmenting Table.
- Augment cost doubling now strictly tracks the actual number of upgrades performed at the Augmenting Table beyond the Apotheosis default maximum level (1.0F), accurately scaling $2 \times 2^{\text{overCount}+1}$ (1st breakthrough: 4 sigils, 2nd: 8 sigils, etc.) without guessing from initial affix levels.
- Item tooltips now strictly display the true Augment Table breakthrough count.

### [1.1.0] - 2026-10-31
#### Added
- Added exponential cost scaling for Augmenting Table (Sigil of Enhancement) upgrades beyond the default maximum level (1.0F): 1st breakthrough (1.00F -> 1.25F) requires 4 sigils ($2 \times 2^1$), 2nd requires 8 sigils ($2 \times 2^2$), 3rd requires 16 sigils ($2 \times 2^3$), doubling exponentially. Normal upgrades (< 1.0F) remain at vanilla cost (2 sigils).
- Unlocked Sigil of Enhancement slot stack limit to Integer.MAX_VALUE in the Augmenting Table.
- Ensured block entity dirty state persistence (`tile.setChanged()`) and network synchronization on augment upgrade, preventing item stack loss across relog and save cycles.
- Dynamic UI tooltip and button state synchronization on Augmenting Table screen: accurately reflects current dynamic cost and gray-outs when sigils are insufficient, guaranteeing 100% consistency between display and actual consumption.
- Added tooltip indicators showing total augment breakthroughs on augmented items.

### [1.0.0] - 2026-10-31
#### Added
- Official Release 1.0.0: Fully verified and stabilized across Minecraft 1.20.1 Forge.
- Exponential cost scaling ($2^{\text{count}-1}$) for repeated same-rarity reforges.
- Dynamic 1-Sigil base requirement for 2nd+ reforges of Common affixes to ensure uniform progression.
- Unlimited slot stack limits (Integer.MAX_VALUE) for Sigils of Reforging and Rarity Materials across Simple, Normal, Ancient, and Esoteric Reforging Tables.
- Safe NBT persistence and network VarInt packet synchronization for massive item stacks (> 64, > 127).
- Automatic dirty state persistence (`tile.setChanged()`) on container menu close and item pickup.
- Complete bypass of Apothic-Infinite's default reforge duplicate limit.

### [1.0.0-beta.5] - 2026-10-31
#### Fixed
- Fixed item loss on world save/reload (relog): overhaul `ItemStackHandlerMixin` to ensure positive byte counts in NBT so vanilla deserialization never discards extended stacks as empty/invalid.
- Ensured block entity dirty state persistence: added `tile.setChanged()` hooks on menu close (`removed`) and after material consumption (`onTake`) across all reforging table variants to ensure internal slot inventories are reliably committed to disk.

### [1.0.0-beta.4] - 2026-10-31
#### Fixed
- Fixed actual material and sigil consumption mismatch on reforging: costs are now accurately calculated and deducted before clearing input slot 0 in ReforgingResultSlotMixin.onTake.
- Added input item snapshot caching (lastInput) in ReforgingMenuMixin, AncientReforgingMenuMixin, and EsotericReforgingMenuMixin so dynamic cost scaling remains active and returns the exact multiplied cost even immediately after slot 0 is cleared during item extraction.

### [1.0.0-beta.3] - 2026-10-31
#### Fixed
- Fixed game launch crash caused by `InvalidInjectionException` on `SlotItemHandlerMixin.apoExp$onGetMaxStackSize`: enabled default Mojang/SRG remapping so `getMaxStackSize` properly targets runtime obfuscated methods (`m_6641_` and `m_5866_`) on Forge production environments.

### [1.0.0-beta.2] - 2026-10-31
#### Changed
- Removed automatic player inventory consumption to completely prevent unintended material and sigil drain.
- Unlocked slot stack limit to Integer.MAX_VALUE for Sigils of Reforging (slot 2) and Rarity Materials (slot 1) across all 4 reforging tables (Simple Reforging Table, Reforging Table, Ancient Reforging Table, and Esoteric Reforging Table).
- Added FriendlyByteBufMixin to support VarInt item count encoding/decoding over network packets, seamlessly syncing counts beyond 64 and 127 between client and server.
- Added ItemStackHandlerMixin to serialize counts > 127 into ExtendedCount NBT tags, ensuring zero stack loss across game restarts.
- Enabled infinite stack support during Shift-click quick move (moveItemStackTo) via AbstractContainerMenuMixin.

### [1.0.0-beta.1] - 2026-10-31
#### Added
- First Beta Release: verified across Common, Mythic, and Esoteric tiers in both Creative and Survival modes.
- Added explicit documentation in `README.md` detailing the dynamic 1-Sigil base requirement for 2nd+ reforges of Common affixes.

### [1.0.0-alpha.10] - 2026-10-31
#### Fixed
- Fixed reforge count advancing across Creative mode and repeated reforges: added `AffixMergerMixin` to directly hook into Apothic-Infinite's affix merging lifecycle (`mergeAffixes`), baking the next reforge count directly into the result item at generation time.
- Set `ReforgingMenuMixin` priority to 10000 and explicitly call `choicesInv.setStackInSlot` to ensure candidate item NBT is fully preserved and synced across client/server.
- Fixed Common reforge cost scaling: when re-reforging Common affixes (count >= 2), the base Sigil of Reforging requirement is dynamically established (1 Sigil base), enabling proper exponential progression ($1 \times 2^{\text{count}-2}$) and doubling material cost.
- Added affix-count based fallback estimation for items missing reforge tags.

### [1.0.0-alpha.9] - 2026-10-31
#### Added
- Added comprehensive diagnosis logging (`[APO-EXP]`) across `slotsChanged`, `getMatCost`, `recordReforgeSuccess`, `onTake`, and `getReforgeCountForRarity` to trace exact NBT tags, rarity ID matches, and inventory sync cycles for Common reforging and 3rd+ reforge progression.

### [1.0.0-alpha.8] - 2026-10-31
#### Fixed
- Fixed 2nd reforge failing to double and reforge count failing to advance: overhauled rarity detection to read directly from `affix_data.rarity` NBT tag, completely bypassing unreliable DynamicHolder runtime binding states on the client and server.
- Refactored reforge count storage to record per-rarity count keys directly under `affix_data` (`apo_exp_count_<rarity>`) and `apo_exp_current_count`, ensuring count data survives slot transfers, NBT serialization, and Creative inventory pickups.
- Safeguarded `ReforgingResultSlotMixin.onTake` to strictly preserve pre-calculated counts generated in `choicesInv` without resetting back to 0 or 1 when input slot 0 is cleared.
- Strengthened tooltip rarity resolution via direct NBT inspection.

### [1.0.0-alpha.7] - 2026-10-31
#### Fixed
- Fixed Common rarity tier failing to double on the 2nd reforge: completely replaced brittle string-based rarity ID comparisons with direct `ordinal()` and `getMaterial()` matching, resolving the `"common"` vs `"apotheosis:common"` identifier discrepancy.
- Fixed 3rd and subsequent reforges not scaling exponentially (stuck at $2\times$): resolved registry key lookup mismatches by persisting `apo_exp_count` and `apo_exp_ordinal` directly inside Apotheosis native `affix_data` and custom tags, ensuring counts accurately scale to $4\times, 8\times, 16\times, \dots$.
- Hardened zero-cost handling: if base cost is 0 (such as Sigils of Rebirth for Common affixes), it correctly remains 0 while material costs scale exponentially.
- Safeguarded `onTake` item pickup logic to prevent reforge counts from being overwritten when input slot 0 is already cleared.

### [1.0.0-alpha.6] - 2026-10-31
#### Fixed
- Fixed cost multiplier not scaling on the UI when reforging an item that already possesses the target rarity. The reforge count now naturally defaults to at least 1 (triggering $2^{2-1} = 2\times$ cost on the 2nd reforge of the same rarity tier).
- Fixed NBT persistence and synchronization: candidate items in `choicesInv` now have reforge counts embedded ahead of time during `slotsChanged`, ensuring reliable tag preservation across Creative and Survival actions.
- Client-side menu input slot resolution now prioritizes `getSlot(0)` to ensure real-time cost updates on GUI tooltips.

### [1.0.0-alpha.5] - 2026-10-31
#### Fixed
- Fixed an `InjectionError` on Apothic-Infinite's `AffixInstanceMixin` by removing constant modification collision on `withNewLevel`.
- The maximum duplicate reforge limit is now cleanly unlocked via dynamic synchronization of Apothic-Infinite's `InfiniteConfig.MAX_AFFIX_LEVEL` across config load, reload, and server startup phases.

### [1.0.0-alpha.4] - 2026-10-31
#### Fixed
- Fixed an injection target failure on `mayPickup` in `ReforgingResultSlotMixin` in obfuscated production environments by enabling Mojang/SRG remapping (`remap = true`).

### [1.0.0-alpha.3] - 2026-10-31
#### Fixed
- Fixed `ReEntrantTransformerError` caused by `Class.forName` inside `ExponentialTweakMixinPlugin` during Mixin preparation phase. Replaced with non-classloading resource and `LoadingModList` checks.

### [1.0.0-alpha.2] - 2026-10-31
#### Fixed
- Corrected mixin configuration refmap references and package layout.

### [1.0.0-alpha.1] - 2026-10-31
#### Added
- Initial alpha implementation.
- Overrides Apothic-Infinite `max_affix_level` to `1.0E300`.
- Exponential reforge cost multiplier ($2^{\text{count}-1}$).
- Smart inventory consumption for costs $> 64$.
- Dynamic item tooltips showing previous reforge count and next cost multiplier.

---

<a name="japanese"></a>
## 日本語

### [1.2.4] - 2026-11-08
#### 修正
- エソテリック等の高ソケット確定ティア（5ソケット）において、別ティアを挟まず同一ティアで連続再鍛造した際にソケットが増加しなかった不具合を修正。リフォージメニュー内の誤判定フェイルセーフが、正当な加算結果（$S_{\text{roll}} \ge S_{\text{input}}$）を掛け算倍増バグと誤認してロール増分を消去していた挙動を完全撤廃。
- `LootControllerMixin` の先頭（HEAD）で再鍛造候補アイテムのソケット数を事前に 0 リセットする処理を追加。これによりソケットがロールされなかった場合でも旧ソケット数を引き継がず、純粋な足し算（$S_{\text{input}} + S_{\text{roll}}$）が全状況で成立。

### [1.2.3] - 2026-11-08
#### 修正
- エソテリックおよびエンシェントリフォージテーブルにおいて、難読化環境下でメソッドシグネチャ（`m_6199_`）へのインジェクションがスキップされ、ソケット倍増（16 $\to$ 32 $\to$ 50）が再発していた不具合を修正。
- 再鍛造生成中に `SocketHelper.getSockets(stack)` をインターセプトする `LootControllerMixin` を導入。Apotheosis が `Math.max(socketsRolled, inputSockets)` で今回ロールされた純粋なソケット数（$S_{\text{roll}}$）を元アイテムのソケット数で上書き・消失させていた挙動を解消。これにより、Apothic-Infinite のスロット加算が全テーブル（通常、Ancient、Esoteric）で完全に「純粋な足し算」（$S_{\text{input}} + S_{\text{roll}}$）として機能するよう抜本的修正。
- 全リフォージメニューにフェイルセーフガードを追加し、予期せぬ状況下でもソケットが倍増（掛け算）しない安全機構を配備。
#### リファクタリング
- ガイドライン（パッケージ名にアカウント名等の使用禁止）を厳格に遵守するため、パッケージ構成を中立的な `dev.tweak.apothic_infinite_exponential_tweak` へ全面刷新。
- 全ソースコードおよびリソースの UTF-8（BOMなし）整合性を検証・徹底。

### [1.2.2] - 2026-11-08
#### 修正
- Apotheosis がスロット0の `input.copy()`（既存ソケット数を保持した状態）をベースに再鍛造候補をロールするため、Apothic-Infinite の素朴な加算によって $2 \times S_{\text{input}} + S_{\text{roll}}$ と2倍（掛け算、16 $\to$ 32 $\to$ 50）に跳ね上がっていた不具合を修正。純粋な足し算（$S_{\text{input}} + S_{\text{roll}}$）へ是正し、再鍛造でロールされた分だけ着実に増えるよう改善。

### [1.2.1] - 2026-11-08
#### 修正
- Apothic-Infinite がスロット更新ごとに `inputSockets + choiceSockets` を無差別に足し算していたことによるジェムスロット（ソケット）の指数関数的増殖不具合を根本修正。ソケット数を `Math.max(inputSockets, rollSockets)` に是正し、スロット肥大化を完全撤廃。
- アフィックスマージロジックを刷新し、同一 tier 内での正当な重複強化（同じ Attribute の再抽選によるレベル合算）を許容しつつ、異なる tier 間での無差別合算による数値跳ね上がり（9 $\to$ 23 等）を防止。
- Apotheosis 本体の著作権（All Rights Reserved）を厳格に遵守するため、`AffixHelperMixin` 内にあったベースMODのコード複製を完全撤廃し、戻り値インターセプトによる 100% クリーンルーム実装へ刷新。

### [1.2.0] - 2026-11-08
#### 追加・新機能
- アフィックスごとのレアリティ保持タグ（`affix_rarities`）の導入により、異なる tier（Apotheotic Additions の Esoteric、Heirloom、Artifact 等を含む）のアフィックスを施しても効果が消滅せず、付与時の計算テーブルを維持して正常に適用されるよう対応。
- 同一 tier における2回目以降の再鍛造時、新規抽選されたアフィックスがツールチップ上部に新規項目として追加され、既存の Attribute / ポーション等のバフ数値は勝手に加算・肥大化しないよう制御。
- 耐久力免除（`DurableAffix`）および防具の特定属性ダメージ軽減（`DamageReductionAffix`）のみを統合（マージ・加算）対象として限定。
- 特定属性ダメージ軽減（`DamageReductionAffix`）の最大割合を 99% (0.99F) に規定（被ダメージ計算、ツールチップ説明、補強画面テキストすべてで上限キャップを適用）。
#### 修正
- エソテリックアフィックス付与後にミシックを付与した際、エソテリックのリーチがミシックの計算テーブルや無差別加算で異常に強化される（例: 9 $\to$ 23）不具合を修正。
- アフィックス2回目で新しい Attribute バフが発生しているにもかかわらず既存バフの項目まで伸びてしまう不具合を修正。
- `AttributeAffix` のモディファイア UUID をアフィックスID固有の決定論的 UUID とすることで、同じ属性を持つ異なるアフィックス同士が競合・上書きされずに両方適用されるよう修正。

### [1.1.1] - 2026-10-31
#### 修正
- Apothic-Infinite の再鍛造（アフィックス合成）によってアフィックスレベルが 1.0F 以上になっていたアイテムを補強テーブルに入れた際、未補強にもかかわらず補強回数が誤推定されて高額なコストが要求される不具合を修正。
- 補強テーブルでの指数関数コスト倍化の判定を、アフィックスの初期レベルからの推測を廃止し、「補強テーブルで Apotheosis のデフォルト最大値（1.0F）を超えて実際に強化した回数」にのみ基づいて $2 \times 2^{\text{突破回数}+1}$（1回目: 4個、2回目: 8個、3回目: 16個...）とスケールするよう根本修正。
- アイテムツールチップの上限突破回数表示も、補強テーブルでの実際の突破実績回数のみを正確にカウント・表示するように修正。

### [1.1.0] - 2026-10-31
#### 追加・新機能
- 補強テーブル（Augmenting Table）におけるデフォルト上限値（1.0F）を超えた値の強化に対する指数関数的コスト倍化を追加: 1回目の上限突破（1.00F $\to$ 1.25F）で補強の紋章4個（$2 \times 2^1$）、2回目で8個（$2 \times 2^2$）、3回目で16個（$2 \times 2^3$）と強化ごとに倍増。1.0F以下の通常強化はバニラ通り（2個）を維持。
- 補強テーブルの「補強の紋章」スロットのスタック上限を Integer.MAX_VALUE（無制限）に開放。
- 強化時およびメニュー終了時にブロックエンティティの保存フラグ（`tile.setChanged()`）とパケット同期を確実に呼び出し、リログ時の紋章消滅を完全防止。
- 補強テーブル画面のアップグレードボタンおよびツールチップを動的コストと完全連動させ、必要コスト・ボタン有効化状態・実際の消費量を100%一致。
- アイテムツールチップに補強上限突破回数の表示を追加。

### [1.0.0] - 2026-10-31
#### 追加・正式リリース
- 正式リリース版 1.0.0: Minecraft 1.20.1 Forge 環境における全機能の安定動作を検証・確立。
- 同一レアリティの連続再鍛造における指数関数的なコスト増加 ($2^{\text{回数}-1}$ 倍)。
- コモンアフィックス再鍛造（2回目以降）における基準紋章コスト1個の設定による、全レアリティ均一な倍化進行。
- 全4種（簡易・通常・古代・秘術）の再鍛造台における素材・紋章スロットのスタック上限無制限化（Integer.MAX_VALUE）およびインベントリ誤消費の完全撤廃。
- 64個および127個を超える巨大スタックに対応したネットワーク通信（VarInt化）およびNBTデータ安全保存（ExtendedCount）・リログ復元対応。
- メニュー終了時・素材消費時のブロックエンティティ保存フラグ（setChanged）保証によるワールド保存の安定化。
- Apothic-Infinite の重複鍛造上限（max_affix_level）の自動最大値開放。

### [1.0.0-beta.5] - 2026-10-31
#### 修正
- リログ（ワールド保存・再読み込み）時に再鍛造台内部スロットの素材が消滅する不具合を根本修正: `ItemStackHandlerMixin` を刷新し、64個超のスタック保存時にバニラ側の判定で空アイテムとして破棄されないようNBT整合性を保証。
- ブロックエンティティの保存フラグ（setChanged）の確実な呼び出し: メニュー終了時（`removed`）および素材消費時（`onTake`）に確実にタイルエンティティの `setChanged()` を呼び出し、内部スロットの変更をワールドディスクへ確実に保存。

### [1.0.0-beta.4] - 2026-10-31
#### 修正
- 再鍛造時に見かけ上の必要素材数と実際の消費量が一致しない不具合の修正: `ReforgingResultSlotMixin.onTake` において入力スロット0を空にする前に消費コストを確定・減算するよう修正。
- `ReforgingMenuMixin`、`AncientReforgingMenuMixin`、`EsotericReforgingMenuMixin` に対し、入力アイテムのスナップショット保持（`lastInput`）を追加。アイテム取り出し時にスロット0が空になった直後でも正確な倍化コストを維持・返却するよう強化。

### [1.0.0-beta.3] - 2026-10-31
#### 修正
- `SlotItemHandlerMixin` の `getMaxStackSize` メソッドインジェクションが Forge 製品環境（難読化/SRG）で `m_6641_` / `m_5866_` にマッチせず、`InvalidInjectionException` でゲーム起動時にクラッシュする不具合を修正（Mixin refmap へのリマップを有効化）。

### [1.0.0-beta.2] - 2026-10-31
#### 変更・改善
- インベントリからの意図しないアフィックス素材や紋章の誤消費を根絶するため、インベントリ自動消費機能を完全廃止。
- 全4種の再鍛造テーブル（簡易再鍛造台、再鍛造台、古代の再鍛造台、秘術の再鍛造台）において、「再鍛造の紋章」スロットおよび「レアリティマテリアル」スロットのスタック上限を Integer.MAX_VALUE（無制限）に開放。スロット内のアイテムからのみ厳密に消費する設計へ変更。
- ネットワークパケット（FriendlyByteBuf）のアイテム個数送受信を VarInt 化し、64個および127個を超える巨大なスタック数がクライアント・サーバー間で正確にリアルタイム同期されるよう対応。
- ItemStackHandler のNBT保存処理に ExtendedCount タグサポートを追加し、ワールド保存・再起動時にも127個以上のアイテムスタックが消失しないよう完全保護。
- AbstractContainerMenu.moveItemStackTo をフックし、Shiftクリック（クイック移動）時にもスロット上限までスムーズにアイテムを投入できるよう改善。

### [1.0.0-beta.1] - 2026-10-31
#### 追加
- 初回Betaリリース: コモン、ミシック、エソテリック各階層において、クリエイティブおよびサバイバル両環境での正常動作を確認。
- `README.md` にコモンアフィックス再鍛造時の基本紋章コスト（2回目以降1個要求）の仕様を明記。

### [1.0.0-alpha.10] - 2026-10-31
#### 修正
- クリエイティブモード等で鍛造回数が進まない不具合の修正: Apothic-Infiniteの `AffixMerger.mergeAffixes` に直接Mixinフック (`AffixMergerMixin`) を適用し、候補生成時に次回鍛造回数をNBTへ事前埋め込み。
- `ReforgingMenuMixin` の優先度を 10000 に設定し、`choicesInv.setStackInSlot` を明示的に呼ぶことでNBT喪失を防止。
- コモンアフィックス再鍛造コストの倍化対応: 2回目以降のコモン再鍛造時、基本紋章コストを1個として扱い、2回目=1個、3回目=2個、4回目=4個と倍増するよう対応。素材消費も正常に倍化。
- タグを持たない既存アイテム向けにアフィックス数ベースのフォールバック推定を追加。

### [1.0.0-alpha.9] - 2026-10-31
#### 追加
- 各種判定・スロット変更・素材計算処理に包括的な診断ログ (`[APO-EXP]`) を追加。

### [1.0.0-alpha.8] - 2026-10-31
#### 修正
- 2回目の倍化および回数加算が失敗する問題の修正: レアリティ判定を `affix_data.rarity` の直接参照へ全面的に刷新。
- レアリティ別カウントタグ (`apo_exp_count_<rarity>`) を導入し、スロット移動やNBTシリアライズ後も確実に回数を保持。
- `onTake` 時に生成済みカウントが初期化されないようガードを強化。

### [1.0.0-alpha.7] - 2026-10-31
#### 修正
- コモンレアリティでの倍化失敗修正: 文字列比較から `ordinal()` および `getMaterial()` 判定へ移行。
- 3回目以降の倍化が $2\times$ で止まる問題の修正: カウントタグの永続化キーを改善し、$4\times, 8\times, 16\times$ と指数スケールするよう修正。

### [1.0.0-alpha.6] - 2026-10-31
#### 修正
- 既にレアリティを持つアイテムの再鍛造時、UI上の必要コストが倍化表示されない不具合の修正。
- 候補スロット生成時のNBT同期を強化。

### [1.0.0-alpha.5] - 2026-10-31
#### 修正
- `AffixInstanceMixin` のインジェクション競合エラーを解消。
- コンフィグ読み込みおよびサーバー起動フェーズでの `MAX_AFFIX_LEVEL` 動的同期による上限開放方式へ移行。

### [1.0.0-alpha.4] - 2026-10-31
#### 修正
- 難読化製品環境における `ReforgingResultSlotMixin.mayPickup` のターゲットエラーを解決 (`remap = true`)。

### [1.0.0-alpha.3] - 2026-10-31
#### 修正
- Mixin準備フェーズでのクラス早期ロードによる `ReEntrantTransformerError` を解消。

### [1.0.0-alpha.2] - 2026-10-31
#### 修正
- Mixin設定ファイルの refmap パスおよびパッケージ構成を修正。

### [1.0.0-alpha.1] - 2026-10-31
#### 追加
- 初回アルファ実装。
- Apothic-Infiniteの `max_affix_level` を `1.0E300` にオーバーライド。
- 同一レアリティ再鍛造時の指数コストスケール ($2^{\text{回数}-1}$)。
- 64個超過時のインベントリ自動消費機能。
- ツールチップへの鍛造回数および次回倍率表示機能。
