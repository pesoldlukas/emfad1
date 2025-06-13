# Add project specific ProGuard rules here.
-keep class com.emfad.app.** { *; }
-keepclassmembers class com.emfad.app.** { *; }

# Keep Bluetooth classes
-keep class android.bluetooth.** { *; }

# Keep Compose classes
-keep class androidx.compose.** { *; }
-keep class androidx.navigation.** { *; }
