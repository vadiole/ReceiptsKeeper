plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
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
        buildConfigField(
            "String",
            "URL_VALIDATOR",
            "\"https://cabinet\\\\.sfs\\\\.gov\\\\.ua/cashregs/check\\\\?id=.+&date=[0-9]{8}\""
        )
        buildConfigField(
            "String",
            "URL_SLICE_RECEIPT",
            "\"https://cabinet.tax.gov.ua/ws/api_public/rro/chkAllWeb\""
        )
        buildConfigField(
            "String",
            "URL_SLICE_CAPTCHA",
            "\"https://www.google.com/recaptcha/api2/payload\""
        )
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
            "SourceLockedOrientationActivity",
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

    // chrome tabs
    implementation("androidx.browser:browser:1.4.0")

    // qr scanner
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    val lifecycleVersion = "2.4.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    // for java 8 language features support
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    // coroutines for async
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    // hilt for di
    implementation("com.google.dagger:hilt-android:2.40.1")
    kapt("com.google.dagger:hilt-android-compiler:2.40.1")

    // serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")

    // room to store receipts
    val roomVersion = "2.3.0"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    // https://issuetracker.google.com/issues/174695268#comment9  can be removed after room 2.4.0
    kapt("org.xerial:sqlite-jdbc:3.36.0")
}

kapt {
    correctErrorTypes = true
}