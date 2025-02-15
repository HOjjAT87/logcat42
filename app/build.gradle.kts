plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Our custom plugin
    alias(libs.plugins.build.config)
}

android {
    namespace = "com.example.logcat42"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.logcat42"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = false
    }
}


task("myCustomTask") {
    dependsOn(project.tasks.getByName("build"))

    doLast {
        println("I announce build complete!!!")
    }
}

buildConfig {
    enabled = true
    generateChangeLog = true

    customValues.put("VersionCode", 2)
    customValues.put("Version", "2.1.0")
    customValues.put("serverUrl", "http://some.server.com/api")
    customValues.put("buildTime", System.currentTimeMillis())
    customValues.put(
        "isRelease",
        (project.properties["isRelease"] as? String)?.toBoolean() ?: false
    )

    // Will not be parsed because of it's type
    customValues.put(
        "thisWillNotExist",
        uri("http://some.server.com")
    )
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
