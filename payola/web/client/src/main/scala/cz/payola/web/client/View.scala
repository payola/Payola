package cz.payola.web.client

import s2js.adapters.js.dom

trait View
{
    def render(parent: dom.Element)

    def destroy()
}
