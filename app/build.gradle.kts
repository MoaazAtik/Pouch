plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)

    alias(libs.plugins.google.devtools.ksp)
}

android {
    namespace = "com.thewhitewings.pouch"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.thewhitewings.pouch"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Used by themes.xml file that is needed for "?attr/colorPrimaryContainer" in drawables or other XML files
    implementation(libs.material)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Room core library
    implementation(libs.androidx.room.runtime)
    // Kotlin Symbol Processing (KSP) for Kotlin annotation processing
    ksp(libs.androidx.room.compiler)
    // Kotlin Extensions and Coroutines support for Room (optional)
    implementation(libs.androidx.room.ktx)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Lottie Animations
    implementation(libs.lottie.compose)

    // JUnit
    testImplementation(libs.junit)
//    testImplementation(libs.androidx.junit)
//    testImplementation(libs.androidx.junit.ktx)

    // Kotlin Coroutines Test
    testImplementation(libs.kotlinx.coroutines.test)

    // Mockito for Mocking dependencies
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)

    // Room Testing
    testImplementation(libs.androidx.room.testing)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}