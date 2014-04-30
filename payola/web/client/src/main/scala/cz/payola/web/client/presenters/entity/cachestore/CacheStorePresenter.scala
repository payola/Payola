package cz.payola.web.client.presenters.entity.cachestore

import cz.payola.common.entities.EmbeddingDescription
import cz.payola.domain.entities
import cz.payola.web.client.Presenter
import cz.payola.web.client.models.Model
import cz.payola.web.client.views.bootstrap.InputControl
import cz.payola.web.client.views.elements.form.fields._
import cz.payola.web.shared.Payola
import cz.payola.web.shared.managers.EmbeddingDescriptionManager
import s2js.adapters.html

class CacheStorePresenter(val viewElement: html.Element, embeddedId: String, defaultPlugin: String) extends Presenter
{
    def initialize() {

        val plugins = List(("", "")) ++ cz.payola.web.client.views.graph.AvailablePluginViews.getPlugins(None).map{ plugin =>
            ((plugin.name, plugin.getClass.getName.replaceAll(".", "_")))
        }

        val dropDown = new Select("", "", "", plugins.map(p => new SelectOption(p._1, p._2)), "form-control")
        dropDown.changed += { e =>
            setViewPlugin(embeddedId, e.target.value)

        }
        val dropDownControl = new InputControl("", dropDown, None, None)

        dropDownControl.render(viewElement)
        dropDownControl.field.updateValue(defaultPlugin)
    }

    private def setViewPlugin(id: String, visualPlugin: String) {
        EmbeddingDescriptionManager.setViewPlugin(id, visualPlugin) { result => } { error => }
    }
}
