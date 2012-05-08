package cz.payola.domain.entities.analyses.plugins.query

import cz.payola.domain.entities.analyses.parameters.StringParameter
import cz.payola.domain.entities.analyses.PluginInstance
import collection.immutable

class Projection extends SparqlQueryPart("Projection", List(new StringParameter("PropertyURIs", "")))
{
    def getPropertyURIs(instance: PluginInstance): Option[immutable.Seq[String]] = {
        instance.getStringParameter("PropertyURIs").map(_.split("\n").toList)
    }

    def getPattern(instance: PluginInstance, subject: String = defaultSubject): Option[String] = {
        getPropertyURIs(instance).map {uris =>
            uris.zipWithIndex.map(uri => getTriplePattern(subject, uri._1, "?p" + uri._2)).mkString("\n")
        }
    }
}
