# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep OpenCSV classes
-keep class com.opencsv.** { *; }
-dontwarn com.opencsv.**

# Keep ZXing classes
-keep class com.google.zxing.** { *; }
-keep class com.journeyapps.barcodescanner.** { *; }
-dontwarn com.google.zxing.**

# Keep Zebra EMDK classes
-keep class com.symbol.** { *; }
-dontwarn com.symbol.**

# Keep model classes (for Parcelable)
-keep class com.zebra.shoppinglistscanner.model.** { *; }

# Keep ViewBinding classes
-keep class com.zebra.shoppinglistscanner.databinding.** { *; }

# Keep ViewModel classes
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep BroadcastReceiver
-keep class * extends android.content.BroadcastReceiver {
    <init>(...);
}