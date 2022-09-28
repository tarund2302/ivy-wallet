import com.ivy.buildsrc.Hilt

apply<com.ivy.buildsrc.IvyComposePlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    implementation(project(":common"))
    implementation(project(":design-system"))
    implementation(project(":ui-components-old"))
    implementation(project(":app-base"))
    implementation(project(":core:ui"))
    implementation(project(":core:data-model"))
    implementation(project(":navigation"))
    implementation(project(":temp-persistence"))
    implementation(project(":temp-domain"))

    implementation(project(":widgets"))
}