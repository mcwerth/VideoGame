# Vault of Luck

Vault of Luck is a Kotlin/Jetpack Compose idle-incremental roguelite prototype designed as a policy-compliant Google Play title. It features deterministic math for balancing, transparent gacha odds, and persistent offline progression.

## Game loop
1. Earn Coins through generators in real time or via offline income.
2. Spend Tokens to spin the luck wheel or open loot packs with pity protection.
3. Unlock and upgrade generators, global boosts, and prestige meta perks.
4. Run limited-time roguelite expeditions to earn Gems and Tokens.
5. Prestige to convert late-game Coins into Essence, resetting local progress while boosting future runs.

## Economy configuration
All key tuning values live in [`Economy.kt`](app/src/main/java/com/example/vaultofluck/core/economy/Economy.kt). Highlights:
- Generator costs follow `base * growth^level` with `growth = 1.18`.
- Prestige returns `floor(k * coins^0.6)` with `k = 0.0005`.
- Offline income is capped at eight hours and scales with AFK upgrades.
- Gacha weights and pity thresholds are defined via `rarityWeights`, `pitySoftStart`, and `pityHardCap`.

Adjusting these constants propagates through the repositories, use-cases, and UI automatically.

## Odds disclosure
The gacha system exposes rarity weights and pity behavior in-app on the Odds screen. Pull sizes (x1/x10/x50) tweak expected value and visual presentation. The pity system ramps Epic+ odds after 20 pulls and guarantees an Epic by 60 pulls. All gambling mechanics consume only soft Tokens.

## Technology
- **Architecture:** MVVM + Clean Architecture with use-cases and a lightweight service locator.
- **UI:** Jetpack Compose Material 3 with navigation, animations, and haptic feedback stubs.
- **Persistence:** Room for durable entities (`Player`, `Currency`, `Generator`, `Upgrade`, `GachaHistory`, `Run`, `Quest`). DataStore covers player preferences.
- **WorkManager:** Applies offline idle gains periodically via `OfflineIncomeWorker`.
- **RNG:** Seedable RNG with weighted sampling and pity helpers.

## Policy & safety
- No real-money purchases; gambling loops consume virtual Tokens only.
- Odds are disclosed in app and via README.
- Analytics and ad hooks are stubbed and do not collect personal data.

## Tuning odds and growth
1. Edit `Economy.rarityWeights` to adjust rarity distribution.
2. Modify `Economy.pitySoftStart`, `pityHardCap`, or `pityRamp` to re-balance protection.
3. Adjust `Economy.GENERATOR_GROWTH` or `Economy.GENERATOR_BASE_COST` to reshape pacing.
4. Prestige progression is driven by `PRESTIGE_K` and `PRESTIGE_EXPONENT`.

## Roadmap v1.1
- Time-limited live events with custom loot tables and AFK boosts.
- Cosmetic-only monetization bundles and VIP booster toggles.
- Cloud save & local slots with sync conflict resolution.
- Leaderboards backed by stubbed service for friendly competition.
- Compose particle effects for legendary drops.

## Running & testing
```bash
./gradlew test
```

The test suite covers RNG weighting, pity adjustments, economy curves, prestige calculations, idle income, and viewmodel snapshot behavior.
