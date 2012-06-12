package cz.payola.web.client.presenters

import s2js.adapters.js.browser.document
import cz.payola.web.client.mvvm.element.extensions.Bootstrap.Icon
import cz.payola.web.client.mvvm.element._
import cz.payola.web.client.presenters.components.DataSourceDialog

class AnalysisBuilder(menuHolder: String)
{
    val icon = new Icon(Icon.hdd)
    val addDataSourceLink = new Anchor(List(icon))
    val addDataSourceListItem = new ListItem(List(addDataSourceLink))
    val dataSourceModal = new DataSourceDialog

    addDataSourceListItem.render(document.getElementById(menuHolder))

    addDataSourceLink.clicked += {event =>
        dataSourceModal.render()
        false
    }


}
