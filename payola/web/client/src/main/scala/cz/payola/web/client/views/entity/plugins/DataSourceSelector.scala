package cz.payola.web.client.views.entity.plugins

import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._
import cz.payola.common.entities.plugins.DataSource
import cz.payola.web.client.events.SimpleUnitEvent
import cz.payola.web.client.views.elements.lists._

class DataSourceSelector(title: String, dataSources: Seq[DataSource]) extends Modal(title, Nil, None)
{
    val dataSourceSelected = new SimpleUnitEvent[DataSource]

    val dataSourceListItems = dataSources.map {
        d =>
            val anchor = new Anchor(List(new Text(d.name)))
            anchor.mouseClicked += {
                e =>
                    dataSourceSelected.triggerDirectly(d)
                    false
            }
            new ListItem(List(anchor))
    }

    override val body = List(new UnorderedList(dataSourceListItems))
}
