package dependencies

@Suppress("SpellCheckingInspection")
object Dep {
    object GradlePlugin {
        const val android = "com.android.tools.build:gradle:4.2.2"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21"
        const val releaseHub = "com.dipien:releases-hub-gradle-plugin:2.0.2"
    }

    object Test {
        const val junit = "junit:junit:4.13.2"
        const val testRunner = "androidx.test:runner:1.4.0"
        const val testRules = "androidx.test:rules:1.4.0"
        const val testCoreKtx = "androidx.test:core-ktx:1.4.0"
        const val androidJunit4Ktx = "androidx.test.ext:junit-ktx:1.1.3"
        const val archCore = "androidx.arch.core:core-testing:2.1.0"
        const val coroutinesTest =
            "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.1"

        object Mockito {
            const val core = "org.mockito:mockito-core:3.11.2"
            const val android = "org.mockito:mockito-android:3.11.2"
            const val kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
        }

        object Robolectric {
            const val client = "org.robolectric:robolectric:4.4"
        }

        object Espresso {
            const val core = "androidx.test.espresso:espresso-core:3.4.0"
            const val contrib = "androidx.test.espresso:espresso-contrib:3.4.0"
        }
    }

    object AndroidX {
        const val appCompat = "androidx.appcompat:appcompat:1.3.1"
        const val design = "com.google.android.material:material:1.4.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"
        const val coreKtx = "androidx.core:core-ktx:1.6.0"
        const val preference = "androidx.preference:preference:1.1.1"
        const val card = "androidx.cardview:cardview:1.0.0"
        const val activityKtx = "androidx.activity:activity-ktx:1.2.4"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:1.3.6"
        const val lifecycleRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:2.4.0-alpha02"
    }

    object Kotlin {
        const val stdlibJvm = "org.jetbrains.kotlin:kotlin-stdlib:1.5.21"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1"
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.1"
    }

    object Koin {
        const val core = "io.insert-koin:koin-core:3.1.2"
        const val test = "io.insert-koin:koin-test:3.1.2"
        const val android = "io.insert-koin:koin-android:3.1.2"
    }

    object OkHttp {
        const val client = "com.squareup.okhttp3:okhttp:4.9.1"
        const val mockWebServer = "com.squareup.okhttp3:mockwebserver:4.9.1"
    }

    object Retrofit {
        const val client = "com.squareup.retrofit2:retrofit:2.9.0"
        const val converterGson = "com.squareup.retrofit2:converter-gson:2.9.0"
    }

    object Zxing {
        const val core = "com.google.zxing:core:3.3.2"
        const val android = "com.journeyapps:zxing-android-embedded:3.6.0"
    }

    const val eventBus = "org.greenrobot:eventbus:3.2.0"
    const val logger = "com.orhanobut:logger:2.2.0"
}
