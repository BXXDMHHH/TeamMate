# TeamMate Android

Android client for the TeamMate demo, built with Kotlin + Jetpack Compose + Retrofit.

## Open in Android Studio

Open the `teammate-android` directory as a Gradle project.

Recommended environment:

- Android Studio recent stable release
- JDK 17
- Android SDK 35

## Backend URL

The default URL in `app/src/main/java/com/teammate/android/data/ApiClient.kt` is:

```text
http://10.0.2.2:8080/
```

This is convenient when the Spring Boot server runs on the same computer as the Android Emulator.

If the Spring Boot server is running in GitHub Codespaces, forward port `8080` in Codespaces and replace `BASE_URL` with the forwarded HTTPS URL, keeping the trailing `/`.

Example:

```kotlin
private const val BASE_URL = "https://your-codespace-forwarded-url/"
```

## Demo account

```text
Email: alice@test.com
Password: 123456
```

## Current flow

```text
Login
  -> Project list
  -> Project Alpha
  -> Chat history
  -> Send message
```

AI features will be connected in the next step.
