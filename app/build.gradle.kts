plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "vadiole.receiptkeeper"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
        resourceConfigurations.addAll(listOf("en", "uk", "ru"))
        setProperty("archivesBaseName", "ReceiptKeeper v$versionName ($versionCode)")
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    packagingOptions {
        resources.excludes.addAll(
            listOf(
                "META-INF/LICENSE",
                "META-INF/NOTICE",
                "META-INF/kotlin.properties",
            )
        )
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }

    lint {
        disable(
            "SetTextI18n",
            "RtlHardcoded", "RtlCompat", "RtlEnabled",
            "ViewConstructor",
            "UnusedAttribute",
        )
    }
}

dependencies {
    implementation(project(mapOf("path" to ":core")))

    // compat libraries
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.fragment:fragment-ktx:1.4.0")

    // recycler view for lists
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // lifecycle and viewmodel for mvvm
    val lifecycleVersion = "2.4.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    // for java 8 language features support
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    // tool for parsing html
    implementation("org.jsoup:jsoup:1.10.3")

    // coroutines for async
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    // hilt for di
    implementation("com.google.dagger:hilt-android:2.38.1")
    kapt("com.google.dagger:hilt-android-compiler:2.38.1")

    // room to store receipts
    val roomVersion = "2.3.0"
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
}

kapt {
    correctErrorTypes = true
}