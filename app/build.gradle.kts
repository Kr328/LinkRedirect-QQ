plugins {
    id("com.android.application")
    kotlin("android")
}

dependencies {
    compileOnly(project(":hideapi"))

    implementation("androidx.annotation:annotation:1.3.0")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.core:core-ktx:1.7.0")
}
