# Project Parent (formlally Project Baby)

Project Parent is a modern, multiplatform baby care tracker app built with Kotlin Multiplatform and Jetpack Compose. It helps parents easily log and visualize their baby's feeding, sleep, and care routines, with a focus on bottle feed tracking, real-time notifications, and insightful analytics.

## Key Features
- **Bottle Feed Tracking:** Start, pause, and finish bottle feeds with real-time timers and persistent notifications.
- **Notifications:** Get reminders for the next bottle feed, with exact alarm support and permission handling on Android.
- **Profile Management:** Store and update your baby's profile, including name, birth date, weight, and profile image.
- **Onboarding:** Guided onboarding flow to set up your baby's profile and select which home cards to track.
- **Customizable Home View:** Toggle which cards (feeds, sleep, food, meds, etc.) are visible on the home screen.
- **Charts & Analytics:** Visualize bottle feed data with interactive charts (hourly, daily, and total feeds) using MPAndroidChart.
- **Persistent Settings:** All preferences and profile data are saved using multiplatform settings.
- **Multiplatform:** Shared business logic and UI for Android and iOS, with platform-specific enhancements.

## Project Structure
- `/composeApp` — Shared Kotlin Multiplatform code (UI, logic, models)
  - `commonMain` — Code shared across all platforms
  - `androidMain` — Android-specific implementations (notifications, services, UI interop)
  - `iosMain` — iOS-specific implementations
- `/iosApp` — iOS entry point and SwiftUI code
- `/iOS_V1` — Legacy/previous iOS implementation (reference only)

## Getting Started
1. **Clone the repository:**
   ```bash
   git clone <repo-url>
   ```
2. **Open in Android Studio or IntelliJ IDEA** (for Android and shared code)
3. **Build and run** on your preferred platform (Android or iOS)

## Contributing
- Contributions are welcome! Please open issues or pull requests for improvements, bug fixes, or new features.

## License
This project is licensed under the MIT License.


