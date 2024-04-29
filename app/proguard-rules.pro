# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class com.google.**
-keep class java.** { *; }
-keep class sun.** { *; }
-keep class org.** { *; }
-keep class com.vitorpamplona.core.models.** { *; }
-keep class com.vitorpamplona.domain.events.** { *; }
-keep class com.vitorpamplona.netra.atom.SingleEyeTestVO { *; }

-keep class com.android.** { *; }
-keep class dalvik.** { *; }

-keep class com.opencsv.**
-keep interface com.opencsv.**

-keep class net.**
-keep interface net.**

-keep interface com.google.**
-keep interface java.** { *; }
-keep interface sun.** { *; }
-keep interface org.** { *; }
-keep interface com.vitorpamplona.core.models.** { *; }
-keep interface com.vitorpamplona.domain.events.** { *; }
-keep interface com.vitorpamplona.netra.atom.SingleEyeTestVO { *; }

-keep interface com.android.** { *; }
-keep interface dalvik.** { *; }

-ignorewarnings