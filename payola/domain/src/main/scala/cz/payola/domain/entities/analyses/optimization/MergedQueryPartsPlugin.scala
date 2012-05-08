package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.entities.analyses.PluginInstance
import cz.payola.domain.entities.analyses.plugins.query.SparqlQueryPart

object MergedQueryPartsPlugin extends SparqlQueryPart("Mltiple merged SPARQL query parts", Nil)
{
    override def createInstance(): PluginInstance = {
        MergedQueryPartsPluginInstance.empty
    }

    def getPattern(instance: PluginInstance, subject: String = defaultSubject): Option[String] = {
        instance match {
            case mergedInstance: MergedQueryPartsPluginInstance => {
                val typedPattern = mergedInstance.typed.flatMap(t => t.plugin.getPattern(t.instance, subject))
                val propertyPatterns = mergedInstance.propertyURIs.map {uri =>
                    getTriplePattern(subject, uri, mergedInstance.propertyVariables(uri))
                }

                if (typedPattern.isDefined || propertyPatterns.nonEmpty) {
                    Some(typedPattern.map(_ + "\n").getOrElse("") + propertyPatterns.mkString("\n"))
                } else {
                    None
                }
            }
            case _ => None
        }
    }

    override def getFilter(instance: PluginInstance, obj: String = defaultObject): Option[String] = {
        instance match {
            case mergedInstance: MergedQueryPartsPluginInstance => {
                val filters = mergedInstance.selections.flatMap { t =>
                    val propertyURI = t.plugin.getPropertyURI(t.instance).get
                    t.plugin.getFilter(t.instance, mergedInstance.propertyVariables(propertyURI))
                }

                if (filters.nonEmpty) {
                    Some(filters.mkString("\n"))
                } else {
                    None
                }
            }
            case _ => None
        }
    }
}


