package cz.payola.domain.entities.plugins.concrete

import collection.immutable
import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins._
import cz.payola.domain.rdf.Graph
import cz.payola.domain.sparql._
import cz.payola.domain.entities.plugins.concrete.query.Construct
import cz.payola.common.entities.Analysis
import cz.payola.domain.IDGenerator

class AnalysisPlugin(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Plugin(name, inputCount, parameters, id)
{

    def this(analysis: Analysis, parameters: immutable.Seq[Parameter[_]]) = {
        this("Analysis "+analysis.name, 1, parameters, IDGenerator.newId)
    }

    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit) = {
        throw new Exception("This should be never called.")
    }
}
