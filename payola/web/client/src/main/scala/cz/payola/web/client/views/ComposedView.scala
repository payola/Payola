package cz.payola.web.client.views

import s2js.adapters.js.dom
import cz.payola.web.client.View

trait ComposedView extends View
{
    private var _subViews: Option[Seq[View]] = None

    def createSubViews: Seq[View]

    def subViews: Seq[View] = {
        if (_subViews.isEmpty) {
            _subViews = Some(createSubViews)
        }
        _subViews.get
    }

    def render(parent: dom.Element) {
        subViews.foreach(_.render(parent))
    }

    def destroy() {
        subViews.foreach(_.destroy())
    }
}
