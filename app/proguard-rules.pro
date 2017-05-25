#------ KidsPOS4j ------

##------ Retrofit -------
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-dontwarn com.squareup.okhttp.*
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.* { *; }
-dontwarn okio.**
-dontwarn com.google.appengine.api.urlfetch.*

#------ Android Support -------
-dontwarn android.support.**
-keep class android.support.** { *; }
-keep interface android.support.** { *; }

#------ Gson -------
-keep class info.nukoneko.cuc.android.kidspos.entity.** { *; }

#------ EventBus -------
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
