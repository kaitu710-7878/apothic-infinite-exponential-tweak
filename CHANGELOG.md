# Changelog / 変更履歴

[English](#english) | [日本語](#japanese)

---

<a name="english"></a>
## English

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
