# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\progress\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
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
#Gson
-dontwarn com.google.gson.**
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }

#JJWT
-keepnames class com.fasterxml.jackson.databind.** { *; }
-dontwarn com.fasterxml.jackson.databind.*
-keepattributes InnerClasses
-keep class org.bouncycastle.** { *; }
-keepnames class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**
-keep class io.jsonwebtoken.** { *; }
-keepnames class io.jsonwebtoken.* { *; }
-keepnames interface io.jsonwebtoken.* { *; }
-dontwarn javax.xml.bind.DatatypeConverter
-dontwarn io.jsonwebtoken.impl.Base64Codec
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames interface com.fasterxml.jackson.** { *; }

#dom4j
-dontwarn org.dom4j.**
-keep class org.dom4j.**{*;}
-dontwarn org.xml.sax.**
-keep class org.xml.sax.**{*;}
-dontwarn com.fasterxml.jackson.**
-keep class com.fasterxml.jackson.**{*;}
-dontwarn com.pax.market.api.sdk.java.base.util.**
-keep class com.pax.market.api.sdk.java.base.util.**{*;}
-dontwarn org.w3c.dom.**
-keep class org.w3c.dom.**{*;}

#dto
-dontwarn com.pax.market.api.sdk.java.base.dto.**
-keep class com.pax.market.api.sdk.java.base.dto.**{*;}