package cz.payola.model

import cz.payola.common.rdf.Graph
import cz.payola.domain.rdf.{RDFEdge, RDFGraph, RDFIdentifiedNode}
import model.graph.{RDFEdge, RDFIdentifiedNode, RDFGraph}
import providers.VirtuosoDataProvider
import sparql.providers.AggregateDataProvider
import cz.payola.data.rdf.QueryExecutor
import sparql.QueryExecutor
import cz.payola.data.rdf.sparql.providers.AggregateDataProvider
import cz.payola.data.rdf.providers.VirtuosoDataProvider

class DataFacade
{
    val dataProvider = new AggregateDataProvider(List(
        new VirtuosoDataProvider()
    ))

    def getGraph(uri: String): Graph = {
        val query = """
            CONSTRUCT {
                <http://dbpedia.org/resource/Prague> ?p1 ?n1 .
                ?n1 ?p2 ?n2 .
            }
            WHERE {
                <http://dbpedia.org/resource/Prague> ?p1 ?n1 .
                OPTIONAL { ?n1 ?p2 ?n2 }
            }
            LIMIT 40
        """
        val result = QueryExecutor.executeQuery(dataProvider, query)

        // If the Virtuoso doesn't work.
        val rdf = """<?xml version="1.0" encoding="utf-8" ?>
            <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:rdfs="http://www.w3
            .org/2000/01/rdf-schema#">
            <rdf:Description rdf:about="http://dbpedia.org/resource/Czech_Republic"><rdfs:label xml:lang="en">Czech
            Republic</rdfs:label></rdf:Description>
            <rdf:Description rdf:about="http://dbpedia.org/resource/Czech_Republic"><geo:lat xmlns:geo="http://www.w3
            .org/2003/01/geo/wgs84_pos#" rdf:datatype="http://www.w3.org/2001/XMLSchema#float">50
            .08333206176758</geo:lat></rdf:Description>
            <rdf:Description rdf:about="http://dbpedia.org/resource/Czech_Republic"><geo:long xmlns:geo="http://www.w3
            .org/2003/01/geo/wgs84_pos#" rdf:datatype="http://www.w3.org/2001/XMLSchema#float">14
            .46666622161865</geo:long></rdf:Description>
            <rdf:Description rdf:about="http://opendata.cz/data/test/wb/CZE"><owl:sameAs xmlns:owl="http://www.w3
            .org/2002/07/owl#" rdf:resource="http://dbpedia.org/resource/Czech_Republic"/></rdf:Description>
            <rdf:Description rdf:about="http://dbpedia.org/resource/Czech_Republic"><geo:lat xmlns:geo="http://www.w3
            .org/2003/01/geo/wgs84_pos#" rdf:datatype="http://www.w3.org/2001/XMLSchema#float">49
            .75</geo:lat></rdf:Description>
            <rdf:Description rdf:about="http://dbpedia.org/resource/Czech_Republic"><n0pred:populationDensity
            xmlns:n0pred="http://dbpedia.org/ontology/" rdf:datatype="http://www.w3.org/2001/XMLSchema#double">131
            .6608360629741</n0pred:populationDensity></rdf:Description>
            <rdf:Description rdf:about="http://dbpedia.org/resource/Czech_Republic"><n0pred:populationDensity
            xmlns:n0pred="http://dbpedia.org/ontology/" rdf:datatype="http://www.w3
            .org/2001/XMLSchema#double">133</n0pred:populationDensity></rdf:Description>
            <rdf:Description rdf:about="http://dbpedia.org/resource/Czech_Republic"><n0pred:areaTotal
            xmlns:n0pred="http://dbpedia.org/ontology/" rdf:datatype="http://www.w3
            .org/2001/XMLSchema#double">78865137959.7312</n0pred:areaTotal></rdf:Description>
            <rdf:Description rdf:about="http://dbpedia.org/resource/Czech_Republic"><geo:long xmlns:geo="http://www.w3
            .org/2003/01/geo/wgs84_pos#" rdf:datatype="http://www.w3.org/2001/XMLSchema#float">15
            .75</geo:long></rdf:Description>
            <rdf:Description rdf:about="http://dbpedia.org/resource/Czech_Republic"><n0pred:areaTotal
            xmlns:n0pred="http://dbpedia.org/ontology/" rdf:datatype="http://www.w3
            .org/2001/XMLSchema#double">78866000000</n0pred:areaTotal></rdf:Description>
            </rdf:RDF>
        """

        RDFGraph(result.data.headOption.getOrElse(rdf))
    }
}
