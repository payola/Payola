package cz.payola.web.client.views.entity.analysis

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Icon

/**
 *
 */
class EditorToolbarView extends ComposedView
{

    val addDataSource = new Button(new Text(""),"",new Icon(Icon.hdd))
    val addPlugin = new Button(new Text(""),"",new Icon(Icon.hdd))
    val addJoin = new Button(new Text(""),"",new Icon(Icon.hdd))
    val addFork = new Button(new Text(""),"",new Icon(Icon.hdd))

    val buttons = List(addDataSource,addPlugin,addJoin,addFork)
    val toolbar = new Div(buttons,"btn-group btn-group-vertical")

    def createSubViews = List(toolbar)
}
