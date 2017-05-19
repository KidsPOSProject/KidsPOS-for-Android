#------ KidsPOS4j ------

-dontwarn info.nukoneko.cuc.kidspos4j.model.**

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

##------ Jackson -------
-keep class com.fasterxml.jackson.databind.** { *; }
-keepattributes *Annotation*,EnclosingMethod
-keep public class info.nukoneko.cuc.kidspos4j.model.** {
  public void set*(***);
  public *** get*();
}

-keepattributes *Annotation*,EnclosingMethod,Signature
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.**
-keep class org.codehaus.** { *; }
-keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
  public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *;
}


#------ Android Support -------
-dontwarn android.support.**
-keep class android.support.** { *; }
-keep interface android.support.** { *; }

#------ ButterKnife -------
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

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