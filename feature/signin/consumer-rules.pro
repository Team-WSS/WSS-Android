# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# consumer-rules.pro (feature.signin 모듈)
-keep class com.kakao.sdk.** { *; }
-keep interface com.kakao.sdk.** { *; }
-dontwarn com.kakao.sdk.**

# 카카오 로그인 관련 클래스 보호
-keep class com.kakao.sdk.auth.** { *; }
-keep class com.kakao.sdk.user.** { *; }
-keep class com.kakao.sdk.common.** { *; }

# 네트워크 관련 (카카오 SDK 의존성)
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**
