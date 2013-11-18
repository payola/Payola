package cz.payola.web.client.views.bootstrap

import cz.payola.web.client.views.elements.lists.ListItem
import cz.payola.web.client.View
import cz.payola.web.client.events.SimpleUnitEvent

class RemovableListItem(incon: String, subViews: Seq[View] = Nil, cssClass: String = "") extends ListItem(subViews, cssClass) {

    private val button = new Anchor(List(new Icon(icon)))

    button.mouseClicked += { e =>
        changed.triggerDirectly(this)
        false
    }

    def createSubViews(): Seq[View] = {
        subViews ++ remove
    }

    val changed = new SimpleUnitEvent[this.type]
}
