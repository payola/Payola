package cz.payola.web.client.models

import cz.payola.common.entities._
import cz.payola.common.entities.plugins.DataSource
import cz.payola.web.shared.managers._
import cz.payola.common.entities.settings.OntologyCustomization
import cz.payola.web.client.events.SimpleUnitEvent
import scala.collection.mutable

object Model
{
    val ontologyCustomizationsChanged = new SimpleUnitEvent[this.type]

    private var _accessibleDataSources: Option[Seq[DataSource]] = None

    private var _accessiblePlugins: Option[Seq[Plugin]] = None

    private var _accessibleDataFetchers: Option[Seq[Plugin]] = None

    private var _ownedOntologyCustomizations: Option[mutable.ListBuffer[OntologyCustomization]] = None

    private var _othersOntologyCustomizations: Seq[OntologyCustomization] = Nil

    private var _ontologyCustomizationsAreLoaded = false

    def accessibleDataSources(successCallback: Seq[DataSource] => Unit)(errorCallback: Throwable => Unit) {
        if (_accessibleDataSources.isEmpty) {
            DataSourceManager.getAccessible() { d =>
                _accessibleDataSources = Some(d)
                successCallback(d)
            }(errorCallback)
        } else {
            successCallback(_accessibleDataSources.get)
        }
    }

    def accessiblePlugins(successCallback: Seq[Plugin] => Unit)(errorCallback: Throwable => Unit) {
        if (_accessiblePlugins.isEmpty) {
            PluginManager.getAccessible() { p =>
                _accessiblePlugins = Some(p)
                successCallback(p)
            }(errorCallback)
        } else {
            successCallback(_accessiblePlugins.get)
        }
    }

    def accessibleDataFetchers(successCallback: Seq[Plugin] => Unit)(errorCallback: Throwable => Unit) {
        if (_accessibleDataFetchers.isEmpty) {
            PluginManager.getAccessibleDataFetchers() { p =>
                _accessibleDataFetchers = Some(p)
                successCallback(p)
            }(errorCallback)
        } else {
            successCallback(_accessibleDataFetchers.get)
        }
    }

    def ontologyCustomizationsByOwnership(successCallback: OntologyCustomizationsByOwnership => Unit)
        (errorCallback: Throwable => Unit) {

        fetchOntologyCustomizations { () =>
            successCallback(new OntologyCustomizationsByOwnership(
                _ownedOntologyCustomizations,
                _othersOntologyCustomizations
            ))
        }(errorCallback)
    }

    def changeOntologyCustomizationName(customization: OntologyCustomization, newName: String)
        (successCallback: () => Unit)
        (errorCallback: Throwable => Unit) {

        OntologyCustomizationManager.rename(customization.id, newName) { () =>
            customization.name = newName
            ontologyCustomizationsChanged.triggerDirectly(this)
            successCallback()
        } (errorCallback)
    }

    def createOntologyCustomization(name: String, ontologyURL: String)
        (successCallback: OntologyCustomization => Unit)
        (errorCallback: Throwable => Unit) {

        fetchOntologyCustomizations { () =>
            _ownedOntologyCustomizations.foreach { ownedCustomizations =>
                OntologyCustomizationManager.create(name, ontologyURL) { newCustomization =>
                    ownedCustomizations += newCustomization
                    ontologyCustomizationsChanged.triggerDirectly(this)
                    successCallback(newCustomization)
                }(errorCallback)
            }
        }(errorCallback)
    }

    def deleteOntologyCustomization(ontologyCustomization: OntologyCustomization)
        (successCallback: () => Unit)
        (errorCallback: Throwable => Unit) {

        OntologyCustomizationManager.delete(ontologyCustomization.id) { () =>
            _ownedOntologyCustomizations.foreach { ownedCustomizations =>
                ownedCustomizations -= ontologyCustomization
            }

            ontologyCustomizationsChanged.triggerDirectly(this)
            successCallback()
        } (errorCallback)

    }

    private def fetchOntologyCustomizations(successCallback: () => Unit)(errorCallback: Throwable => Unit) {
        if (!_ontologyCustomizationsAreLoaded) {
            OntologyCustomizationManager.getByOwnership() { customizationsByOwnership =>
                customizationsByOwnership.ownedCustomizations.foreach { owned =>
                    val c = mutable.ListBuffer.empty[OntologyCustomization]
                    owned.foreach(c += _)
                    _ownedOntologyCustomizations = Some(c)
                }
                _othersOntologyCustomizations = customizationsByOwnership.othersCustomizations
                _ontologyCustomizationsAreLoaded = true
                successCallback()
            }(errorCallback)
        } else {
            successCallback()
        }
    }
}
