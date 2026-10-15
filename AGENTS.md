# Agent Notes

## Android Client (ticket DEAL-1)

Native Kotlin/Compose client located in `android/` with Cedar design system theme; architecture and setup details in `android/README.md`, decisions in `android/NATIVE_EVALUATION.md`.

- **Build and Test**: `cd android && ./gradlew lintDebug testDebugUnitTest assembleDebug`. Release builds are compiled and signed via the Argo Workflows `android-build-apk` template (`-p repo-name=game-deals -p version=<semver>`) and installed using the create-android-app skill's `install_apk.py game-deals <version>`.
- **Application ID & Namespace**: `online.codyuhi.gamedeals`. Debug and release builds share the identical application ID per Golden Rule 8 (One app, one icon).
- **Offline Reliability**: Frontend-only client querying CheapShark API (`https://www.cheapshark.com/api/1.0/`). Fully caches stores, deals, and custom favorite groups & items locally in Room SQLite (`gamedeals.db`). When offline or with no internet, falls back to bundled sample data assets with zero network lockup.
- **Adaptive Layout**: Implements `WindowSizeClass` adaptive layouts supporting Galaxy Z Fold 8 Ultra (compact 360x840dp cover screen with bottom navigation, and medium 752x834dp unfolded inner screen with slim 72dp icon rail dedicating ≥85% width to fluid multi-column deal grid) and Galaxy S26+.
