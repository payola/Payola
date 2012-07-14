package cz.payola.web.client.views

import s2js.adapters.js.dom

trait Component
{
    def render(parent: dom.Element)

    def destroy()
}
