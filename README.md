# Background Remover Android App

A simple Android application built with **Jetpack Compose** that allows users to select an image from their gallery and remove its background using a remote API.

## Features
- Select images from the local gallery.
- Upload images to a backend service for background removal.
- Display the processed image (PNG with transparency).
- Built using modern Android components: Jetpack Compose, Ktor Client, and Coil.

## Tech Stack
- **UI:** Jetpack Compose
- **Networking:** Ktor Client (CIO Engine)
- **Image Loading:** Coil
- **Serialization:** Kotlinx Serialization
- **Concurrency:** Kotlin Coroutines

## Getting Started

### Prerequisites
- Android Studio Ladybug (or newer).
- JDK 17 or higher.
- A running backend API for background removal.

### Configuration
1. Open `BackgroundRemoverClient.kt`.
2. Update the `BASE_URL` with your backend server URL:
   ```kotlin
   private val BASE_URL = "https://your-backend-api.com"
   ```

### Permissions
The app requires the following permissions (declared in `AndroidManifest.xml`):
- `INTERNET`
- `READ_MEDIA_IMAGES` (for Android 13+)
- `READ_EXTERNAL_STORAGE` (for older versions)

## Backend Requirements
The app expects a `POST` request at `${BASE_URL}/remove-bg` with `multipart/form-data` containing an `image` file. The response should be the raw bytes of the processed image.

## Author
**Denis**
