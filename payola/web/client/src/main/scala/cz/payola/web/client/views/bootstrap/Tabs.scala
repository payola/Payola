package cz.payola.web.client.views.bootstrap

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements._
import cz.payola.web.client.View
import cz.payola.web.client.views.elements.lists._

class Tabs(tabs: Seq[(String, View)], tabsId: String) extends ComposedView
{
    private var activeTabIndex = 0

    var i = 1

    private val tabItems = tabs.map { tuple =>
        val anchor = new Anchor(List(new Text(tuple._1)), "#tab-" + i)
        anchor.setAttribute("data-toggle", "tab")
        i += 1
        new ListItem(List(anchor))
    }

    var j = 1

    private val tabViews = tabs.map { t =>
        val div = new Div(List(t._2), "tab-pane")
        div.id = "tab-" + j
        j += 1
        div
    }

    private val tabContent = new Div(tabViews, "tab-content")

    private val list = new UnorderedList(tabItems, "nav nav-tabs")

    list.id = tabsId

    private val tabbable = new Div(List(list, tabContent), "tabbable")

    switchTab(0)

    def createSubViews = List(tabbable)

    def hideTab(index: Int) {
        tabItems(index).hide()
    }

    def showTab(index: Int) {
        val item = tabItems(index)
        item.show()
    }

    def switchTab(index: Int) {
        tabViews(activeTabIndex).removeCssClass("active")
        tabViews(index).addCssClass("active")
        tabItems(activeTabIndex).removeCssClass("active")
        tabItems(index).addCssClass("active")
        activeTabIndex = index
    }
}
