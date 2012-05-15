package cz.payola.domain.entities.analyses.plugins

import cz.payola.domain.rdf.Graph
import scala.collection.immutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.analyses._

class Union(
    name: String = "Union",
    inputCount: Int = 2,
    parameters: immutable.Seq[Parameter[_]] = Nil,
    id: String = IDGenerator.newId)
    extends Plugin(name, inputCount, parameters, id)
{
    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Graph], progressReporter: Double => Unit): Graph = {
        inputs(0) + inputs(1)
    }
}
