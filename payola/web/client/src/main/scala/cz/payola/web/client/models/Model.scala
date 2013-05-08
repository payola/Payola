package cz.payola.web.client.models

import cz.payola.common.entities._
import cz.payola.common.entities.plugins.DataSource
import cz.payola.web.shared.managers._
import cz.payola.common.entities.settings._
import cz.payola.web.client.events.SimpleUnitEvent
import scala.collection.mutable
import scala.Some

/**
 * Model object which contains calls to the remote objects on server. Most of the calls are provided with caching
 * capabilities to prevent triggering the same call on the same page twice in the page lifetime (which is not always
 * wanted).
 */
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

    def getOwnedDataSourceByID(dataSourceID: String)(successCallback: DataSource => Unit)(errorCallback: Throwable => Unit) {
        DataSourceManager.getOwnedDataSourceByID(dataSourceID) { ds => successCallback(ds) } { t => errorCallback(t)}
    }

    def getOwnedAnalysisById(analysisId: String)(successCallback: Analysis => Unit)(errorCallback: Throwable => Unit) {

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

    def forceOntologyCustomizationsByOwnershipUpdate(successCallback: OntologyCustomizationsByOwnership => Unit)
        (errorCallback: Throwable => Unit) {

        _ontologyCustomizationsAreLoaded = false
        ontologyCustomizationsByOwnership(successCallback)(errorCallback)
    }

    def changeOntologyCustomizationName(customization: OntologyCustomization, newName: String)
        (successCallback: () => Unit)
        (errorCallback: Throwable => Unit) {

        OntologyCustomizationManager.rename(customization.id, newName) { () =>
            customization.name = newName
            ontologyCustomizationsChanged.triggerDirectly(this)
            successCallback()
        }(errorCallback)
    }

    def createUserCustomization(name: String)
        (successCallback: OntologyCustomization => Unit)
        (errorCallback: Throwable => Unit) {

        fetchOntologyCustomizations { () =>
            _ownedOntologyCustomizations.foreach { ownedCustomizations =>
                OntologyCustomizationManager.create(name, "") { newCustomization =>
                    ownedCustomizations += newCustomization
                    ontologyCustomizationsChanged.triggerDirectly(this)
                    successCallback(newCustomization)
                }(errorCallback)
            }
        }(errorCallback)
    }

    def createOntologyCustomization(name: String, ontologyURLs: String)
        (successCallback: OntologyCustomization => Unit)
        (errorCallback: Throwable => Unit) {

        fetchOntologyCustomizations { () =>
            _ownedOntologyCustomizations.foreach { ownedCustomizations =>
                OntologyCustomizationManager.create(name, ontologyURLs) { newCustomization =>
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
            _ownedOntologyCustomizations.foreach(_ -= ontologyCustomization)
            ontologyCustomizationsChanged.triggerDirectly(this)
            successCallback()
        }(errorCallback)
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
