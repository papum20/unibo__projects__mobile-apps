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

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
	implementation("androidx.preference:preference-ktx:1.2.1")
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

	/* google OCR (ML Kit) */
	// For bundling the model with your app:
	implementation("com.google.mlkit:text-recognition:16.0.0")				// latin script
	//implementation("com.google.mlkit:text-recognition-chinese:16.0.0")
	//implementation("com.google.mlkit:text-recognition-devanagari:16.0.0")
	//implementation("com.google.mlkit:text-recognition-japanese:16.0.0")
	//implementation("com.google.mlkit:text-recognition-korean:16.0.0")

	// For using the model in Google Play Services:
	//implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")				// latin script
	//implementation("com.google.android.gms:play-services-mlkit-text-recognition-chinese:16.0.0")
	//implementation("com.google.android.gms:play-services-mlkit-text-recognition-devanagari:16.0.0")
	//implementation("com.google.android.gms:play-services-mlkit-text-recognition-japanese:16.0.0")
	//implementation("com.google.android.gms:play-services-mlkit-text-recognition-korean:16.0.0")

	/* OSMdroid */
	implementation("org.osmdroid:osmdroid-android:6.1.18")
	//implementation("org.osmdroid:osmdroid-wms:6.1.18")		// Adds basic support for WMS servers, specifically GeoServer
	//implementation("org.osmdroid:osmdroid-mapsforge:6.1.18")	// Adds Maps Forge support
	//iplementation("org.osmdroid:osmdroid-geopackage:6.1.18")	// Adds geopackage support

	/* geolocalization */
	implementation("com.google.android.gms:play-services-location:21.2.0")
	implementation("com.google.android.gms:play-services-maps:18.2.0")

}
