plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "tr.kisisel.vpn"
    compileSdk = 34

    defaultConfig {
        applicationId = "tr.kisisel.vpn"
        minSdk = 24           // GoBackend min 24 istiyor
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions { jvmTarget = "17" }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    // WireGuard Go kütüphanesi (kendi VpnService + tünel yönetimi)
    implementation("com.wireguard.android:tunnel:1.0.20230706")
    // Şifreli SharedPreferences
    implementation("androidx.security:security-crypto:1.0.0")
    // Core
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    // Desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}
