# ProGuard rules to shrink & optimize code

# Keep application classes
-keep class com.example.audiovideoplayer.** { *; }

# Keep classes required for Jetpack Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep ExoPlayer classes (Media3)
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Keep Lifecycle and ViewModel
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

# Keep Accompanist and Coil
-keep class com.google.accompanist.** { *; }
-dontwarn com.google.accompanist.**

# Keep Coil classes (Image Loading)
-keep class coil.** { *; }
-dontwarn coil.**


# Keep Kotlin Coroutines
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Keep Parcelable classes
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Hide source file names for security
-renamesourcefileattribute SourceFile
