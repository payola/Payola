package cz.payola.domain.entities.plugins.concrete

import collection.immutable
import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.parameters.StringParameter
import cz.payola.common.rdf.DataCubeDataStructureDefinition
import cz.payola.domain.IDGenerator
import cz.payola.domain.rdf._
import cz.payola.common.rdf.DataCubeDataStructureDefinition

/**
 * Data cube plugin. Its apply method takes a DataStructure definition of a DCV and builds a plugin based on that.
 *
 * The plugin then has as many parameters, as is the total sum of dimensions, attributes and measures. Its name
 * is based on the URI of the DSD. It has 1 input.
 *
 * @param name plugin name
 * @param inputCount input count
 * @param parameters parameters definition
 * @param id plugin ID
 * @author Jiri Helmich
 */
class DataCube(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends SparqlQuery(name, inputCount, parameters, id)
{
    def this(dataStructure: DataCubeDataStructureDefinition) = {
        this(dataStructure.uri, 1,
            (dataStructure.dimensions.map {
                d => new StringParameter(d.uri, "dimension"+" ~ "+d.label.getOrElse(""), false, true, false, false, d.order)
            } ++ dataStructure.measures.map {
                m => new StringParameter(m.uri, "measure"+" ~ "+m.label.getOrElse(""), false, true, false, false, m.order)
            } ++ dataStructure.attributes.map {
                a => new StringParameter(a.uri, "attribute"+" ~ "+a.label.getOrElse(""), false, true, false, false, a.order)
            }).toList
            , IDGenerator.newId)
    }

    /**
     * Returns the query to execute based on the plugin instance.
     * @param instance The evaluated plugin instance.
     * @return The query.
     */
    def getQuery(instance: PluginInstance): String = {
        instance.getStringParameter(instance.plugin.parameters.sortBy(_.ordering.getOrElse(9999)).head.name).getOrElse("")
    }

    /**
     * Plugin evaluation -> run a SPARQL query + add DCV definition
     * @param instance The corresponding instance.
     * @param inputs The input graphs.
     * @param progressReporter A method that can be used to report plugin evaluation progress (which has to be within
     *                         the (0.0, 1.0] interval).
     * @return The output graph.
     */
    override def evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit) = {
        val definedInputs = getDefinedInputs(inputs)
        val query = getQuery(instance)

        definedInputs(0).executeSPARQLQuery(query) + JenaGraph(RdfRepresentation.Turtle, getDCVDefinitionQuery(instance))
    }

    /**
     * Query builder
     * @param instance Plugin instance to build query from
     * @return Query
     */
    def getDCVDefinitionQuery(instance: PluginInstance) : String = {
        "[] a <http://purl.org/linked-data/cube#DataStructureDefinition> ;\n" +
        instance.plugin.parameters.map{ p =>
            val componentType = p.defaultValue.toString.split(" ~ ")(0)

        "    <http://purl.org/linked-data/cube#component> [ \n" +
        "        <http://purl.org/linked-data/cube#"+componentType+"> <"+p.name+"> ;\n" +
        "        <http://www.w3.org/2000/01/rdf-schema#label> \""+p.defaultValue.toString.split(" ~ ")(1) +"\"" +
                 p.ordering.map{ o =>
                    " ;\n        <http://purl.org/linked-data/cube#order> "+o.toString+" \n"
                 }.getOrElse("") +
        "    ] "
        }.mkString(" ;\n")+" ."
    }
}
