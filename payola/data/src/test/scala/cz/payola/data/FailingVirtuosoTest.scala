package cz.payola.data

import model.graph.RDFGraph
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import sparql.providers.AggregateDataProvider
import sparql.QueryExecutor

class FailingVirtuosoTest extends FlatSpec with ShouldMatchers
{
    "Result from virtuoso" should "be parsable by RFDGraph" in {
        val dataProvider = new AggregateDataProvider(List(new VirtuosoDataProvider()))
        val query = "select distinct ?Concept where {[] a ?Concept} LIMIT 100";
        val result = QueryExecutor.executeQuery(dataProvider, query)

        assert(result.data.nonEmpty)
        try {
            /* in my case,
                <rdf:RDF xmlns:res="http://www.w3.org/2005/sparql-results#" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                <rdf:Description rdf:nodeID="rset">
                <rdf:type rdf:resource="http://www.w3.org/2005/sparql-results#ResultSet" />
                    <res:resultVariable>Concept</res:resultVariable>
                    <res:solution rdf:nodeID="r0">
                      <res:binding rdf:nodeID="r0c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.openlinksw.com/schemas/virtrdf#QuadMapFormat"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r1">
                      <res:binding rdf:nodeID="r1c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.openlinksw.com/schemas/virtrdf#QuadStorage"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r2">
                      <res:binding rdf:nodeID="r2c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.openlinksw.com/schemas/virtrdf#array-of-QuadMap"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r3">
                      <res:binding rdf:nodeID="r3c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.openlinksw.com/schemas/virtrdf#QuadMap"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r4">
                      <res:binding rdf:nodeID="r4c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.openlinksw.com/schemas/virtrdf#array-of-QuadMapFormat"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r5">
                      <res:binding rdf:nodeID="r5c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.openlinksw.com/schemas/virtrdf#QuadMapValue"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r6">
                      <res:binding rdf:nodeID="r6c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.openlinksw.com/schemas/virtrdf#array-of-QuadMapATable"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r7">
                      <res:binding rdf:nodeID="r7c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.openlinksw.com/schemas/virtrdf#array-of-QuadMapColumn"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r8">
                      <res:binding rdf:nodeID="r8c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.openlinksw.com/schemas/virtrdf#QuadMapColumn"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r9">
                      <res:binding rdf:nodeID="r9c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.openlinksw.com/schemas/virtrdf#QuadMapFText"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r10">
                      <res:binding rdf:nodeID="r10c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.openlinksw.com/schemas/virtrdf#QuadMapATable"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r11">
                      <res:binding rdf:nodeID="r11c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.openlinksw.com/schemas/virtrdf#array-of-string"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r12">
                      <res:binding rdf:nodeID="r12c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.w3.org/2002/07/owl#AnnotationProperty"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r13">
                      <res:binding rdf:nodeID="r13c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.w3.org/2002/07/owl#Class"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r14">
                      <res:binding rdf:nodeID="r14c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.w3.org/2000/01/rdf-schema#Class"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r15">
                      <res:binding rdf:nodeID="r15c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.w3.org/1999/02/22-rdf-syntax-ns#Property"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r16">
                      <res:binding rdf:nodeID="r16c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.w3.org/2002/07/owl#Ontology"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r17">
                      <res:binding rdf:nodeID="r17c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.w3.org/2002/07/owl#OntologyProperty"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r18">
                      <res:binding rdf:nodeID="r18c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.geonames.org/ontology#Feature"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r19">
                      <res:binding rdf:nodeID="r19c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://purl.org/linked-data/cube#DataSet"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r20">
                      <res:binding rdf:nodeID="r20c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://www.w3.org/2004/03/trix/rdfg-1/Graph"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r21">
                      <res:binding rdf:nodeID="r21c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://xmlns.com/foaf/0.1/Document"/></res:binding>
                    </res:solution>
                    <res:solution rdf:nodeID="r22">
                      <res:binding rdf:nodeID="r22c0"><res:variable>Concept</res:variable><res:value rdf:resource="http://purl.org/linked-data/cube#Observation"/></res:binding>
                    </res:solution>
                  </rdf:Description>
                </rdf:RDF>
            */
            // TODO make this work
            val graph = RDFGraph(result.data.head)
        } catch {
            case e => fail(e.toString)
        }
    }
}
