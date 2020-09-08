package io.github.detekt.report.xml

import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.WriterConfig
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport

/**
 * Contains rule violations in an XML format. The report follows the structure of a Checkstyle report.
 * See: https://detekt.github.io/detekt/configurations.html#output-reports
 */
class SarifOutputReport : OutputReport() {

    override val ending = "json"

    override val name = "SARIF"

    override fun render(detektion: Detektion): String {
        val sarif = JsonObject()

        val results = JsonArray()
        detektion.findings.flatMap { it.value }.forEach { finding ->
            results.add(JsonObject().apply {
                set("ruleId", finding.id)
                set("message", JsonObject().set("text", finding.messageOrDescription()))

                val location = JsonObject()
                    .set(
                        "physicalLocation", JsonObject()
                            .set(
                                "artifactLocation", JsonObject()
                                    .set("uri", "file://${finding.location.file}")
                                    .set("index", 0)
                            )
                    )
                    .set(
                        "region", JsonObject()
                            .set("startLine", finding.location.source.line)
                            .set("startColumn", finding.location.source.column)
                    )
                set("locations", JsonArray().add(location))
            })
        }

        sarif.set("version", "2.1.0")
        sarif.set("runs", JsonArray().also { runs ->
            val run = JsonObject()
            run.set("tool", JsonObject().also { tool ->
                tool.set("driver", JsonObject().also { driver ->
                    driver.set("name", "Detekt")
                })
            })
            run.set("results", results)

            runs.add(run)
        })

        return sarif.toString(WriterConfig.PRETTY_PRINT)
/*
        val smells = detektion.findings.flatMap { it.value }

        val lines = ArrayList<String>()
        lines += "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
        lines += "<checkstyle version=\"4.3\">"

        smells.groupBy { it.location.file }.forEach { (fileName, findings) ->
            lines += "<file name=\"${fileName.toXmlString()}\">"
            findings.forEach {
                lines += arrayOf(
                        "\t<error line=\"${it.location.source.line.toXmlString()}\"",
                        "column=\"${it.location.source.column.toXmlString()}\"",
                        "severity=\"${it.severityLabel.toXmlString()}\"",
                        "message=\"${it.messageOrDescription().toXmlString()}\"",
                        "source=\"${"detekt.${it.id.toXmlString()}"}\" />"
                ).joinToString(separator = " ")
            }
            lines += "</file>"
        }

        lines += "</checkstyle>"
        return lines.joinToString(separator = "\n")
*/
    }

/*
    private val Finding.severityLabel: String
        get() = when (issue.severity) {
            Severity.CodeSmell,
            Severity.Style,
            Severity.Warning,
            Severity.Maintainability,
            Severity.Performance -> "warning"
            Severity.Defect -> "error"
            Severity.Minor -> "info"
            Severity.Security -> "fatal"
        }
*/

/*
    private fun Any.toXmlString() = XmlEscape.escapeXml(toString().trim())
*/
}
