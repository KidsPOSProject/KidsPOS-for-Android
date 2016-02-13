##--------------- Default Settings  ----------
-optimizationpasses 10
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-dontnote
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.app.Activity{
    public void *(android.view.View);
}
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet.AttributeSet, int);
    public void set*(...);
}
-keep public class **.R
-keep class javax.** { *; }
-keep class org.** { *; }
##---------------End: Default Settings  ----------
##--------------- Gson  ----------
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }

##--------------- Jackson  ----------
-keep class com.fasterxml.** { *; }
-keep interface com.fasterxml.** {*;}
-dontwarn com.fasterxml.**
-dontwarn org.w3c**
-keep class org.w3c**

##--------------- Butterknife  ----------
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}

##--------------- HttpClient  ----------
-keep class org.apache.** { *; }
-dontwarn org.apache.**

##--------------- square Product ----------
-keep class com.squareup**
-dontwarn com.squareup**
-keepattributes *Annotation*,EnclosingMethod
-dontwarn okio.**
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

##--------------- Android Support Library ----------
-dontwarn android.support.v4.**
-dontwarn android.support.v7.**
-keep class android.support.v4.** { *; }
-keep class android.support.v7.** {*;}
-keep class com.facebook.** { *; }
-keep class android.webkit.WebViewClient
-keep class * extends android.webkit.WebViewClient
-keepclassmembers class * extends android.webkit.WebViewClient {
    <methods>;
}

##-------------- Log ----------------
-assumenosideeffects class * android.util.Log {
    <methods>;
}
-assumenosideeffects class * info.nukoneko.cuc.kidspos.util.KPLogger {
    <methods>;
}

##-------------- retrolambda ----------------
-dontwarn java.lang.invoke.*


-keepattributes Signature

-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement