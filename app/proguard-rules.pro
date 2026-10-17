# Keep models and entities
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
# Firestore toObject()/set() and Gson map by field/getter name; R8 would rename them in release.
# (The old data.model.** rule matched no package.)
-keep class com.vocabmaster.app.domain.models.** { *; }
-keep class com.vocabmaster.app.data.local.** { *; }
-keep class com.vocabmaster.app.data.remote.TranslationResponse { *; }
-keep class com.vocabmaster.app.data.remote.ResponseData { *; }
-keep class com.vocabmaster.app.data.repository.Master* { *; }

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
