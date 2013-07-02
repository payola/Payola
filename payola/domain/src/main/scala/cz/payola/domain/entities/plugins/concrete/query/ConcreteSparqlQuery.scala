package cz.payola.domain.entities.plugins.concrete.query

import scala.collection.immutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.concrete.SparqlQuery
import cz.payola.domain.entities.plugins.parameters.StringParameter

sealed class ConcreteSparqlQuery(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends SparqlQuery(name, inputCount, parameters, id)
{
    def this() = {
        this("SPARQL Query", 1, List(
            new StringParameter(ConcreteSparqlQuery.queryParameter, "", true, false, false, false, Some(0))
        ), IDGenerator.newId)
    }

    def getQuery(instance: PluginInstance): String = {
        usingDefined(instance.getStringParameter(ConcreteSparqlQuery.queryParameter))(q => q)
    }
}

object ConcreteSparqlQuery
{
    val queryParameter = "SPARQL Query"
}

