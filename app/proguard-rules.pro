-dontoptimize
-dontobfuscate

# Keep models and entities
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.vocabmaster.app.data.model.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }

# Retrofit & Gson
-keep class retrofit2.** { *; }
-keepclassmembers class * {
    @retrofit2.http.** <methods>;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
