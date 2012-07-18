package cz.payola.web.client.views

import s2js.adapters.js.dom
import cz.payola.web.client.View

trait ComposedView extends View
{
    private var _subViews: Option[Seq[View]] = None

    private var parentElement: Option[dom.Element] = None

    def createSubViews: Seq[View]

    def subViews: Seq[View] = {
        if (_subViews.isEmpty) {
            _subViews = Some(createSubViews)
        }
        _subViews.get
    }

    def render(parent: dom.Element) {
        parentElement = Some(parent)
        subViews.foreach(_.render(parent))
    }

    def destroy() {
        subViews.foreach(_.destroy())
        parentElement = None
    }

    def block() {
        parentElement.foreach(View.block(_))
    }

    def unblock() {
        parentElement.foreach(View.unblock(_))
    }
}
