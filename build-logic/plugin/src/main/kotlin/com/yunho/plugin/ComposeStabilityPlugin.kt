package com.yunho.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

class ComposeStabilityPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            project.tasks.register(
                "reportComposeStabilityWithMendable",
                MendableReportTask::class.java
            ) {
                group = "compose"
                description = "report stability"

                dependsOn(
                    project.tasks.matching { it.name.startsWith("compile") && it.name.endsWith("DebugKotlin") }
                )
            }

            project.tasks.register(
                "reportComposeStability",
                ReportTask::class.java
            ) {
                group = "compose"
                description = "report stability"

                dependsOn(
                    project.tasks.matching { it.name.startsWith("compile") && it.name.endsWith("DebugKotlin") }
                )
            }

            allprojects {
                tasks.withType(KotlinCompilationTask::class.java)
                    .configureEach {
                        compilerOptions {
                            freeCompilerArgs.addAll(
                                "-P",
                                "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                                        rootProject.layout.buildDirectory.get().asFile.absolutePath + "/compose_metrics/",
                                "-P",
                                "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                                        rootProject.layout.buildDirectory.get().asFile.absolutePath + "/compose_metrics/"
                            )
                        }
                    }
            }
        }
    }
}