package dependencies

@Suppress("SpellCheckingInspection")
object Dep {
    object GradlePlugin {
        const val android = "com.android.tools.build:gradle:7.1.3"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21"
        const val kotlinSerialization =
            "org.jetbrains.kotlin:kotlin-serialization:1.6.21"
    }

    object AndroidX {
        const val appCompat = "androidx.appcompat:appcompat:1.5.0"
        const val design = "com.google.android.material:material:1.6.1"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.4"
        const val activityKtx = "androidx.activity:activity-ktx:1.5.1"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:1.5.2"
        const val preferenceKtx = "androidx.preference:preference-ktx:1.2.0"
    }

    object Kotlin {
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4"
        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0"
        const val serializationConverter =
            "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0"
    }

    object Koin {
        const val android = "io.insert-koin:koin-android:3.1.2"
    }

    object OkHttp {
        const val client = "com.squareup.okhttp3:okhttp:4.9.3"
        const val mockWebServer = "com.squareup.okhttp3:mockwebserver:4.9.3"
    }

    object Retrofit {
        const val client = "com.squareup.retrofit2:retrofit:2.9.0"
    }

    object Zxing {
        const val android = "com.journeyapps:zxing-android-embedded:4.3.0"
    }

    const val eventBus = "org.greenrobot:eventbus:3.2.0"
    const val logger = "com.orhanobut:logger:2.2.0"
}
