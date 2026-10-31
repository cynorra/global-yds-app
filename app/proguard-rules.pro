# Preserve generic signatures and annotations for reflection and Gson
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Keep models and entities
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
# Firestore toObject()/set() and Gson map by field/getter name; R8 would rename them in release.
-keep class com.vocabmaster.app.domain.models.** { *; }
-keep class com.vocabmaster.app.data.local.** { *; }
-keep class com.vocabmaster.app.data.remote.TranslationResponse { *; }
-keep class com.vocabmaster.app.data.remote.ResponseData { *; }
-keep class com.vocabmaster.app.data.repository.Master* { *; }
-keep class * extends com.google.gson.reflect.TypeToken { *; }
-keep class com.google.gson.** { *; }

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
