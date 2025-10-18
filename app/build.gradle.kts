plugins {
  id("com.android.application") version "8.5.2"
  id("org.jetbrains.kotlin.android") version "1.9.24"
  id("com.google.devtools.ksp") version "1.9.24-1.0.20"
}

android {
  namespace = "com.miam.app"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.miam.app"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
    vectorDrawables { useSupportLibrary = true }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions { jvmTarget = "17" }
  buildFeatures { viewBinding = true }
}

val roomVersion = "2.6.1"
val lifecycleVersion = "2.8.6"

dependencies {
  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("androidx.fragment:fragment-ktx:1.8.4")

  // Lifecycle / LiveData / ViewModel
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:\")
  implementation("androidx.lifecycle:lifecycle-livedata-ktx:\")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:\")

  // Room + KSP
  implementation("androidx.room:room-ktx:\")
  implementation("androidx.room:room-runtime:\")
  ksp("androidx.room:room-compiler:\")

  // CameraX + ML Kit
  implementation("androidx.camera:camera-core:1.3.4")
  implementation("androidx.camera:camera-camera2:1.3.4")
  implementation("androidx.camera:camera-lifecycle:1.3.4")
  implementation("androidx.camera:camera-view:1.3.4")
  implementation("com.google.mlkit:barcode-scanning:17.2.0")

  // Network (OpenFoodFacts)
  implementation("com.squareup.okhttp3:okhttp:4.12.0")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}