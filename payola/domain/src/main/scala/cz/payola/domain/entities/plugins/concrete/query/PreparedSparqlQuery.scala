package cz.payola.domain.entities.plugins.concrete.query

import scala.collection.immutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.concrete.SparqlQuery
import cz.payola.domain.entities.plugins.parameters.StringParameter

sealed class PreparedSparqlQuery(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends SparqlQuery(name, inputCount, parameters, id)
{
    def this() = {
        this("Prepared SPARQL Query", 1, List(
            new StringParameter(PreparedSparqlQuery.queryParameter, "", true, false, false, false, Some(0)),
            new StringParameter(PreparedSparqlQuery.paramsParameter, "", true, false, false, false, Some(0))
        ), IDGenerator.newId)
    }

    def getQuery(instance: PluginInstance): String = {
        usingDefined(
            instance.getStringParameter(PreparedSparqlQuery.queryParameter),
            instance.getStringParameter(PreparedSparqlQuery.paramsParameter)
        )((q,p) => {
            var query = q
            p.split("\n").foreach { param =>
                val parts = param.split(":")
                query = query.replaceAll(parts(0).replaceAll("\\?","\\\\?"),parts(1))
            }
            query
        })
    }
}

object PreparedSparqlQuery
{
    val queryParameter = "SPARQL Query"

    val paramsParameter = "Parameters"
}



