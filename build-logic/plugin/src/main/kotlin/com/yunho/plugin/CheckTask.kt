package com.yunho.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.HttpURLConnection
import java.net.URI

internal abstract class CheckTask : DefaultTask() {

    @TaskAction
    fun check() {
        val metricsDir = File(project.rootProject.buildDir, "compose_metrics")
        if (metricsDir.exists()) {
            execute()
        } else {
            println("There is No Compose Metrics")
        }
    }

    private fun execute() {

        val connection = URI(MENDABLE).toURL().openConnection() as HttpURLConnection

        val metricsDir =
            File(project.rootProject.layout.buildDirectory.get().asFile, "compose_metrics")

        val file = File(metricsDir, "mendable.jar")

        connection.inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
                println("Saved to: ${file.path}")
            }
        }

        runJar(file)

        val indexFile = File(metricsDir, "index.html")
        openBrowserUsingProcessBuilder(indexFile)
    }

    private fun runJar(jarFile: File) {
        try {
            val process = ProcessBuilder("java", "-jar", jarFile.absolutePath)
                .directory(jarFile.parentFile)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()

            process.waitFor()
        } catch (e: Exception) {
            println("JAR 실행 중 오류 발생: ${e.message}")
        }
    }

    private fun openBrowserUsingProcessBuilder(file: File) {
        try {
            val os = System.getProperty("os.name").lowercase()
            val command = when {
                os.contains("win") -> listOf("cmd", "/c", "start", file.absolutePath)
                os.contains("mac") -> listOf("open", file.absolutePath)
                os.contains("nix") || os.contains("nux") -> listOf("xdg-open", file.absolutePath)
                else -> null
            }

            command?.let {
                ProcessBuilder(it)
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start()
            } ?: println("지원되지 않는 OS입니다.")
        } catch (e: Exception) {
            println("브라우저 실행 중 오류 발생: ${e.message}")
        }
    }

    companion object {
        private const val MENDABLE =
            "https://github.com/jayasuryat/mendable/releases/download/v0.7.0/mendable.jar"
    }
}