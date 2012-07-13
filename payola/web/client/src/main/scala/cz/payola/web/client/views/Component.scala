package cz.payola.web.client.views

import s2js.adapters.js.dom

abstract class Component
{
    def render(parent: dom.Node)

    def destroy()
}
