# Proguard

    #okhttp
    -dontwarn com.squareup.okhttp.**
    -keep class com.squareup.okhttp.** { *; }
    -keep interface com.squareup.okhttp.** { *; }
    -dontwarn com.squareup.okhttp3.**
    -keep class com.squareup.okhttp3.** { *;}
    -dontwarn okio.**
    
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
    -dontwarn javax.xml.**
    -keep class javax.xml.**{*;}
    
    #dto
    -dontwarn com.pax.market.api.sdk.java.base.dto.**
    -keep class com.pax.market.api.sdk.java.base.dto.**{*;}
    
    #By default Google keeps classes of  Activity 、Service ... 
    -keep public class * extends android.app.Activity
    -keep public class * extends android.app.Service
    -keep public class * extends android.content.BroadcastReceiver
    -keep public class * extends android.preference.Preference

## 
