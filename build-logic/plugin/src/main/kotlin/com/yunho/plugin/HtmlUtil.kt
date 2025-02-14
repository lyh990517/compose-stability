package com.yunho.plugin

import java.io.File

object HtmlUtil {
    fun generateHtml(classData: String): String {
        val classLines = classData.lines().filter { it.isNotBlank() }
        val nodes = mutableListOf<String>()
        val links = mutableListOf<String>()

        for (line in classLines) {
            val match = Regex("(stable|unstable) class (\\w+)").find(line)
            if (match != null) {
                val (stability, className) = match.destructured
                nodes.add("{ id: \"$className\", stability: \"$stability\" }")
            }
        }

        return """
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Class Stability Graph</title>
        <script src="https://d3js.org/d3.v7.min.js"></script>
        <style>
            .node circle {
                stroke: #fff;
                stroke-width: 2px;
            }
            .stable { fill: green; }
            .unstable { fill: red; }
            text { font-size: 12px; fill: black; }
        </style>
    </head>
    <body>
        <svg width="800" height="600"></svg>
        <script>
            const data = {
                nodes: [${nodes.joinToString(",")}],
                links: [${links.joinToString(",")}]
            };

            const width = 800, height = 600;
            const svg = d3.select("svg");
            
            const simulation = d3.forceSimulation(data.nodes)
                .force("link", d3.forceLink(data.links).id(d => d.id).distance(100))
                .force("charge", d3.forceManyBody().strength(-200))
                .force("center", d3.forceCenter(width / 2, height / 2));

            const link = svg.selectAll(".link")
                .data(data.links)
                .enter().append("line")
                .attr("stroke", d => d.stability === "stable" ? "green" : "red")
                .attr("stroke-width", 2);

            const node = svg.selectAll(".node")
                .data(data.nodes)
                .enter().append("g")
                .attr("class", "node");

            node.append("circle")
                .attr("r", 20)
                .attr("class", d => d.stability);

            node.append("text")
                .attr("dy", 5)
                .attr("text-anchor", "middle")
                .text(d => d.id);

            simulation.on("tick", () => {
                link
                    .attr("x1", d => d.source.x)
                    .attr("y1", d => d.source.y)
                    .attr("x2", d => d.target.x)
                    .attr("y2", d => d.target.y);

                node
                    .attr("transform", d => "translate(" + d.x + "," + d.y + ")");
            });
        </script>
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