plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
	id("androidx.navigation.safeargs.kotlin")
	id("kotlin-parcelize")	// allow automatic @Parcelize (for bundles)
	kotlin("kapt") 			// Kotlin Annotation Processing Tool, for Room
}

android {
    namespace = "com.papum.homecookscompanion"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.papum.homecookscompanion"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        viewBinding = true
    }
}

dependencies {

	val work_version = "2.9.0"

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.room:room-ktx:2.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    kapt("androidx.room:room-compiler:2.6.1")

	// WorkManager
	// (Java only)
	implementation("androidx.work:work-runtime:$work_version")
	// Kotlin + coroutines
	implementation("androidx.work:work-runtime-ktx:$work_version")
	// optional - RxJava2 support
	implementation("androidx.work:work-rxjava2:$work_version")
	// optional - GCMNetworkManager support
	implementation("androidx.work:work-gcm:$work_version")
	// optional - Test helpers
	androidTestImplementation("androidx.work:work-testing:$work_version")
	// optional - Multiprocess support
	implementation("androidx.work:work-multiprocess:$work_version")

	// ia vision
	implementation("com.google.android.gms:play-services-vision:20.1.3")
	implementation(fileTree("libs") {
		include("*.jar")
	})
	implementation(project(":opencv"))	// add opencv dependency

}
