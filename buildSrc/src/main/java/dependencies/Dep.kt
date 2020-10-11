package dependencies

@Suppress("MayBeConstant", "SpellCheckingInspection")
object Dep {
    object GradlePlugin {
        val android = "com.android.tools.build:gradle:4.0.2"
        val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}"
    }

    object Test {
        val junit = "junit:junit:4.13"
        val testRunner = "androidx.test:runner:1.3.0"
        val testRules = "androidx.test:rules:1.3.0"
        val testCoreKtx = "androidx.test:core-ktx:1.3.0"
        val androidJunit4Ktx = "androidx.test.ext:junit-ktx:1.1.2"
        val archCore = "androidx.arch.core:core-testing:2.1.0"
        val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Kotlin.coroutinesVersion}"

        object Mockito {
            private val version = "3.5.13"
            val core = "org.mockito:mockito-core:$version"
            val android = "org.mockito:mockito-android:$version"
            val kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:+"
        }

        object Robolectric {
            private val version = "4.4"
            val client = "org.robolectric:robolectric:$version"
        }

        object Espresso {
            private val version = "3.3.0"
            val core = "androidx.test.espresso:espresso-core:$version"
            val contrib = "androidx.test.espresso:espresso-contrib:$version"
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
        val version = "1.4.10"
        val stdlibJvm = "org.jetbrains.kotlin:kotlin-stdlib:$version"
        val coroutinesVersion = "1.3.9"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
        val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion"
    }

    object Koin {
        private val version = "2.1.6"
        val core = "org.koin:koin-core:$version"
        val android = "org.koin:koin-android:$version"
        val viewModel = "org.koin:koin-androidx-viewmodel:$version"
        val test = "org.koin:koin-test:$version"
    }

    object OkHttp {
        private val version = "4.9.0"
        val client = "com.squareup.okhttp3:okhttp:$version"
        val mockWebServer = "com.squareup.okhttp3:mockwebserver:$version"
    }

    object Retrofit {
        private val version = "2.9.0"
        val client           = "com.squareup.retrofit2:retrofit:$version"
        val converterGson    = "com.squareup.retrofit2:converter-gson:$version"
    }

    object Zxing {
        val core = "com.google.zxing:core:3.3.2"
        val android = "com.journeyapps:zxing-android-embedded:3.6.0"
    }

    val eventBus = "org.greenrobot:eventbus:3.2.0"
    val logger = "com.orhanobut:logger:2.2.0"
}