package dependencies

@Suppress("MayBeConstant", "SpellCheckingInspection")
object Dep {
    object GradlePlugin {
        val android = "com.android.tools.build:gradle:4.0.2"
        val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10"
    }

    object Test {
        val junit = "junit:junit:4.13"
        val testRunner = "androidx.test:runner:1.3.0"
        val testRules = "androidx.test:rules:1.3.0"
        val testCoreKtx = "androidx.test:core-ktx:1.3.0"
        val androidJunit4Ktx = "androidx.test.ext:junit-ktx:1.1.2"
        val archCore = "androidx.arch.core:core-testing:2.1.0"
        val coroutinesTest =
            "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.9"

        object Mockito {
            val core = "org.mockito:mockito-core:3.5.13"
            val android = "org.mockito:mockito-android:3.5.13"
            val kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:+"
        }

        object Robolectric {
            val client = "org.robolectric:robolectric:4.4"
        }

        object Espresso {
            val core = "androidx.test.espresso:espresso-core:3.3.0"
            val contrib = "androidx.test.espresso:espresso-contrib:3.3.0"
        }
    }

    object AndroidX {
        val appCompat = "androidx.appcompat:appcompat:1.2.0"
        val design = "com.google.android.material:material:1.2.1"
        val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.2"
        val coreKtx = "androidx.core:core-ktx:1.3.2"
        val preference = "androidx.preference:preference:1.1.1"
        val card = "androidx.cardview:cardview:1.0.0"
        val activityKtx = "androidx.activity:activity-ktx:1.1.0"
        val fragmentKtx = "androidx.fragment:fragment-ktx:1.2.5"
    }

    object Kotlin {
        val stdlibJvm = "org.jetbrains.kotlin:kotlin-stdlib:1.4.10"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9"
        val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9"
    }

    object Koin {
        val core = "org.koin:koin-core:2.1.6"
        val android = "org.koin:koin-android:2.1.6"
        val viewModel = "org.koin:koin-androidx-viewmodel:2.1.6"
        val test = "org.koin:koin-test:2.1.6"
    }

    object OkHttp {
        val client = "com.squareup.okhttp3:okhttp:4.9.0"
        val mockWebServer = "com.squareup.okhttp3:mockwebserver:4.9.0"
    }

    object Retrofit {
        val client = "com.squareup.retrofit2:retrofit:2.9.0"
        val converterGson = "com.squareup.retrofit2:converter-gson:2.9.0"
    }

    object Zxing {
        val core = "com.google.zxing:core:3.3.2"
        val android = "com.journeyapps:zxing-android-embedded:3.6.0"
    }

    val eventBus = "org.greenrobot:eventbus:3.2.0"
    val logger = "com.orhanobut:logger:2.2.0"
}
