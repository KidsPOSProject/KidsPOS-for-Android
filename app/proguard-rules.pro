#------ KidsPOS4j ------

##------ sqlite-jdbc ------
-keep class org.sqlite.** {
    <fields>;
    <methods>;
}

##------ okhttp3 ------
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.* { *; }
-dontwarn okio.**

##------ Retrofit -------
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-dontwarn rx.**
-dontwarn com.squareup.okhttp.*
-dontwarn com.google.appengine.api.urlfetch.*

#------ Android Support -------
-dontwarn android.support.**
-keep class android.support.** { *; }
-keep interface android.support.** { *; }

#------ Retrolambda -------
-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*

##------ RxJava ------
-dontwarn sun.misc.Unsafe

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
   long producerNode;
   long consumerNode;
}