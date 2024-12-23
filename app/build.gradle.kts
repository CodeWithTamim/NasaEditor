plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.nasahacker.nasaeditor"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nasahacker.nasaeditor"
        minSdk = 21
        targetSdk = 35
        versionCode = 13
        versionName = "1.0.13-beta"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    val abiFilterList = (properties["ABI_FILTERS"] as? String)?.split(';')
    splits {
        abi {
            isEnable = true
            reset()
            if (!abiFilterList.isNullOrEmpty()) {
                include(*abiFilterList.toTypedArray())
            } else {
                include(
                    "arm64-v8a",
                    "armeabi-v7a",
                    "x86_64",
                    "x86"
                )
            }
            isUniversalApk = abiFilterList.isNullOrEmpty()
        }
    }

    val defaultVersionCode = defaultConfig.versionCode
    val versionCodes = mapOf(
        "armeabi-v7a" to defaultVersionCode,
        "arm64-v8a" to defaultVersionCode,
        "x86" to defaultVersionCode,
        "x86_64" to defaultVersionCode,
        "universal" to defaultVersionCode
    )

    // Correcting ABI filters and output file names
    applicationVariants.all { variant ->
        variant.outputs.all { output ->
            // Get ABI or default to "universal"
            val abi = output.filters.find { it.name == "abi" }?.identifier ?: "universal"

            // Set the output file name based on ABI
            val outputFile = output.outputFile
            output.outputFileName = "convertit_${abi}_release.apk"

            // Set version code override based on ABI
            versionCodes[abi]?.let {
                output.versionCodeOverride = it
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.lottie)
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)
}