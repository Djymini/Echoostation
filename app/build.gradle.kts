plugins {
    id("com.android.application")
}

android {
    namespace = "com.djymini.echoostation"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.djymini.echoostation"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments.put("room.schemaLocation", "$projectDir/schemas")
            }
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation ("com.google.code.gson:gson:2.10.1")
    testImplementation(libs.junit)
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("androidx.test:core:1.7.0")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation ("androidx.test:core:1.7.0")
    androidTestImplementation ("androidx.test.ext:junit:1.3.0")
    androidTestImplementation ("androidx.arch.core:core-testing:2.2.0")

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    //Room dependencies
    implementation(libs.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.room.rxjava2)
    implementation(libs.room.rxjava3)
    implementation(libs.room.guava)
    testImplementation(libs.room.testing)
    implementation(libs.room.paging)
}
