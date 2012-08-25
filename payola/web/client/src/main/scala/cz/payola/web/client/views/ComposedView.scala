package cz.payola.web.client.views

import s2js.adapters.browser._
import s2js.adapters.html
import cz.payola.web.client.View
import cz.payola.web.client.views.elements.Text
import s2js._

/**
 * Representation of structured View.
 * This class allows to put more View objects together and create a structured HTML document.
 */
trait ComposedView extends View
{
    /**
     * View objects that have this View as a parent.
     */
    private var _subViews: Option[Seq[View]] = None

    /**
     * Parent of this View.
     */
    private var parentElement: Option[html.Element] = None

    /**
     * Construction of child View objects that are rendered with this View object.
     * @return Child View objects
     */
    def createSubViews: Seq[View]

    /**
     * Getter of the list of child View objects
     * @return
     */
    def subViews: Seq[View] = {
        if (_subViews.isEmpty) {
            _subViews = Some(createSubViews)
        }
        _subViews.get
    }

    def blockHtmlElement = {
        subViews.headOption.map(_.blockHtmlElement).getOrElse(document.createElement[adapters.html.Element]("div"))
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
