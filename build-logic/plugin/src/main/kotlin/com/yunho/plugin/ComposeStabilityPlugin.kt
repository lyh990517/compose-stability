package com.yunho.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class ComposeStabilityPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            val printComposeMetrics =
                project.tasks.register("printComposeMetrics", CheckTask::class.java) {
                    group = "compose"
                    description = "check stability"

                    dependsOn("build")
                }

            printComposeMetrics.configure {
                mustRunAfter("build")
            }

            allprojects {
                tasks.withType(org.jetbrains.kotlin.gradle.dsl.KotlinCompile::class.java)
                    .configureEach {
                        kotlinOptions {
                            freeCompilerArgs += listOf(
                                "-P",
                                "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" + rootProject.buildDir.absolutePath + "/compose_metrics/"
                            )
                            freeCompilerArgs += listOf(
                                "-P",
                                "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" + rootProject.buildDir.absolutePath + "/compose_metrics/"
                            )
                        }
                    }
            }
        }
    }
}