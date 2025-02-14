package com.yunho.plugin

import java.io.File

object HtmlUtil {
    fun generateHtml(classData: String): String {
        val classBlocks =
            Regex("(stable|unstable) class .*?\\{.*?\\}", RegexOption.DOT_MATCHES_ALL).findAll(
                classData
            ).map { it.value.trim() }.toList()
        val rows = mutableListOf<String>()

        for (block in classBlocks) {
            val lines = block.lines().map { it.trim() }

            val classMatch = Regex("(stable|unstable) class (\\w+)").find(lines[0])
            val stability = classMatch?.groupValues?.get(1) ?: "Unknown"
            val className = classMatch?.groupValues?.get(2) ?: "Unknown"

            val runtimeStabilityMatch = Regex("<runtime stability> = (\\w+)").find(block)
            val runtimeStability = runtimeStabilityMatch?.groupValues?.get(1) ?: "Unknown"

            val stableMembers = lines.drop(1).filter { it.matches(Regex("stable (var|val) .*")) }
            val unstableMembers =
                lines.drop(1).filter { it.matches(Regex("unstable (var|val) .*")) }

            val stableMembersHtml =
                if (stableMembers.isEmpty()) "<tr><td>없음</td></tr>" else stableMembers.joinToString(
                    "\n"
                ) { "<tr><td>$it</td></tr>" }

            val unstableMembersHtml =
                if (unstableMembers.isEmpty()) "<tr><td>없음</td></tr>" else unstableMembers.joinToString(
                    "\n"
                ) { "<tr><td>$it</td></tr>" }

            rows.add(
                """
            <tr class="class-row">
                <td><b>$className</b></td>
                <td class="stability $stability">$stability</td>
                <td class="members">
                    <div class="members-section">
                        <b>Stable:</b>
                        <table class="stable-table">
                            <thead><tr><th>멤버</th></tr></thead>
                            <tbody>
                                $stableMembersHtml
                            </tbody>
                        </table>
                    </div>
                    <div class="members-section">
                        <b>Unstable:</b>
                        <table class="unstable-table">
                            <thead><tr><th>멤버</th></tr></thead>
                            <tbody>
                                $unstableMembersHtml
                            </tbody>
                        </table>
                    </div>
                </td>
                <td class="runtime-stability">$runtimeStability</td>
            </tr>
        """.trimIndent()
            )
        }

        return """
    <!DOCTYPE html>
    <html lang="ko">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Class Stability Report</title>
        <style>
            body {
                font-family: 'Arial', sans-serif;
                background-color: #f4f4f4;
                color: #333;
                margin: 20px;
                padding: 20px;
            }

            h2 {
                text-align: center;
                color: #4CAF50;
                margin-bottom: 40px;
                font-size: 36px;
            }

            table {
                width: 100%;
                border-collapse: collapse;
                margin: 20px 0;
                font-size: 16px;
                text-align: left;
                border-radius: 10px;
                overflow: hidden;
            }

            th, td {
                padding: 16px;
                border: 1px solid #ddd;
                text-align: center;
                vertical-align: middle;
            }

            th {
                background-color: #333;
                color: #fff;
                font-size: 18px;
            }

            td {
                background-color: #fff;
            }

            .class-row {
                transition: background-color 0.3s;
            }

            .class-row:hover {
                background-color: #f1f1f1;
            }

            .stability.stable {
                background-color: #d4edda;
                color: #155724;
            }

            .stability.unstable {
                background-color: #f8d7da;
                color: #721c24;
            }

            .members-section {
                margin-bottom: 20px;
            }

            .members-section b {
                font-size: 18px;
                display: block;
                margin-bottom: 8px;
                color: #333;
            }

            .stable-table, .unstable-table {
                width: 100%;
                border: 1px solid #ddd;
                border-radius: 5px;
                margin-bottom: 10px;
            }

            .stable-table th, .unstable-table th {
                background-color: #d4edda;
                font-size: 16px;
            }

            .stable-table tbody, .unstable-table tbody {
                background-color: #f9f9f9;
            }

            .runtime-stability {
                font-weight: bold;
                font-size: 18px;
            }

            /* Scrollable Table for members */
            .stable-table tbody, .unstable-table tbody {
                max-height: 200px;
                overflow-y: auto;
                display: block;
            }

        </style>
    </head>
    <body>
        <h2>Class Stability Report</h2>
        <table>
            <thead>
                <tr>
                    <th>클래스</th>
                    <th>안정성</th>
                    <th>멤버</th>
                    <th>런타임 안정성</th>
                </tr>
            </thead>
            <tbody>
                ${rows.joinToString("\n")}
            </tbody>
        </table>
    </body>
    </html>
    """.trimIndent()
    }

    fun openBrowserUsingProcessBuilder(file: File) {
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
            } ?: println("not supported os")
        } catch (e: Exception) {
            println("error: ${e.message}")
        }
    }
}