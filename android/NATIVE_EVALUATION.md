# Native Capability Evaluation: game-deals

- **Date**: 2026-10-14
- **Web app**: https://game-deals.lan.codyuhi.online (frontend-only SPA, CheapShark API client, no internal auth required)
- **Primary users / devices**: Cody (Galaxy Z Fold 8 Ultra), Lena (Galaxy S26+)

## How the app is used today
GameDeals Finder is used to quickly browse digital PC game sales across 30+ digital storefronts (Steam, Epic Games Store, GOG, Fanatical, Humble Store, etc.), search for specific game discounts, compare historical cheapest prices against current sales, and organize games into categorized custom favorite lists. It is used casually on the couch, while traveling, or when deciding whether to buy a game on sale.

## Web limitations that hurt
- **Mobile responsiveness constraints**: On mobile browsers, browsing dense deals tables and comparing prices across stores can feel cramped, particularly with browser chrome eating vertical space.
- **No true offline reliability**: Although the web app caches some JSON in localStorage, opening it without an active connection or with flaky Wi-Fi can feel jarring.
- **Unfolding on Fold 8 Ultra isn't optimal**: The web app stretches or centers columns instead of utilizing the 752dp wide canvas for rich multi-column deal browsing with a dedicated icon rail.
- **Sharing friction**: Sharing a great deal with friends requires manually copying the URL rather than invoking the native Android sharesheet.
- **No tactile feedback**: Missing native haptics when favoriting games or filtering.

## Capability scores
| Capability | Value (0–3) | Cost (S/M/L) | Verdict | Notes |
| :--- | :---: | :---: | :---: | :--- |
| Offline Room cache & bundled fallback | 3 | S | `v1` | Complete offline availability. Stores, popular deals, and favorites persist locally in Room with bundled fallback data. Zero network lockup. |
| Foldable Content-First (Z Fold 8 Ultra) | 3 | M | `v1` | Slim navigation rail (~72dp) on unfolded 752dp screen dedicating ≥85% width to fluid multi-column deal grid (2-3 columns) and wide deal inspection. Compact bottom nav on cover/S26+. |
| Native Sharesheet (`ACTION_SEND`) | 2 | S | `v1` | Share deal information and store link to friends via Android system sharesheet with 1 tap. |
| Haptic Feedback | 2 | S | `v1` | Cedar tactile haptics on favoriting, filter selection, store switches, and group actions. |
| Room Favorites & Group Management | 3 | S | `v1` | Custom group nicknames and game favorites stored in Room with instant local search, add, edit, and deletion. |
| Direct Store Redirection | 3 | S | `v1` | Seamlessly launch store deals via Android Custom Tabs or default browser for purchasing. |
| Home Screen Glance Widget | 2 | M | `later` | Daily top deals widget or price drop alert widget. |
| Background Price Drop Monitoring | 2 | M | `later` | WorkManager periodic task checking favorited games for price drops and notifying the user. |
| Inbound URL Share Target (`ACTION_SEND`) | 2 | M | `later` | Share a Steam or Epic URL from a browser to GameDeals to immediately search for discounts across all stores. |
| BiometricPrompt | 0 | S | `no` | Public game deal data and local favorites do not warrant biometric security. |
| Camera / Barcode Scanning | 0 | M | `no` | PC game digital keys have no physical barcodes. |

## Recommended v1 (beyond parity)
1. **Offline Room cache + Bundled Fallbacks** — Room database caching stores, active deals, and favorites, with zero network blocking so the app launches instantly anywhere.
2. **Foldable Content-First Experience** — Purpose-built adaptive layout utilizing the 752dp unfolded screen with a slim navigation rail and 2–3 column deal cards, plus single-column bottom navigation on the cover screen and Galaxy S26+.
3. **Native Sharesheet Out** — One-tap sharing of deal discounts, savings percentage, and store links to messaging apps.
4. **Subtle Haptics** — Native physical confirmation feedback on favorites and actions.
5. **Direct Store Intent Launch** — Launching store links directly in Chrome Custom Tabs or preferred browser.

## Deferred (to be filed as future tickets)
- **Glance Widget**: Home screen widget showcasing the #1 Deal Rating of the day.
- **Background Price Drop Alerts**: Periodic WorkManager worker checking favorites against CheapShark API.
- **Inbound Store URL Share Target**: Intercepting shared Steam/Epic links to find cheaper storefront alternatives.

## Rejected
- **Biometrics**: Unnecessary overhead for non-sensitive public deal listings.
- **Camera/Barcode**: Digital PC keys have no retail barcode relevance.

## Permissions requested in v1
- `android.permission.INTERNET` (access CheapShark API for live deals and store listings)
- `android.permission.ACCESS_NETWORK_STATE` (monitor online/offline connectivity to show Cedar offline banner and switch to local Room data)
- *Zero dangerous or runtime permissions required.*
