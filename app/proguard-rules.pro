
-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.ykizilkaya.t24.models.** { *; }

