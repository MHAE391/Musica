// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        val nav_version = "2.7.5"
        //noinspection GradleDependency
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")

    }
    plugins {
        id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
    }
}
plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}