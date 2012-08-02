package cz.payola.web.client.views

import s2js.adapters.js.html
import s2js.adapters.js.browser.document
import cz.payola.web.client.View
import cz.payola.web.client.views.elements.Text

trait ComposedView extends View
{
    private var _subViews: Option[Seq[View]] = None

    private var parentElement: Option[html.Element] = None

    def createSubViews: Seq[View]

    def subViews: Seq[View] = {
        if (_subViews.isEmpty) {
            _subViews = Some(createSubViews)
        }
        _subViews.get
    }

    def blockHtmlElement = {
        subViews.headOption.map(_.blockHtmlElement).getOrElse(document.createElement[html.Element]("div"))
    }

    def render(parent: html.Element) {
        parentElement = Some(parent)
        subViews.foreach { v =>
            new Text(" ").render(parent)
            v.render(parent)
        }
    }

    def destroy() {
        subViews.foreach(_.destroy())
        parentElement = None
    }
}
