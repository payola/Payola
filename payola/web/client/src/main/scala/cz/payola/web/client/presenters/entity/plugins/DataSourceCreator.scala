package cz.payola.web.client.presenters.entity.plugins

import s2js.adapters.js.html
import s2js.adapters.js.browser._
import cz.payola.common.ValidationException
import cz.payola.web.client._
import cz.payola.web.client.models.Model
import cz.payola.web.client.views.entity.plugins.DataSourceCreatorView
import cz.payola.web.shared.managers.DataSourceManager
import s2js.adapters.html.Element

class DataSourceCreator(val viewElement: html.Element) extends Presenter
{
    def initialize() {
        blockPage("Fetching accessible data fetcher plugins.")
        Model.accessibleDataFetchers { d =>
            val view = new DataSourceCreatorView(d)

            view.createButton.mouseClicked += { _ =>
                blockPage("Creating.")
                val name = view.name.input.value
                val description = view.description.input.value
                val plugin = view.plugin.input.value
                val parameterValues = view.parameters.map(_.input.value)
                DataSourceManager.create(name, description, plugin, parameterValues) { () =>
                    window.location.href = "/datasource/list"
                } { e =>
                    unblockPage()
                    e match {
                        case v: ValidationException => {
                            view.name.setState(v, "name")
                            view.description.setState(v, "description")
                            view.parameters.foreach(i => i.setState(v, i.name))
                        }
                        case t => fatalErrorHandler(t)
                    }
                }
                false
            }

            view.render(viewElement)
            unblockPage()
        }(fatalErrorHandler(_))
    }

}
