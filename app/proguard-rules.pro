# Keep kotlinx.serialization generated serializers
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class com.maxime.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.maxime.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}
