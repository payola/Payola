package cz.payola.domain.entities.analyses.plugins.query

import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.analyses.{PluginInstance, Plugin, Parameter}
import collection.immutable
import cz.payola.domain.entities.analyses.plugins.SparqlQuery

abstract class SparqlQueryPart(name: String, parameters: immutable.Seq[Parameter[_]])
    extends SparqlQuery(name, parameters)
{
    protected val defaultSubject = "?x"

    protected val defaultObject = "?y"

    /**
      * Returns a graph pattern that would be placed both in the construct and where regions of the query.
      * @param instance The evaluated plugin instance.
      * @param subject The subject used in the pattern.
      * @return The graph pattern.
      */
    def getPattern(instance: PluginInstance, subject: String = defaultSubject): Option[String]

    /**
      * Returns an expression that would be used as a filter in the where region of the query.
      * @param instance The evaluated plugin instance.
      * @param obj The object used in the filter.
      * @return The graph pattern.
      */
    def getFilter(instance: PluginInstance, obj: String = defaultObject): Option[String] = {
        None
    }

    def getQuery(instance: PluginInstance): Option[String] = {
        getPattern(instance).map {pattern =>
            """
                CONSTRUCT { %1$s }
                WHERE {
                    %1$s
                    %2$s
                }
            """.format(pattern, getFilter(instance).map("FILTER (" + _ + ") .").getOrElse(""))
        }
    }
}
