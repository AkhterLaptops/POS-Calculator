# Implementation Plan - POS Calculator v2.2 & Persistent Dropdown Preset Customization

This plan addresses upgrading the app version to **v2.2** and fixing the dropdown values persistence so that the 3 default values are seeded only on first install, and all subsequent user edits, additions, and deletions in the Settings menu are permanently remembered across app launches.

---

## 1. Problem Analysis & Root Cause

Currently, in `PosRepository.kt`, `initializeDefaultsIfNeeded()` checks:
```kotlin
val isDifferentSet = existing.isEmpty() || existing.size != DEFAULT_POS_CATEGORIES.size || existingIds != defaultIds
if (hasOldData || isDifferentSet) {
    // Deletes all existing categories and re-inserts DEFAULT_POS_CATEGORIES
}
```
Because `initializeDefaultsIfNeeded()` is invoked in `PosCalculatorViewModel.init`, whenever a user:
- Adds a new category (size becomes 4 instead of 3),
- Deletes a category (size becomes 2 instead of 3),
- Modifies names or IDs, or
- Restarts / re-opens the screen or ViewModel,
`isDifferentSet` evaluates to `true`. This triggers a complete wipe of the Room database table and restores the hardcoded default presets, effectively discarding all user modifications.

---

## 2. Proposed Solution

### A. Version Bump to v2.2
- **`app/build.gradle.kts`**:
  - Update `versionName` to `"v2.2"`.
  - Increment `versionCode` to `3`.
- **`metadata.json`**:
  - Update description to reflect `v2.2`.
- **`PosCalculatorScreen.kt`**:
  - Update TopAppBar version badge chip from `v2.1` to `v2.2`.
- **`HistoryAndSettingsDialogs.kt`**:
  - Update the Settings dialog footer to `"POS Calculator v2.2 • Android 9 to Latest (API 28 - 36)"`.

### B. First-Install Initialization & Preset Preservation
- **`PosRepository.kt`**:
  - Introduce a persistent preference flag: `KEY_INITIALIZED_DEFAULTS = "has_initialized_v2_2_presets"` stored in `pos_prefs`.
  - In `initializeDefaultsIfNeeded()`:
    1. Check if `prefs.getBoolean(KEY_INITIALIZED_DEFAULTS, false)` is `true`.
    2. If `true` and `existing.isNotEmpty()`: Do **NOT** modify or reset anything. All user additions, deletions, and rate/name changes in Room are fully preserved.
    3. If `false` (first install or upgrade to v2.2 without initial seed) or if `existing.isEmpty()`:
       - Clear any obsolete legacy keys (from earlier pre-v2.1 drafts).
       - Insert the 3 required defaults:
         1. **POS Charges for Customer** @ **2.50%** (`id = "pos_customer"`)
         2. **POS Charges for Other Shopkeepers** @ **3.00%** (`id = "pos_shopkeepers"`)
         3. **POS Charges on International Cards** @ **4.50%** (`id = "pos_international"`)
       - Mark `KEY_INITIALIZED_DEFAULTS` as `true`.
  - Ensure `resetCategoriesToDefault()` remains available when the user explicitly clicks the "Reset Defaults" button in the Settings dialog.

---

## 3. Files to Modify

1. **`app/build.gradle.kts`**:
   - `versionCode = 3`
   - `versionName = "v2.2"`
2. **`metadata.json`**:
   - Update version string to `v2.2`.
3. **`app/src/main/java/com/example/data/repository/PosRepository.kt`**:
   - Refactor `initializeDefaultsIfNeeded()` to use `has_initialized_v2_2_presets` so defaults are only seeded on first install/launch.
   - Guarantee that user additions (`addNewCategory`), updates (`updateCategory`), and deletions (`deleteCategory`) persist permanently in Room and are never overwritten.
4. **`app/src/main/java/com/example/ui/screens/PosCalculatorScreen.kt`**:
   - Update header badge to `v2.2`.
5. **`app/src/main/java/com/example/ui/components/HistoryAndSettingsDialogs.kt`**:
   - Update footer text in Settings dialog to `v2.2`.
6. **`app/src/test/java/com/example/ExampleRobolectricTest.kt`**:
   - Add unit/integration tests verifying that modified, added, and deleted categories are preserved across repository re-initialization.

---

## 4. Verification & Testing Plan

1. **Unit & Persistence Verification**:
   - Run `gradle :app:testDebugUnitTest` to test:
     - First-time initialization seeds the exact 3 defaults (2.50%, 3.00%, 4.50%).
     - Custom category addition is remembered after calling `initializeDefaultsIfNeeded()` a second time.
     - Custom rate modification is retained and not reset to default.
     - Category deletion is retained and not revived.
     - "Reset Defaults" button correctly restores the 3 baseline categories when explicitly invoked.
2. **Build & Compilation**:
   - Run `compile_applet` to confirm zero compilation errors.
