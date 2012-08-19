package cz.payola.domain.entities.plugins.concrete

import cz.payola.domain.rdf.Graph
import scala.collection.immutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins._

class Union(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Plugin(name, inputCount, parameters, id)
{
    def this() = {
        this("Union", 2, Nil, IDGenerator.newId)
    }

    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit) = {
        // Currently the Union behaves as a strict union which means that all inputs have to be defined.
        getDefinedInputs(inputs).reduce(_ + _)
    }
}
