plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.skinaura20"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.skinaura20"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // AndroidX & Material
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.gridlayout)

    // JUnit
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Image Slider
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")

    // ✅ Firebase Dependencies (Optimized)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0")) // BOM use karna best hai
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Glide: Images dikhane ke liye
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // OkHttp: Cloudinary upload ke liye
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Lottie Animation
    implementation("com.airbnb.android:lottie:6.1.0")

    // ✅ MPAndroidChart (Sahi Syntax)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}