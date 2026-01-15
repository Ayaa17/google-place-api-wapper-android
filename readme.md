# Google Place API Wrapper for Android

[![Release](https://img.shields.io/github/v/release/Ayaa17/google-place-api-wapper-android?label=Release&sort=semver)](https://github.com/Ayaa17/google-place-api-wapper-android/releases)
[![License](https://img.shields.io/github/license/Ayaa17/google-place-api-wapper-android)](LICENSE)

A lightweight Android library that wraps Google Place API, making it easier to search places, get place details, and integrate with your Android app.  

This library is distributed as an AAR and published to **GitHub Packages**. CI workflow automatically builds and publishes the library on every release. It is also available via **JitPack**.

---

## Features

- Simple wrapper around Google Place API for Android
- Search places by query or coordinates
- Get place details including location and types
- Supports Kotlin and Java projects

---

## Installation

### 1️⃣ Add JitPack repository

Edit your **`settings.gradle.kts`** and add at the end of `repositories`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### 2️⃣ Add the dependency

```gradle
dependencies {
    implementation("com.github.Ayaa17:google-place-api-wapper-android:v0.0.2")
}
```

---

## Usage

```kotlin
val cb: PlaceCallback = object : PlaceCallback {
    override fun onSuccess(response: Any): Int {
        Log.d(TAG, "onSuccess: $response")
        resultText = response.toString()
        return 0
    }

    override fun onError(error: Throwable): Int {
        Log.d(TAG, "onError: $error")
        resultText = error.toString()
        return 0
    }
}

val key = "PUT_KEY_HERE" // fixme: hide sec key
placeApi = PlaceApiWapper(this, key, cb)

val keyword = "台北"
placeApi?.autocompletePlaces(keyword)

```

---

## Contributing

Contributions are welcome!

* Fork the repository
* Create a feature branch (`git checkout -b feature/my-feature`)
* Commit your changes (`git commit -am 'Add some feature'`)
* Push to the branch (`git push origin feature/my-feature`)
* Open a Pull Request

---

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

```


