package cz.payola.domain.entities.plugins.concrete

import collection.immutable
import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.parameters.StringParameter
import cz.payola.common.rdf.DataCubeDataStructureDefinition
import cz.payola.domain.IDGenerator
import cz.payola.domain.rdf.Graph

class DataCube(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends SparqlQuery(name, inputCount, parameters, id)
{
    def this(dataStructure: DataCubeDataStructureDefinition) = {
        this(dataStructure.uri, 1,
            (dataStructure.dimensions.map {
                d => new StringParameter(d.uri, d.label.getOrElse(""), false, true, false, false, d.order)
            } ++ dataStructure.measures.map {
                m => new StringParameter(m.uri, m.label.getOrElse(""), false, true, false, false, m.order)
            }).toList
            , IDGenerator.newId)
    }

    /**
     * Returns the query to execute based on the plugin instance.
     * @param instance The evaluated plugin instance.
     * @return The query.
     */
    def getQuery(instance: PluginInstance): String = {
        instance.getStringParameter(instance.plugin.parameters.head.name).getOrElse("")
    }
}
