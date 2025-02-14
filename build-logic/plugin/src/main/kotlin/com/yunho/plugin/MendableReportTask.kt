package com.yunho.plugin

import com.yunho.plugin.HtmlUtil.openBrowserUsingProcessBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.HttpURLConnection
import java.net.URI

internal abstract class MendableReportTask : DefaultTask() {

    @TaskAction
    fun check() {
        val metricsDir =
            File(project.rootProject.layout.buildDirectory.get().asFile, "compose_metrics")
        if (metricsDir.exists()) {
            executeMendable()
        } else {
            println("There is No Compose Metrics")
        }
    }

    private fun executeMendable() {
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
            println("JAR error: ${e.message}")
        }
    }

    companion object {
        private const val MENDABLE =
            "https://github.com/jayasuryat/mendable/releases/download/v0.7.0/mendable.jar"
    }
}