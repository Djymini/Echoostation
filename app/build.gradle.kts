plugins {
    id("com.android.application")
}

val cleLastfm: String = if (project.hasProperty("CLE_LASTFM")) project.property("CLE_LASTFM") as String else ""
val clientIdSpotify: String = if (project.hasProperty("CLIENT_ID_SPOTIFY")) project.property("CLIENT_ID_SPOTIFY") as String else ""
val clientSecretSpotify: String = if (project.hasProperty("CLIENT_SECRET_SPOTIFY")) project.property("CLIENT_SECRET_SPOTIFY") as String else ""


android {
    namespace = "com.djymini.echoostation"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
    }

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

        buildConfigField("String", "CLE_LASTFM", "\"$cleLastfm\"")
        buildConfigField("String", "CLIENT_ID_SPOTIFY", "\"$clientIdSpotify\"")
        buildConfigField("String", "CLIENT_SECRET_SPOTIFY", "\"$clientSecretSpotify\"")
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

    //Exoplayer
    implementation("androidx.media3:media3-exoplayer:1.8.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.8.0")
    implementation("androidx.media3:media3-ui:1.8.0")
    implementation("androidx.media3:media3-ui-compose:1.8.0")
    implementation ("androidx.media3:media3-session:1.8.0")

    // Retrofit pour les appels API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp pour intercepteurs (facultatif mais pratique)
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Scrollbar
    implementation("io.github.l4digital:fastscroll:2.1.0")

}
