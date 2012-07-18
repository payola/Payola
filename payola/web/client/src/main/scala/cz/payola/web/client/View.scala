package cz.payola.web.client

import s2js.adapters.js.dom
import s2js.adapters.js.browser.document
import s2js.compiler.javascript

object View
{
    @javascript("$(target).block(self.getBlockParameters(messageView));")
    def block(target: AnyRef, messageView: Option[View] = None) { }

    @javascript("$.blockUI(self.getBlockParameters(messageView));")
    def blockPage(messageView: Option[View] = None) { }

    @javascript("$(target).unblock({ fadeOut: 0 });")
    def unblock(target: AnyRef) { }

    @javascript("$.unblockUI({ fadeOut: 0 });")
    def unblockPage() { }

    @javascript("""
        return {
            message: self.messageViewToHtml(messageView),
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
    private def getBlockParameters(messageView: Option[View]): String = ""

    private def messageViewToHtml(messageView: Option[View]): String = {
        messageView.map { m =>
            val wrapper = document.createElement[dom.Div]("div")
            m.render(wrapper)
            wrapper.innerHTML
        }.getOrElse(null)
    }
}

trait View
{
    def render(parent: dom.Element)

    def destroy()

    def block()

    def unblock()
}
