# Jatrilrun Android Application

Jatrilrun adalah aplikasi Android modern yang dikembangkan menggunakan **Kotlin** dan **Jetpack Compose**, dengan menerapkan prinsip **Clean Architecture** (pemisahan layer Data, Domain, dan Presentation) serta pola arsitektur **MVVM (Model-View-ViewModel)**.

---

## 🛠️ Teknologi & Library yang Digunakan

- **Bahasa:** [Kotlin](https://kotlinlang.org/)
- **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) & Material 3
- **Dependency Injection:** [Dagger Hilt](https://dagger.dev/hilt/)
- **Arsitektur:** Clean Architecture + MVVM
- **Navigasi:** Jetpack Navigation Compose (`androidx.navigation.compose`)
- **Database Lokal & Penyimpanan:**
  - [Room Database](https://developer.android.com/training/data-storage/room) (dengan KSP) untuk penyimpanan data terstruktur.
  - [Jetpack DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore) untuk manajemen sesi pengguna.
- **Networking:**
  - [Retrofit 2](https://square.github.io/retrofit/) & [OkHttp 3](https://square.github.io/okhttp/) untuk komunikasi REST API.
  - Persistent CookieJar untuk pengelolaan sesi/cookie.
- **Desain & Kustomisasi:**
  - Font Kustom: Plus Jakarta Sans (Regular, Medium, SemiBold, Bold, ExtraBold).
  - Skala Desain Kustom: `JrDimens`, `JrSpacing`, `JrShapes`, dan kustomisasi Tema Material 3 (`Theme.kt`).

---

## 📂 Struktur Project

Struktur direktori project berada di package `com.example.jatrilrun`:

```text
com.example.jatrilrun/
│
├── data/                    # Layer Data (Implementasi & Sumber Data)
│   ├── local/
│   │   ├── database/        # Room Database (AppDatabase, UserDao, UserEntity)
│   │   └── datastore/       # SessionManager (DataStore Preferences)
│   ├── mapper/              # Mappers (Konversi DTO ke Domain Model)
│   ├── remote/
│   │   ├── api/             # Retrofit ApiService
│   │   ├── client/          # NetworkClient & Konfigurasi OkHttp
│   │   └── dto/             # Data Transfer Objects (AuthRequest, AuthResponse, dll)
│   └── repository/          # Implementasi Repository (AuthRepositoryImpl)
│
├── domain/                  # Layer Domain (Business Logic & Kontrak)
│   ├── model/               # Model Entitas Domain (User)
│   └── repository/          # Interface Repository (AuthRepository)
│
├── di/                      # Dependency Injection Modules (Hilt)
│   ├── DatabaseModule.kt
│   ├── DataStoreModule.kt
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
│
├── ui/                      # Layer Presentation (UI & State)
│   ├── components/          # Reusable UI (CommonButton, CommonTextField, LoadingIndicator)
│   ├── navigation/          # NavGraph & Screen Routes
│   ├── screens/
│   │   ├── auth/            # LoginScreen, RegisterScreen, AuthViewModel
│   │   ├── home/            # HomeScreen, HomeViewModel
│   │   └── payment/         # PaymentScreen, PaymentViewModel
│   └── theme/               # Tema, Warna, Tipografi, Shape, & Dimensi Kustom
│
├── JatrilrunApp.kt          # Kelas Application (dianotasi @HiltAndroidApp)
└── MainActivity.kt          # Activity Utama (dianotasi @AndroidEntryPoint)
```

---

## 🔄 Alur Code & Cara Kerja Aplikasi (Code Flow)

### 1. Inisialisasi Aplikasi & Dependency Injection
- **`JatrilrunApp.kt`** dianotasi dengan `@HiltAndroidApp` sebagai titik awal Hilt Dependency Injection container.
- **`MainActivity.kt`** dianotasi dengan `@AndroidEntryPoint`, yang menyiapkan Compose `setContent` dan menginisialisasi `NavGraph`.
- Modul-modul di package **`di/`** menyediakan instance singleton atau scoped untuk Retrofit, Room Database, DataStore, dan Repository secara otomatis menggunakan Hilt.

### 2. Alur Navigasi (`ui/navigation/`)
- **`Screen.kt`** mendefinisikan rute-rute navigasi aplikasi (misal: `Login`, `Register`, `Home`, `Payment`).
- **`NavGraph.kt`** mengatur grafik navigasi Compose (`NavHost`), menghubungkan setiap rute URL/path dengan Composable screen yang sesuai.

### 3. Alur Data & State Management (Clean Architecture + MVVM)
1. **UI Layer (`ui/screens/`)**:
   - Pengguna berinteraksi dengan Composable (contoh: `LoginScreen`).
   - `LoginScreen` mendapatkan instance `AuthViewModel` melalui `hiltViewModel()`.
   - Ketika tombol aksi ditekan, UI memanggil fungsi di ViewModel.
2. **ViewModel (`ui/screens/auth/AuthViewModel.kt`)**:
   - ViewModel menjalankan business logic, memanggil fungsi di Domain/Repository, dan mengelola state UI (`StateFlow` / Compose `mutableStateOf`).
3. **Domain Layer (`domain/`)**:
   - Berisi interface `AuthRepository` dan model murni (`User`) yang independen dari framework luar.
4. **Data Layer (`data/`)**:
   - **`AuthRepositoryImpl`** mengimplementasikan kontrak `AuthRepository`.
   - Mengambil data dari **Remote** (`ApiService` via Retrofit) atau **Local** (`UserDao` via Room / `SessionManager` via DataStore).
   - Menggunakan **Mapper** (`UserMapper`) untuk mengubah DTO API menjadi Domain Model sebelum dikembalikan ke ViewModel.

---

## 🚀 Cara Menjalankan Project

1. Buka project ini di **Android Studio** (Koala / Jellyfish / Meerkat atau versi terbaru dengan dukungan Compose & KSP).
2. Pastikan Gradle sync berhasil.
3. Jalankan aplikasi pada emulator atau perangkat fisik Android melalui tombol **Run** di Android Studio.
