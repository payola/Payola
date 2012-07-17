package cz.payola.web.client

import s2js.adapters.js.dom
import s2js.compiler.javascript

trait View
{
    def render(parent: dom.Element)

    def destroy()

    def block()

    def unblock()

    @javascript("""
        $(domElement).block({
            message: null,
            fadeIn: 100,
            overlayCSS: {
                backgroundColor: '#FFF',
                opacity: 0.6
            }
        });""")
    protected def blockElement(domElement: dom.Element) { }

    @javascript("$(domElement).unblock({ fadeOut: 0 });")
    def unblockElement(domElement: dom.Element) { }
}
