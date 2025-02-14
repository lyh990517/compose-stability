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
            <tr>
                <td><b>$className</b></td>
                <td class="$stability">$stability</td>
                <td>
                    <b>Stable:</b>
                    <table border="1">
                        <thead><tr><th>멤버</th></tr></thead>
                        <tbody>
                            $stableMembersHtml
                        </tbody>
                    </table>
                    <b>Unstable:</b>
                    <table border="1">
                        <thead><tr><th>멤버</th></tr></thead>
                        <tbody>
                            $unstableMembersHtml
                        </tbody>
                    </table>
                </td>
                <td>$runtimeStability</td>
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
            table {
                width: 100%;
                border-collapse: collapse;
                margin: 20px 0;
                font-size: 18px;
                text-align: left;
            }
            th, td {
                padding: 12px;
                border: 1px solid #ddd;
            }
            th {
                background-color: #f4f4f4;
            }
            .stable { background-color: #d4edda; } /* 연한 초록색 */
            .unstable { background-color: #f8d7da; } /* 연한 빨간색 */
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