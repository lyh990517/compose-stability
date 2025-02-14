package com.yunho.plugin

import java.io.File

object HtmlUtil {
    fun generateHtml(title: String, classData: String): String {
        val sections = classData.split(Regex("<<<<(.*?)>>>>"))
        val matches =
            Regex("<<<<(.*?)>>>>").findAll(classData).map { it.groupValues[1] }.toList()
        println(matches)
        val rows = mutableListOf<String>()

        sections.forEachIndexed { index, section ->
            if (section.isNotBlank()) {
                val sectionTitle = matches[index - 1]

                rows.add("<tr><td colspan='4' class='section-title'>$sectionTitle</td></tr>")

                val classBlocks =
                    Regex("(stable|unstable) class .*?\\{.*?\\}", RegexOption.DOT_MATCHES_ALL)
                        .findAll(section).map { it.value.trim() }.toList()

                classBlocks.forEach { block ->
                    val lines = block.lines().map { it.trim() }

                    val classMatch = Regex("(stable|unstable) class (\\w+)").find(lines[0])
                    val stability = classMatch?.groupValues?.get(1) ?: "Unknown"
                    val className = classMatch?.groupValues?.get(2) ?: "Unknown"

                    val runtimeStabilityMatch = Regex("<runtime stability> = (\\w+)").find(block)
                    val runtimeStability = runtimeStabilityMatch?.groupValues?.get(1) ?: "Unknown"

                    val stableMembers =
                        lines.drop(1).filter { it.matches(Regex("stable (var|val) .*")) }
                    val unstableMembers =
                        lines.drop(1).filter { it.matches(Regex("unstable (var|val) .*")) }

                    val stableMembersHtml =
                        if (stableMembers.isEmpty()) "<p>없음</p>" else stableMembers.joinToString("\n") { "<p>$it</p>" }

                    val unstableMembersHtml =
                        if (unstableMembers.isEmpty()) "<p>없음</p>" else unstableMembers.joinToString(
                            "\n"
                        ) { "<p>$it</p>" }

                    rows.add(
                        """
                    <tr class="class-row">
                        <td><b>$className</b></td>
                        <td class="stability $stability">$stability</td>
                        <td class="members">
                            <div class="members-section">
                                <b>Stable</b>
                                <div class="stable-column">
                                    $stableMembersHtml
                                </div>
                            </div>
                            <div class="members-section">
                                <b>Unstable</b>
                                <div class="unstable-column">
                                    $unstableMembersHtml
                                </div>
                            </div>
                        </td>
                        <td class="runtime-stability">$runtimeStability</td>
                    </tr>
                """.trimIndent()
                    )
                }
            }
        }

        return """
        <!DOCTYPE html>
        <html lang="ko">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>$title</title>
            <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
            <style>
                body {
                    font-family: 'Poppins', sans-serif;
                    background-color: #f7f7f7;
                    color: #333;
                    margin: 0;
                    padding: 0;
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    height: 100vh;
                    overflow-y: auto;
                }

                h2 {
                    text-align: center;
                    color: #00796b;
                    margin-bottom: 40px;
                    font-size: 36px;
                    letter-spacing: 1px;
                    position: sticky;
                    top: 0;
                    background-color: #fff;
                    padding: 10px;
                    z-index: 1;
                    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                }

                table {
                    width: 100vw;  /* Full width of the screen */
                    max-width: 100%;  /* Ensures the table doesn't exceed screen width */
                    border-collapse: collapse;
                    margin: 20px 0;
                    font-size: 16px;
                    text-align: left;
                    border-radius: 8px;
                    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
                    overflow: hidden;
                    background-color: #fff;
                }

                th, td {
                    padding: 16px;
                    border: 1px solid #e0e0e0;
                    text-align: center;
                    vertical-align: middle;
                }

                th {
                    background-color: #00796b;
                    color: #fff;
                    font-size: 18px;
                }

                td {
                    background-color: #fafafa;
                }

                .class-row:hover {
                    background-color: #e0f2f1;
                }

                .stability.stable {
                    background-color: #a5d6a7;
                    color: #388e3c;
                }

                .stability.unstable {
                    background-color: #ef9a9a;
                    color: #d32f2f;
                }

                .members-section {
                    margin-bottom: 20px;
                    text-align: start;
                }

                .members-section b {
                    font-size: 18px;
                    margin-bottom: 8px;
                    color: #00796b;
                    font-weight: 600;
                }

                .stable-column, .unstable-column {
                    width: 100%;
                    border: 1px solid #ddd;
                    border-radius: 5px;
                    padding: 10px;
                    background-color: #f1f1f1;
                    text-align: start;
                }

                .stable-column:hover, .unstable-column:hover {
                    background-color: #e0f2f1;
                }

                .stable-column p, .unstable-column p {
                    margin: 4px 0;
                    color: #333;
                }

                .runtime-stability {
                    font-weight: bold;
                    font-size: 18px;
                }

                .section-title {
                    text-align: center;
                    font-size: 24px;
                    font-weight: bold;
                    color: #00796b;
                    background-color: #f1f1f1;
                    padding: 10px;
                    margin: 20px 0;
                    width: 100%;
                }
            </style>
        </head>
        <body>
            <div>
                <h2>$title</h2>
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
            </div>
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