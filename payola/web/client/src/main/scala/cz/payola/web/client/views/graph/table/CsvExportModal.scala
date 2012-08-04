package cz.payola.web.client.views.graph.table

import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements._
import s2js.compiler.javascript

class CsvExportModal(val csv: String) extends Modal("CSV Export", Nil, Some("OK"), None, false)
{
    private val csvPreFormatted = new PreFormatted(csv, "pre-scrollable")

    override val body = List(
        new Paragraph(List(new Text("Click on the following box to select everything."))),
        csvPreFormatted
    )

    csvPreFormatted.mouseClicked += { e =>
        selectAll()
        false
    }

    @javascript( """
        if (document.selection) {
            var range = document.body.createTextRange();
            range.moveToElementText(self.csvPreFormatted.htmlElement);
            range.select();
        } else if (window.getSelection) {
            var range = document.createRange();
            range.selectNode(self.csvPreFormatted.htmlElement);
            window.getSelection().addRange(range);
        }
                 """)
    private def selectAll() {}
}
