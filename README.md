# VibePlayer

VibePlayer is a modern, feature-rich local music player for Android, built with the latest technologies including Jetpack Compose, Media3 ExoPlayer, and Koin.

## Features

- **Scan Local Audio**: Automatically finds and indexes audio files on your device.
- **Smart Playlists**: Create, manage, and play your favorite tracks.
- **Smooth Playback**: Powered by Media3 ExoPlayer for a reliable and high-quality listening experience.
- **Modern UI**: A beautiful, responsive interface designed with Material 3.
- **Search**: Quickly find the songs you want to hear.
- **Background Playback**: Listen to your music while using other apps.

## Tech Stack

The project leverages a modern Android tech stack:

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Dependency Injection**: [Koin](https://insert-koin.io/)
- **Database**: [Room](https://developer.android.com/training/data-storage/room)
- **Media Playback**: [Media3 ExoPlayer](https://developer.android.com/media/media3/exoplayer)
- **Image Loading**: [Coil 3](https://coil-kt.github.io/coil/)
- **Navigation**: [Navigation Compose](https://developer.android.com/guide/navigation/navigation-compose)
- **Permissions**: [Accompanist Permissions](https://google.github.io/accompanist/permissions/)
- **Architecture**: Clean Architecture (Presentation, Domain, Data) with MVVM pattern.

## Architecture

This app follows the **Clean Architecture** principles to ensure separation of concerns and testability:

- **Presentation Layer**: Contains UI components (Compose), ViewModels, and UI State/Events.
- **Domain Layer**: Contains Use Cases and Repository interfaces. Pure Kotlin, no Android dependencies.
- **Data Layer**: Contains Repository implementations, Data Sources (Room, File System), and Mappers.

## Setup & Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/VibePlayer.git
   ```
2. **Open in Android Studio**:
   - Launch Android Studio.
   - Select "Open" and navigate to the cloned directory.
3. **Build the project**:
   - Wait for Gradle to sync.
   - Run the app on an emulator or physical device.

**Requirements**:
- Android Studio Ladybug or newer (recommended).
- JDK 21 (as defined in `build.gradle.kts`).
- Min SDK: 24 (Android 7.0).
- Target SDK: 36.

## Screenshots

*(Screenshots coming soon)*

## License

This project is open source.
