package cz.payola.web.client.views.map.facets

import cz.payola.web.client.views.elements._
import s2js.runtime.client.scala.collection.immutable.HashMap
import s2js.compiler.javascript
import cz.payola.web.client.views.map.Marker
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements.form.fields._
import cz.payola.web.client.events.EventArgs
import cz.payola.common.visual.Color

class GroupingMapFacet(typeUri: String = "http://www.w3.org/2000/01/rdf-schema#type") extends MapFacet
{

    val panelContent = new Div(List())
    val panelBody = new Div(List(panelContent), "panel-body")
    val panelHeading = new Div(List(new Strong(List(new Text("Filter by values of <"+shortenTypeUri(typeUri,38)+">:")))), "panel-heading")

    val primaryButton = Button("Set facet as primary", "btn btn-primary btn-xs")
    primaryButton.mouseClicked += { e =>
        primaryRequested.trigger(new EventArgs[MapFacet](this))
        false
    }

    private var _isPrimary: Boolean = false

    val toggleAll = Button("Toggle all", "btn btn-default btn-xs")
    val randomColors = Button("Randomize colors", "btn btn-default btn-xs")

    val panelFooter = new Div(List(primaryButton, toggleAll, randomColors), "panel-footer")
    val panel = new Div(List(panelHeading, panelBody, panelFooter),"panel panel-default")

    val facetContainer = new Div(List(panel), "facetContainer")

    val groups = new mutable.HashMap[String, ArrayBuffer[Marker]]
    val colorGroups = new mutable.HashMap[String, ColorHTML5Input]
    val markers = new ArrayBuffer[Marker]()

    def shortenTypeUri(typeUri: String, maxLength: Int = 20) : String = {
        if (typeUri.length > maxLength){
            typeUri.substring(0,maxLength/2)+"..."+typeUri.substring(typeUri.length-(maxLength/2))
        } else {
            typeUri
        }
    }

    @javascript(""" return "#" + ('00000'+(Math.random()*(1<<24)|0).toString(16)).slice(-6); """)
    def randomColor() : String = "#AF54D7"

    @javascript(""" console.log(x) """)
    def log(x: Any) {}

    @javascript(
        """
           var entity = jsonGraphRepresentation[uri];
           if (entity[self.typeUri]){

                var property = entity[self.typeUri][0];

                if(property){
                    if(property["datatype"] && (property["datatype"] == "http://www.w3.org/2001/XMLSchema#dateTime" || property["datatype"] == "http://www.w3.org/2001/XMLSchema#date")){
                        return new scala.Some(property.value.split("-")[0]);
                    }
                }

                var dereferenced = jsonGraphRepresentation[entity[self.typeUri][0].value];

                if(dereferenced){

                    if(dereferenced["http://www.w3.org/2004/02/skos/core#prefLabel"]){
                        return new scala.Some(dereferenced["http://www.w3.org/2004/02/skos/core#prefLabel"][0].value);
                    }

                    if(dereferenced["http://www.w3.org/2000/01/rdf-schema#label"]){
                        return new scala.Some(dereferenced["http://www.w3.org/2000/01/rdf-schema#label"][0].value);
                    }
                }
                return new scala.Some(entity[self.typeUri][0].value);
           }
           return new scala.Some("not set");
        """)
    def getGroupValue(uri: String, jsonGraphRepresentation: String): Option[String] = None

    def registerUri(uri: String, jsonGraphRepresentation: String, marker: Marker){
        val groupValue = getGroupValue(uri, jsonGraphRepresentation)
        groupValue.map{ g =>
            if (!groups.isDefinedAt(g)){
                groups.put(g, new ArrayBuffer[Marker])
            }
            groups.get(g).foreach(_.append(marker))
        }
        markers += marker
    }

    def becamePrimary(){
        _isPrimary = true
        colorGroups.keys.foreach{ k =>
            groups.get(k).foreach(_.foreach{ _.setColor(colorGroups.get(k).map{ i => i.value.replace("#","")}.getOrElse("0000000")) })
            colorGroups.get(k).map{ i => i.show("inline-block") }
        }
        primaryButton.hide()
        randomColors.show("inline-block")
    }

    def unsetPrimary(){
        _isPrimary = false
        primaryButton.show("inline-block")
        randomColors.hide()
        colorGroups.keys.foreach{ k =>
            colorGroups.get(k).map{ i => i.hide() }
        }
    }

    def groupsCount = groups.size

    def namedMarkerGroups = groups

    def createSubViews = {

        groups.keys.foreach{ k =>
            val cbox = new CheckBox(k,true,"")
            val colorInput = new ColorHTML5Input("markerColor")
            colorInput.value = "#FF0000"
            if(!_isPrimary){
                colorInput.hide()
            }

            colorGroups.put(k, colorInput)

            val span = new Div(List(colorInput, cbox, new Text(k + (" ("+groups.get(k).get.size+") "))))

            cbox.changed += { e =>
                val add = if(e.target.value){ 1 }else{ -1 }
                groups.get(k).foreach(_.foreach{ m =>
                    m.visibility += add
                } )
            }

            toggleAll.mouseClicked += { e =>
                cbox.value = !cbox.value
                false
            }

            randomColors.mouseClicked += { e =>
                val color = randomColor()
                colorInput.updateValue(color)
                groups.get(k).foreach(_.foreach{ _.setColor(color.replace("#","")) })
                false
            }

            colorInput.changed += { e =>
                groups.get(k).foreach(_.foreach{ _.setColor(e.target.value.replace("#","")) })
            }

            span.render(panelContent.blockHtmlElement)
        }

        List(facetContainer)
    }
}
