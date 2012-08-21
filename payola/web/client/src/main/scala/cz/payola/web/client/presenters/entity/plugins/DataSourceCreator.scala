package cz.payola.web.client.presenters.entity.plugins

import s2js.adapters.browser._
import s2js.adapters.html
import cz.payola.common.ValidationException
import cz.payola.web.client._
import cz.payola.web.client.models.Model
import cz.payola.web.client.views.entity.plugins.DataSourceCreatorView
import cz.payola.web.shared.managers.DataSourceManager

class DataSourceCreator(val viewElement: html.Element) extends Presenter
{
    def initialize() {
        blockPage("Fetching accessible data fetcher plugins...")
        Model.accessibleDataFetchers { d =>
            val view = new DataSourceCreatorView(d)

            view.createButton.mouseClicked += { _ =>
                blockPage("Creating...")
                val name = view.name.field.value
                val description = view.description.field.value
                val plugin = view.plugin.field.value
                val parameterValues = view.parameters.map(_.field.value)
                DataSourceManager.create(name, description, plugin, parameterValues) { () =>
                    window.location.href = "/datasource/list"
                } { e =>
                    unblockPage()
                    e match {
                        case v: ValidationException => {
                            view.name.setState(v, "name")
                            view.description.setState(v, "description")
                            view.parameters.foreach(i => i.setState(v, i.field.htmlElement.name))
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
