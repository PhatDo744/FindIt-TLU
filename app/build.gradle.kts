plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.findittlu"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.findittlu"
        minSdk = 24
        targetSdk = 35
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    // AndroidX Navigation Component
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.navigation.runtime.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.material.v150)
    implementation(libs.circleimageview)
    // Retrofit
    implementation(libs.retrofit)
// Converter Gson cho Retrofit
    implementation(libs.converter.gson)
// OkHttp (nếu cần logging)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
// Nếu dùng LiveData adapter cho Retrofit (không bắt buộc)
    implementation(libs.adapter.rxjava2)
}