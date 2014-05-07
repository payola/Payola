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
    val customizationsChanged = new SimpleUnitEvent[this.type]

    private var _accessibleDataSources: Option[Seq[DataSource]] = None

    private var _accessiblePlugins: Option[Seq[Plugin]] = None

    private var _accessibleDataFetchers: Option[Seq[Plugin]] = None

    private var _ownedOntologyCustomizations: Option[mutable.ListBuffer[OntologyCustomization]] = None

    private var _ownedUserCustomizations: Option[mutable.ListBuffer[UserCustomization]] = None

    private var _othersOntologyCustomizations: Seq[OntologyCustomization] = Nil

    private var _othersUserCustomizations: Seq[UserCustomization] = Nil

    private var _ontologyCustomizationsAreLoaded = false

    private var _userCustomizationsAreLoaded = false

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

    def userCustomizationsByOwnership(successCallback: UserCustomizationsByOwnership => Unit)
        (errorCallback: Throwable => Unit) {

        fetchUserCustomizations { () =>
            successCallback(new UserCustomizationsByOwnership(
                _ownedUserCustomizations,
                _othersUserCustomizations
            ))
        }(errorCallback)
    }

    def forceCustomizationsByOwnershipUpdate
        (successCallback: (UserCustomizationsByOwnership, OntologyCustomizationsByOwnership) => Unit)
        (errorCallback: Throwable => Unit) {

        _userCustomizationsAreLoaded = false
        _ontologyCustomizationsAreLoaded = false

        fetchUserCustomizations {() => ()}(errorCallback)
        fetchOntologyCustomizations {() => ()}(errorCallback)

        successCallback(
            new UserCustomizationsByOwnership(_ownedUserCustomizations, _othersUserCustomizations),
            new OntologyCustomizationsByOwnership(_ownedOntologyCustomizations, _othersOntologyCustomizations)
        )
    }

    def changeCustomizationName(customization: DefinedCustomization, newName: String)
        (successCallback: () => Unit)
        (errorCallback: Throwable => Unit) {

        CustomizationManager.rename(customization.id, newName) { () =>
            customization.name = newName
            customizationsChanged.triggerDirectly(this)
            successCallback()
        }(errorCallback)
    }

    def createUserCustomization(name: String)
        (successCallback: UserCustomization => Unit)
        (errorCallback: Throwable => Unit) {

        fetchUserCustomizations { () =>
            _ownedUserCustomizations.foreach { ownedCustomizations =>
                CustomizationManager.createByUser(name) { newCustomization =>
                    ownedCustomizations += newCustomization
                    customizationsChanged.triggerDirectly(this)
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
                CustomizationManager.createByOntology(name, ontologyURLs) { newCustomization =>
                    ownedCustomizations += newCustomization
                    customizationsChanged.triggerDirectly(this)
                    successCallback(newCustomization)
                }(errorCallback)
            }
        }(errorCallback)
    }

    def deleteOntologyCustomization(ontologyCustomization: OntologyCustomization)
        (successCallback: () => Unit)
        (errorCallback: Throwable => Unit) {

        CustomizationManager.delete(ontologyCustomization.id) { () =>
            _ownedOntologyCustomizations.foreach(_ -= ontologyCustomization)
            customizationsChanged.triggerDirectly(this)
            successCallback()
        }(errorCallback)
    }

    def deleteUserCustomization(customization: UserCustomization)
        (successCallback: () => Unit)
        (errorCallback: Throwable => Unit) {

        CustomizationManager.delete(customization.id) { () =>
            _ownedUserCustomizations.foreach(_ -= customization)
            customizationsChanged.triggerDirectly(this)
            successCallback()
        }(errorCallback)
    }

    private def fetchOntologyCustomizations(successCallback: () => Unit)(errorCallback: Throwable => Unit) {
        if (!_ontologyCustomizationsAreLoaded) {
            CustomizationManager.getOntologyCustomizationsByOwnership() { ontoCustomizations =>
                ontoCustomizations.ownedCustomizations.foreach { owned =>
                    val c = mutable.ListBuffer.empty[OntologyCustomization]
                    owned.foreach(c += _)
                    _ownedOntologyCustomizations = Some(c)
                }
                _othersOntologyCustomizations = ontoCustomizations.othersCustomizations
                _ontologyCustomizationsAreLoaded = true

                successCallback()
            }(errorCallback)
        } else {
            successCallback()
        }
    }

    private def fetchUserCustomizations(successCallback: () => Unit)(errorCallback: Throwable => Unit) {
        if (!_userCustomizationsAreLoaded) {
            CustomizationManager.getUserCustomizationsByOwnership() { userCustomization =>
                userCustomization.ownedCustomizations.foreach { owned =>
                    val c = mutable.ListBuffer.empty[UserCustomization]
                    owned.foreach(c += _)
                    _ownedUserCustomizations = Some(c)
                }
                _othersUserCustomizations = userCustomization.othersCustomizations
                _userCustomizationsAreLoaded = true
                successCallback()
            }(errorCallback)
        } else {
            successCallback()
        }
    }

    def customizationsByOwnership(successCallback: (OntologyCustomizationsByOwnership, UserCustomizationsByOwnership) => Unit)
        (errorCallback: Throwable => Unit) {

        if (!_ontologyCustomizationsAreLoaded || !_userCustomizationsAreLoaded) {
            CustomizationManager.getCustomizationsByOwnership() { customizations =>
                customizations.ontologyCustomizations.ownedCustomizations.foreach { owned =>
                    val c = mutable.ListBuffer.empty[OntologyCustomization]
                    owned.foreach(c += _)
                    _ownedOntologyCustomizations = Some(c)
                }
                _othersOntologyCustomizations = customizations.ontologyCustomizations.othersCustomizations
                _ontologyCustomizationsAreLoaded = true

                customizations.userCustomizations.ownedCustomizations.foreach { owned =>
                    val c = mutable.ListBuffer.empty[UserCustomization]
                    owned.foreach(c += _)
                    _ownedUserCustomizations = Some(c)
                }
                _othersUserCustomizations = customizations.userCustomizations.othersCustomizations
                _userCustomizationsAreLoaded = true

                successCallback(
                    new OntologyCustomizationsByOwnership(_ownedOntologyCustomizations, _othersOntologyCustomizations),
                    new UserCustomizationsByOwnership(_ownedUserCustomizations, _othersUserCustomizations))

            }(errorCallback)
        } else {
            successCallback(
                new OntologyCustomizationsByOwnership(_ownedOntologyCustomizations, _othersOntologyCustomizations),
                new UserCustomizationsByOwnership(_ownedUserCustomizations, _othersUserCustomizations))
        }
    }
}
