# Warranty Vault — Android Mini Project

**Java + XML + SQLite, offline, minimum Android 6.0 (API 23).**

## Open and run
1. Install Android Studio with Android SDK 35 and JDK 17.
2. Open the `WarrantyManager` folder as a project (not the `app` folder).
3. Let Gradle sync and install any SDK components it requests. This ZIP does not include a Gradle wrapper; Android Studio can use its configured Gradle distribution, or generate a wrapper with Gradle 8.9.
4. Run on an emulator or Android phone. On Android 13+, grant notification permission when prompted.
5. Tap **Add Product**, fill in name, purchase date, price and warranty months, optionally attach a receipt using the system document picker, then Save.
6. Tap a product to view, edit or delete it. Use the **Expiring soon** filter for warranties expiring in 30 days.

## Source files
- `MainActivity`: dashboard, search and expiring-soon filter.
- `ProductFormActivity`: add/edit form and persistent receipt URI selection.
- `ProductDetailActivity`: product details, receipt preview and delete confirmation.
- `DatabaseHelper`: SQLite product CRUD and notification deduplication.
- `Dates`: warranty date calculation and status.
- `ReminderWorker`: daily background checks and 30-, 7-, and 0-day expiry reminders.

## Important limitations
- Reminder checks use WorkManager's periodic background scheduling. Android may delay jobs; reminders are **not guaranteed at an exact time**. A newly added product with fewer than 7 days remaining may only trigger on its expiry day.
- Receipt attachments use Android's document picker, which grants a persistent read URI; keep the selected file available. If a file is deleted or the provider revokes access, the preview may no longer work.
- There is no cloud backup or login. Clearing app data or uninstalling removes the database.
- Product warranty is calculated by adding calendar months to the purchase date; actual vendor warranty terms may differ.
- All prices are displayed in INR (₹), but no payments are processed.

## Demonstration test cases
1. Add a product purchased 2026-01-31 with a one-month warranty: expiry should be 2026-02-28.
2. Add a product, edit its brand, verify changes persist after reopening.
3. Attach a receipt, reopen product details, and verify the image is visible.
4. Search by product name or category; toggle expiring-soon filter.
5. Delete a product and verify it disappears from the dashboard.
6. On Android 13+, verify notification permission is requested. WorkManager timing cannot be guaranteed for an immediate demo.

## Possible extensions
Export receipts to PDF, encrypted storage, category charts, multiple receipt attachments and cloud synchronization.
"# Warrenty_Manager" 
