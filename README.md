# Project-Parent
This project is a simple app to manage your baby care routine. It allows you to track feeding, diaper changes, and sleep patterns. Project folders are included for iOS(Existing) and Android(New)

This is a Kotlin Multiplatform project targeting Android, iOS.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…

## Features
- Log feedings (breastfeeding or bottle)
- Track diaper changes
- Monitor sleep patterns
- View history of activities

## Installation
1. Clone the repository:
   ```bash
   git clone


