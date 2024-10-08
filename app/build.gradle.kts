plugins {
    kotlin("android")
    kotlin("kapt")
    id("com.android.application")
}

android {
    namespace = "com.mrapps.chatrecovery"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mrapps.chatrecovery"
        minSdk = 24
        //noinspection EditedTargetSdkVersion
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    implementation("com.airbnb.android:lottie:6.2.0")
    // koin for dependency injection
    implementation("io.insert-koin:koin-android:3.5.0")

    implementation("androidx.preference:preference-ktx:1.2.1")

    implementation ("com.github.bumptech.glide:glide:4.13.1")

    implementation("com.github.MugdhaRahman:horizontalstackedbarview:0.3")

}