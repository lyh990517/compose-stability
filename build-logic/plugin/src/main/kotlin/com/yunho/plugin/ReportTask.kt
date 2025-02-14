package com.yunho.plugin

import com.yunho.plugin.HtmlUtil.generateHtml
import com.yunho.plugin.HtmlUtil.openBrowserUsingProcessBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

internal abstract class ReportTask : DefaultTask() {

    @TaskAction
    fun check() {
        val metricsDir =
            File(project.rootProject.layout.buildDirectory.get().asFile, "compose_metrics")
        if (!metricsDir.exists()) {
            println("There is No Compose Metrics")
            return
        }

        val txtFiles = metricsDir.listFiles { file -> file.extension == "txt" }
        if (txtFiles.isNullOrEmpty()) {
            println("No classes.txt files found in compose_metrics")
            return
        }

        val mergedFile = File(metricsDir, "compose_stability_report.txt")

        if (mergedFile.exists()) {
            mergedFile.writeText("")
        } else {
            mergedFile.createNewFile()
        }

        txtFiles.filter { it.name.contains("classes") }.forEach { file ->
            mergedFile.appendText("<<<<${file.name}>>>>" + "\n")

            val classData = file.readText()

            mergedFile.appendText(classData + "\n")
        }

        mergedFile.let { file ->
            val classData = file.readText()
            val htmlContent = generateHtml(file.name, classData)
            val htmlFile = File(metricsDir, "${file.nameWithoutExtension}.html")
            htmlFile.writeText(htmlContent)
            println("Generated: ${htmlFile.absolutePath}")

            openBrowserUsingProcessBuilder(htmlFile)
        }
    }
}