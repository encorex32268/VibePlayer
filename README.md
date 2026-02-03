# VibePlayer

VibePlayer is a modern, feature-rich local music player for Android, built with the latest technologies including Jetpack Compose, Media3 ExoPlayer, and Koin.

>[!IMPORTANT]
>This project was developed for the **Mobile Dev Campus**, focusing on industry-standard Clean Architecture and modern Android development practices.

## ✨ Features

- **Scan Local Audio**: Automatically finds and indexes audio files on your device.
- **Smart Playlists**: Create, manage, and play your favorite tracks.
- **Smooth Playback**: Powered by Media3 ExoPlayer for a reliable and high-quality listening experience.
- **Modern UI**: A beautiful, responsive interface designed with Material 3.
- **Search**: Quickly find the songs you want to hear.

## 🛠 Tech Stack

The project leverages a modern Android tech stack:

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Dependency Injection**: [Koin](https://insert-koin.io/)
- **Database**: [Room](https://developer.android.com/training/data-storage/room)
- **Media Playback**: [Media3 ExoPlayer](https://developer.android.com/media/media3/exoplayer)
- **Image Loading**: [Coil 3](https://coil-kt.github.io/coil/)
- **Navigation**: [Navigation Compose](https://developer.android.com/guide/navigation/navigation-compose)
- **Permissions**: [Accompanist Permissions](https://google.github.io/accompanist/permissions/)
- **Architecture**: Clean Architecture (Presentation, Domain, Data) with MVI pattern.

## 🏗 Architecture

This app follows the **Clean Architecture** principles to ensure separation of concerns and testability:

- **Presentation Layer**: Contains UI components (Compose), ViewModels, and UI State/Events.
- **Domain Layer**: Repository interfaces. Pure Kotlin, no Android dependencies.
- **Data Layer**: Contains Repository implementations, Data Sources (Room, File System), and Mappers.

## 🚀 Setup & Installation

**Clone the repository**:
   ```bash
   git clone https://github.com/your-username/VibePlayer.git
   ```

## 📸 Screenshots

*(Screenshots coming soon)*

## 📜 License

This project is open source.

**Author**: [LiHan](https://github.com/encorex32268)
**Project Link**: [VibePlayer](https://github.com/encorex32268/VibePlayer) 

## 🚀 Future Roadmap
- **Background Playback**: Support seamless audio streaming and media controls via `MediaSessionService` when the app is in the background.
- **Dark/Light & Dynamic Theme**: Support for Android 12+ **Material You** dynamic coloring and seamless switching between dark and light modes.
- **Android Auto Support**: Extend the music experience to car dashboards for a safer driving experience.
- **Unit & UI Testing**: Increase code coverage with **Room database tests** and **Compose UI tests** to ensure app stability and reliability.