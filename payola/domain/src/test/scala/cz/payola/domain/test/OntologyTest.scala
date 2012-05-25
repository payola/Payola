package cz.payola.domain.test

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import cz.payola.domain.rdf.Graph
import cz.payola.domain.rdf.ontology.Model
import cz.payola.scala2json._

/**
  * Created with IntelliJ IDEA.
  * User: charliemonroe
  * Date: 5/23/12
  * Time: 2:00 PM
  * To change this template use File | Settings | File Templates.
  */

class OntologyTest extends FlatSpec with ShouldMatchers {

    val sampleOWL = """<?xml version="1.0" encoding="UTF-8"?>
                      |
                      |<!DOCTYPE rdf:RDF [
                      |  <!ENTITY xsd "http://www.w3.org/2001/XMLSchema#">
                      |  <!ENTITY rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                      |  <!ENTITY rdfs "http://www.w3.org/2000/01/rdf-schema#">
                      |  <!ENTITY owl "http://www.w3.org/2002/07/owl#">
                      |  <!ENTITY ns_transport "http://www.ibm.com/WSRR/Transport#">
                      |]>
                      |
                      |<rdf:RDF
                      |  xmlns:xsd="&xsd;"
                      |  xmlns:rdf="&rdf;"
                      |  xmlns:rdfs="&rdfs;"
                      |  xmlns:owl="&owl;"
                      |  xmlns:ns_transport="&ns_transport;"
                      |>
                      |
                      |  <owl:Ontology rdf:about="&ns_transport;TransportOntology">
                      |    <rdfs:label>A transport classification system.</rdfs:label>
                      |    <rdfs:comment>Cars and buses and some superclasses.</rdfs:comment>
                      |  </owl:Ontology>
                      |
                      |  <owl:Class rdf:about="&ns_transport;Transport">
                      |    <rdfs:label>Transport</rdfs:label>
                      |    <rdfs:comment>Top-level root class for transport.</rdfs:comment>
                      |  </owl:Class>
                      |
                      |  <owl:Class rdf:about="&ns_transport;LandTransport">
                      |    <rdfs:subClassOf rdf:resource="&ns_transport;Transport"/>
                      |    <rdfs:label>Land Transport.</rdfs:label>
                      |    <rdfs:comment>Middle-level land transport class.</rdfs:comment>
                      |  </owl:Class>
                      |
                      |  <owl:Class rdf:about="&ns_transport;AirTransport">
                      |    <rdfs:subClassOf rdf:resource="&ns_transport;Transport"/>
                      |    <rdfs:label>Air Transport.</rdfs:label>
                      |    <rdfs:comment>Middle-level air transport class.</rdfs:comment>
                      |  </owl:Class>
                      |
                      |  <owl:Class rdf:about="&ns_transport;Bus">
                      |    <rdfs:subClassOf rdf:resource="&ns_transport;LandTransport"/>
                      |    <rdfs:label>Bus.</rdfs:label>
                      |    <rdfs:comment>Bottom-level bus class.</rdfs:comment>
                      |  </owl:Class>
                      |
                      |  <owl:Class rdf:about="&ns_transport;Car">
                      |    <rdfs:subClassOf rdf:resource="&ns_transport;LandTransport"/>
                      |    <rdfs:label>Car.</rdfs:label>
                      |    <rdfs:comment>Bottom-level car class.</rdfs:comment>
                      |  </owl:Class>
                      |
                      |
                      |</rdf:RDF>""".stripMargin

    val secondSample = """<rdf:RDF
                         |	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                         |	xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                         |	xmlns:owl="http://www.w3.org/2002/07/owl#"
                         |	xmlns:dc="http://purl.org/dc/elements/1.1/"
                         |	xmlns:plants="http://www.linkeddatatools.com/plants#">
                         |
                         |	<!-- OWL Header Omitted For Brevity -->
                         |
                         |	<!-- OWL Class Definition - Plant Type -->
                         |	<owl:Class rdf:about="http://www.linkeddatatools.com/plants#planttype">
                         |
                         |		<rdfs:label>The plant type</rdfs:label>
                         |		<rdfs:comment>The class of all plant types.</rdfs:comment>
                         |
                         |	</owl:Class>
                         |
                         |	<!-- OWL Subclass Definition - Flower -->
                         |	<owl:Class rdf:about="http://www.linkeddatatools.com/plants#flowers">
                         |
                         |		<!-- Flowers is a subclassification of planttype -->
                         |		<rdfs:subClassOf rdf:resource="http://www.linkeddatatools.com/plants#planttype"/>
                         |
                         |		<rdfs:label>Flowering plants</rdfs:label>
                         |		<rdfs:comment>Flowering plants, also known as angiosperms.</rdfs:comment>
                         |
                         |	</owl:Class>
                         |
                         |	<!-- OWL Subclass Definition - Shrub -->
                         |	<owl:Class rdf:about="http://www.linkeddatatools.com/plants#shrubs">
                         |
                         |		<!-- Shrubs is a subclassification of planttype -->
                         |		<rdfs:subClassOf rdf:resource="http://www.linkeddatatools.com/plants#planttype"/>
                         |
                         |		<rdfs:label>Shrubbery</rdfs:label>
                         |		<rdfs:comment>Shrubs, a type of plant which branches from the base.</rdfs:comment>
                         |
                         |	</owl:Class>
                         |
                         |	<!-- Individual (Instance) Example RDF Statement -->
                         |	<rdf:Description rdf:about="http://www.linkeddatatools.com/plants#magnolia">
                         |
                         |		<!-- Magnolia is a type (instance) of the flowers classification -->
                         |		<rdf:type rdf:resource="http://www.linkeddatatools.com/plants#flowers"/>
                         |
                         |	</rdf:Description>
                         |
                         |</rdf:RDF>""".stripMargin

    "Ontology" should "be created from sample RDF" in {
        try {
            val m: Model = Model(sampleOWL)
            //val serializer = new JSONSerializer()
            //serializer.outputFormat = OutputFormat.PrettyPrinted
            //serializer.serializeInDepth = true
            println(m)
        }catch{
            case e: Exception => e.printStackTrace()
        }
    }

    "Ontology" should "be created from second sample RDF" in {
        try {
            val m: Model = Model(secondSample)
            //val serializer = new JSONSerializer()
            //serializer.outputFormat = OutputFormat.PrettyPrinted
            //serializer.serializeInDepth = true
            println(m)
        }catch{
            case e: Exception => e.printStackTrace()
        }
    }
}
