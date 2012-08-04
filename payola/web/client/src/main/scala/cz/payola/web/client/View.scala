package cz.payola.web.client

import s2js.compiler.javascript
import s2js.adapters.js.html
import s2js.adapters.html.Element
import s2js._

object View
{
    @javascript("$.blockUI(self.getBlockParameters(message));")
    def blockPage(message: String = "") { }

    @javascript("$.unblockUI({ fadeOut: 0 });")
    def unblockPage() { }

    @javascript("$(target).block(self.getBlockParameters(message));")
    private def block(target: html.Element, message: String) { }

    @javascript("$(target).unblock({ fadeOut: 0 });")
    private def unblock(target: adapters.html.Element) { }

    @javascript("""
        return {
            message: self.messageToHtml(message),
            fadeIn: 100,
            css: {
                padding: 20,
                border: '0'
            },
            overlayCSS: {
                backgroundColor: '#FFF',
                opacity: 0.6
            }
        };""")
    private def getBlockParameters(message: String): String = ""

    private def messageToHtml(message: String): String = {
        if (message == null || message == "") {
            null
        } else {
            """
                <div class="row-fluid">
                    <h3 style="padding: 20px;">""" + message + """</h3>
                </div>
                <div class="row-fluid">
                    <div class="progress progress-striped active">
                        <div class="bar" style="width: 100%"></div>
                    </div>
                </div>
            """
        }
    }
}

trait View
{
    /**
     * Constructs this View's HTML representation and appends it to the parent HTML element.
     * @param parent element to which this View will be appended
     */
    def render(parent: adapters.html.Element)

    /**
     * Destroys inner variables and removes this View's HTML representation from its parent element.
     */
    def destroy()

    def blockHtmlElement: adapters.html.Element

    def block(message: String = "") {
        View.block(blockHtmlElement, message)
    }

    def unblock() {
        View.unblock(blockHtmlElement)
    }
}
