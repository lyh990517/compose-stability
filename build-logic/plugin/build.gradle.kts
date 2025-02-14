plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    compileOnly(libs.android.tools.build.gradle)
    compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("stability") {
            id = "compose.stability"
            implementationClass = "com.yunho.plugin.ComposeStabilityPlugin"
        }
    }
}
