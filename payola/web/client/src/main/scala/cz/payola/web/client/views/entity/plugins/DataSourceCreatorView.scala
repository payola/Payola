package cz.payola.web.client.views.entity.plugins

import cz.payola.web.client.views.elements._
import cz.payola.common.entities.Plugin

class DataSourceCreatorView(plugins: Seq[Plugin], val createButton: Button = new Button(new Text("Create"))) extends DataSourceDetailView(plugins, true, List(createButton))
{

}
