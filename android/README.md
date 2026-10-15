# GameDeals for Android

Native Android client for GameDeals Finder (Kotlin K2, Jetpack Compose, Material 3 themed with Cedar). It brings PC game deals from 30+ digital storefronts (Steam, GOG, Epic Games, Fanatical, Humble Store, etc.) to Cody's Galaxy Z Fold 8 Ultra and Galaxy S26+ with 100% offline-first reliability, custom favorite grouping in Room, adaptive multi-column foldable layouts, and native share sheets. See [`NATIVE_EVALUATION.md`](./NATIVE_EVALUATION.md).

## Architecture

| Layer | Contents |
| :--- | :--- |
| `domain/` | Pure Kotlin models: `Store.kt`, `Deal.kt`, `DealDetails.kt`, `GameDetails.kt`, `SearchResult.kt`, `FavoriteGroup.kt`, `FavoriteItem.kt` |
| `data/api/` | Retrofit 3 + OkHttp 5 client with kotlinx.serialization for CheapShark API (`https://www.cheapshark.com/api/1.0/`) |
| `data/db/` | Room database (`gamedeals.db`): stores, popular deals, and custom favorite groups & items |
| `data/repository/` | `GameDealsRepository` (offline fallback to bundled sample data and local Room cache), `SettingsRepository` (DataStore theme mode) |
| `ui/` | Cedar Compose UI: `DealsScreen`, `SearchScreen`, `FavoritesScreen`, `DealDetailSheet`, `GameDetailSheet`, `FavoriteModal` |
| `ui/theme/` | Cedar design tokens: `CedarColors`, `CedarTypography` (bundled Source Serif 4, Inter, JetBrains Mono), `CedarShapes` |

## Offline & Local Storage Behaviour

- **100% Offline-First**: All storefront listings, active deals, and user favorites are cached in Room SQLite (`gamedeals.db`). If offline or without internet access, the repository seamlessly falls back to bundled JSON assets (`ListOfStores.json`, `ListOfDeals.json`, `Deal.json`, `Game.json`, `SearchResults.json`) so the app opens instantly with full catalog data.
- **Universal Theme Toggle**: Moon/Sun toggle accessible in the top app bar across all screens, persisted via DataStore Preferences.
- **System Share Target**: One-tap native Android sharesheet integration for sharing deal savings and store links.

## Build and Test

```bash
cd android
./gradlew lintDebug testDebugUnitTest      # lint (warnings are errors) + unit tests
./gradlew assembleDebug                    # local debug build
./gradlew assembleRelease -PappVersionName=1.0.0   # release build (signed with homelab key)
```

## Sideloading and Phone Targets

```bash
# Sideload to USB-connected phones (Galaxy Z Fold 8 Ultra, Galaxy S26+):
python3 ~/dev/repos/skills/skills/create-android-app/scripts/install_apk.py gamedeals 1.0.0
# Or install a local debug build:
python3 ~/dev/repos/skills/skills/create-android-app/scripts/install_apk.py --apk app/build/outputs/apk/debug/app-debug.apk
```

| Device | Screen | Window | Layout |
| :--- | :--- | :--- | :--- |
| Galaxy Z Fold 8 Ultra, folded (cover) | 1080×2520 px @ 480 dpi | 360×840 dp, compact | Bottom navigation bar, single-column deal cards |
| Galaxy Z Fold 8 Ultra, unfolded | 2256×2504 px @ 480 dpi | 752×834 dp, medium | Slim icon rail (~72dp), ≥85% width for 2-column fluid deal grid |
| Galaxy S26+ | ~410–430 dp wide | compact | Same as cover screen |

## Licences

- Bundled OFL fonts: Source Serif 4, Inter, JetBrains Mono.
- Icons: Lucide (ISC).
