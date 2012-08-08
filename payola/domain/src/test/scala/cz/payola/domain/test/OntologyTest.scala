package cz.payola.domain.test

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import cz.payola.domain.rdf.ontology.Ontology
import cz.payola.domain.net.Downloader
import cz.payola.domain.entities.settings.OntologyCustomization

class OntologyTest extends FlatSpec with ShouldMatchers
{
    val sampleOWL =
        """<?xml version="1.0" encoding="UTF-8"?>
          |<!DOCTYPE rdf:RDF [
          |    <!ENTITY xsd "http://www.w3.org/2001/XMLSchema#">
          |    <!ENTITY rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#">
          |    <!ENTITY rdfs "http://www.w3.org/2000/01/rdf-schema#">
          |    <!ENTITY owl "http://www.w3.org/2002/07/owl#">
          |    <!ENTITY ns_transport "http://www.ibm.com/WSRR/Transport#">
          |]>
          |<rdf:RDF
          |    xmlns:xsd="&xsd;"
          |    xmlns:rdf="&rdf;"
          |    xmlns:rdfs="&rdfs;"
          |    xmlns:owl="&owl;"
          |    xmlns:ns_transport="&ns_transport;">
          |
          |    <owl:Ontology rdf:about="&ns_transport;TransportOntology">
          |        <rdfs:label>A transport classification system.</rdfs:label>
          |        <rdfs:comment>Cars and buses and some superclasses.</rdfs:comment>
          |    </owl:Ontology>
          |    <owl:Class rdf:about="&ns_transport;Transport">
          |        <rdfs:label>Transport</rdfs:label>
          |        <rdfs:comment>Top-level root class for transport.</rdfs:comment>
          |    </owl:Class>
          |    <owl:Class rdf:about="&ns_transport;LandTransport">
          |        <rdfs:subClassOf rdf:resource="&ns_transport;Transport"/>
          |        <rdfs:label>Land Transport.</rdfs:label>
          |        <rdfs:comment>Middle-level land transport class.</rdfs:comment>
          |    </owl:Class>
          |    <owl:Class rdf:about="&ns_transport;AirTransport">
          |        <rdfs:subClassOf rdf:resource="&ns_transport;Transport"/>
          |        <rdfs:label>Air Transport.</rdfs:label>
          |        <rdfs:comment>Middle-level air transport class.</rdfs:comment>
          |    </owl:Class>
          |    <owl:Class rdf:about="&ns_transport;Bus">
          |        <rdfs:subClassOf rdf:resource="&ns_transport;LandTransport"/>
          |        <rdfs:label>Bus.</rdfs:label>
          |        <rdfs:comment>Bottom-level bus class.</rdfs:comment>
          |    </owl:Class>
          |    <owl:Class rdf:about="&ns_transport;Car">
          |        <rdfs:subClassOf rdf:resource="&ns_transport;LandTransport"/>
          |        <rdfs:label>Car.</rdfs:label>
          |        <rdfs:comment>Bottom-level car class.</rdfs:comment>
          |    </owl:Class>
          |    <rdf:Description rdf:about="http://purl.org/procurement/public-contracts#additionalObject">
          |        <rdf:type rdf:resource="http://www.w3.org/1999/02/22-rdf-syntax-ns#Property"/>
          |        <rdfs:subPropertyOf rdf:resource="http://purl.org/dc/terms/subject"/>
          |        <rdfs:label xml:lang="en">
          |            Additional object of contract
          |        </rdfs:label>
          |        <rdfs:comment xml:lang="en">
          |            Property for additional object of public contract. Cardinality 0..*
          |        </rdfs:comment>
          |        <rdfs:domain rdf:resource="http://purl.org/procurement/public-contracts#Contract"/>
          |        <rdfs:range rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
          |    </rdf:Description>
          |</rdf:RDF>
        """.stripMargin

    val regularRDF =
        """<?xml version="1.0"?>
          |<rdf:RDF
          |    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
          |    xmlns:cd="http://www.recshop.fake/cd#">
          |
          |    <rdf:Description rdf:about="http://www.recshop.fake/cd/Empire Burlesque">
          |        <cd:artist>Bob Dylan</cd:artist>
          |        <cd:country>USA</cd:country>
          |        <cd:company>Columbia</cd:company>
          |        <cd:price>10.90</cd:price>
          |        <cd:year>1985</cd:year>
          |    </rdf:Description>
          |
          |    <rdf:Description rdf:about="http://www.recshop.fake/cd/Hide your heart">
          |        <cd:artist>Bonnie Tyler</cd:artist>
          |        <cd:country>UK</cd:country>
          |        <cd:company>CBS Records</cd:company>
          |        <cd:price>9.90</cd:price>
          |        <cd:year>1988</cd:year>
          |    </rdf:Description>
          |</rdf:RDF>""".stripMargin

    val publicDataOntologyUrl = "http://opendata.cz/pco/public-contracts.xml"

    "Ontology" should "be created from a sample OWL document" in {
        val ontology = Ontology(sampleOWL)
        assert(ontology.classes.toList.length == 5, "All classes haven't been retrieved from the OWL.")
        assert(ontology.classes.contains("http://www.ibm.com/WSRR/Transport#Transport"),
            "The Transport class hasn't been retrieved.")
    }

    it should "be created from a valid RDF document" in {
        val ontology = Ontology(regularRDF)
        assert(ontology.classes.isEmpty, "The ontology contains classes in case it shouldn't.")
    }

    it should "be created from a downloaded document" in {
        val ontology = Ontology(new Downloader(publicDataOntologyUrl, accept = "application/rdf+xml").result)
        assert(!ontology.classes.isEmpty, "The downloaded ontology doesn't contain classes.")
        assert(!ontology.classes.values.flatMap(_.properties.values).isEmpty, "The classes don't contain properties.")
    }

    /*
    "Colors" should "be stored only if are valid" in {
        val emptyColor = ""
        val properColor = "rgb(0,       200,   155)"
        val invalidColor1 = "rgb(256, 0, 0)";
        val invalidColor2 = "rgba(200, 200, 200)"
        val invalidColor3 = "rgb(200, 200, 200, 1)"
        val invalidColor4 = "some string"

        val ontology = Ontology(new Downloader(publicDataOntologyUrl, accept = "application/rdf+xml").result)
        val clas = ontology
        val property = clas.propertyCustomizations(0)

        clas.fillColor = emptyColor
        property.strokeColor = emptyColor
    }
    */
}
