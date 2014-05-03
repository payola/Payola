package cz.payola.domain.rdf

import cz.payola.common.rdf._
import cz.payola.domain._
import java.io._
import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.query._

object JenaGraph
{
    /**
     * Returns a new empty graph.
     */
    def empty: JenaGraph = new JenaGraph(ModelFactory.createDefaultModel)

    /**
     * Takes a string representing a RDF data and returns an instance of Graph representing that particular graph.
     * @param representation Type of the RDF data representation.
     * @param data The RDF data of the graph.
     * @return A new graph instance.
     */
    def apply(representation: RdfRepresentation.Type, data: String): JenaGraph = {

        val result = Graph.rdf2Jena(representation, data).map(g => JenaGraph(ModelFactory.createModelForGraph(g)))

        result.size match {
            case 0 => JenaGraph.empty
            case 1 => result.head
            case _ => result.fold(JenaGraph.empty)(_ + _)
        }
    }

    def apply(payolaGraph: PayolaGraph) : JenaGraph = {
        JenaGraph(payolaGraph.getModel)
    }

    /**
     * Creates a new Graph instance from an instance of [[com.hp.hpl.jena.rdf.model.Model]].
     * @param model The model to create the graph from.
     * @return A new graph instance.
     */
    def apply(model: Model): JenaGraph = {
        new JenaGraph(model)
    }
}

class JenaGraph(model: Model)
    extends Graph(Nil, Nil, None)
{
    private var _payolaGraph : Option[PayolaGraph] = None

    override val edges = toPayolaGraph().edges
    override val vertices = toPayolaGraph().vertices
    override val resultsCount = Some(model.size)

    def +(otherGraph: Graph): JenaGraph = {
        val model = getModel
        model.add(otherGraph.getModel)
        JenaGraph(model)
    }

    def toPayolaGraph(forceUpdate : Boolean = false) : PayolaGraph = {
        if (forceUpdate || !_payolaGraph.isDefined){
            _payolaGraph = Some(PayolaGraph(getModel))
        }
        _payolaGraph.get
    }

   def getModel: Model = {
        //model
        ModelFactory.createDefaultModel().add(model)
    }

    protected def makeGraph(representation: RdfRepresentation.Type, rdf: String): JenaGraph = {
        JenaGraph(representation, rdf)
    }

    protected def processConstructQueryExecution(execution: QueryExecution): JenaGraph = {
        JenaGraph(execution.execConstruct)
    }
}
