package cz.payola.web.client.presenters.entity.plugins

import cz.payola.web.client.Presenter
import s2js.adapters.html
import cz.payola.web.client.models.Model
import cz.payola.web.client.views.entity.plugins._
import cz.payola.web.shared.managers.DataSourceManager
import cz.payola.common.ValidationException
import cz.payola.common.entities.plugins.DataSource
import cz.payola.common.entities.Plugin

class DataSourceEditor(val viewElement: html.Element, dataSourceID: String) extends Presenter
{
    var dataSource: DataSource = null

    def initialize() {
        blockPage("Fetching data source...")
        Model.getOwnedDataSourceByID(dataSourceID) { ds =>
            dataSource = ds
            unblockPage()
            loadAvailableDataFetchers()
        } (fatalErrorHandler(_))
    }

    private def initializeViewWithDataFetchers(fetchers: Seq[Plugin]) {
        val view = new DataSourceEditorView(fetchers)

        // Need to render the view before setting values
        // as the view re-renders parameter fields on render
        view.render(viewElement)

        view.name.field.updateValue(dataSource.name)
        view.description.field.updateValue(dataSource.description)

        view.selectDataFetcherWithID(dataSource.plugin.id)

        dataSource.parameterValues foreach { p =>
            val input = view.getInputFieldForParameterID(p.parameter.id)
            if (input.isDefined){
                input.get.field.updateValue(p.value.toString())
            }
        }

        view.nameChanged += { args =>
            val input = args.target
            input.isActive = true

            val newName = input.field.value
            DataSourceManager.changeDataSourceName(dataSource.id, newName) { () =>
                input.isActive = false
                input.setOk()
            } { t =>
                t match {
                    case e: ValidationException => input.setState(e, "name")
                    case _ => fatalErrorHandler(t)
                }
            }
        }

        view.descriptionChanged += { args =>
            val input = args.target
            input.isActive = true

            val newDescription = input.field.value
            DataSourceManager.changeDataSourceDescription(dataSource.id, newDescription) { () =>
                input.isActive = false
                input.setOk()
            } { t =>
                t match {
                    case e: ValidationException => input.setState(e, "description")
                    case _ => fatalErrorHandler(t)
                }
            }
        }

        view.parameterValueChanged += { args =>
            val input = args.target
            input.isActive = true

            val parValue = input.field.value
            val parID = args.pluginParameter.id
            DataSourceManager.changeDataSourceParameterValue(dataSource.id, parID, parValue) { () =>
                input.isActive = false
                input.setOk()
            } { t =>
                t match {
                    case e: ValidationException => input.setState(e, parID)
                    case _ => fatalErrorHandler(t)
                }
            }
        }
    }

    private def loadAvailableDataFetchers() {
        blockPage("Fetching accessible data fetcher plugins...")
        Model.accessibleDataFetchers { d =>
            initializeViewWithDataFetchers(d)
            unblockPage()
        }(fatalErrorHandler(_))
    }


}
