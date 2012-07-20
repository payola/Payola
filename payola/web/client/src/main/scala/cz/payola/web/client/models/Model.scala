package cz.payola.web.client.models

import cz.payola.common.entities._
import cz.payola.common.entities.plugins.DataSource
import cz.payola.web.shared.managers._
import cz.payola.common.entities.settings.OntologyCustomization

object Model
{
    private var _accessibleDataSources: Option[Seq[DataSource]] = None

    private var _accessiblePlugins: Option[Seq[Plugin]] = None

    private var _accessibleDataFetchers: Option[Seq[Plugin]] = None

    private var _accessibleOntologyCustomizations: Option[Seq[OntologyCustomization]] = None

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

    def accessibleOntologyCustomizations(successCallback: Seq[OntologyCustomization] => Unit)
        (errorCallback: Throwable => Unit) {
        if (_accessibleOntologyCustomizations.isEmpty) {
            OntologyCustomizationManager.getAccessible() { o =>
                _accessibleOntologyCustomizations = Some(o)
                successCallback(o)
            }(errorCallback)
        } else {
            successCallback(_accessibleOntologyCustomizations.get)
        }
    }
}
