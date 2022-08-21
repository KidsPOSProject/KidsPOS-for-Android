package dependencies

@Suppress("SpellCheckingInspection")
object Dep {
    object GradlePlugin {
        const val android = "com.android.tools.build:gradle:7.1.3"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}"
    }

    object Test {
        const val junit = "junit:junit:4.13.2"
        const val testRunner = "androidx.test:runner:1.4.0"
        const val testRules = "androidx.test:rules:1.4.0"
        const val testCoreKtx = "androidx.test:core-ktx:1.4.0"
        const val androidJunit4Ktx = "androidx.test.ext:junit-ktx:1.1.3"
        const val archCore = "androidx.arch.core:core-testing:2.1.0"
        const val coroutinesTest =
            "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Kotlin.version}"

        object Mockito {
            const val core = "org.mockito:mockito-core:3.11.2"
            const val android = "org.mockito:mockito-android:3.11.2"
            const val kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
        }

        object Robolectric {
            const val client = "org.robolectric:robolectric:4.4"
        }
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
        const val version = "1.6.21"
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4"
    }

    object Koin {
        private const val version = "3.1.2"
        const val core = "io.insert-koin:koin-core:$version"
        const val test = "io.insert-koin:koin-test:$version"
        const val android = "io.insert-koin:koin-android:$version"
    }

    object OkHttp {
        private const val version = "4.9.3"
        const val client = "com.squareup.okhttp3:okhttp:$version"
        const val mockWebServer = "com.squareup.okhttp3:mockwebserver:$version"
    }

    object Retrofit {
        private const val version = "2.9.0"
        const val client = "com.squareup.retrofit2:retrofit:$version"
        const val converterGson = "com.squareup.retrofit2:converter-gson:$version"
    }

    object Zxing {
        const val core = "com.google.zxing:core:3.3.0"
        const val android = "com.journeyapps:zxing-android-embedded:4.3.0"
    }

    const val eventBus = "org.greenrobot:eventbus:3.2.0"
    const val logger = "com.orhanobut:logger:2.2.0"
}
