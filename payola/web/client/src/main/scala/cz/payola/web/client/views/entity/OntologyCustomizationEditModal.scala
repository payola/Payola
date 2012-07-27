package cz.payola.web.client.views.entity

import s2js.adapters.js.dom
import cz.payola.common.entities.settings._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.events._
import cz.payola.web.client.views.bootstrap.inputs._
import cz.payola.web.client.views.graph.visual.settings.components.visualsetup.ColorPane
import cz.payola.web.client.views.graph.visual.Color
import cz.payola.web.client.presenters.entity.settings._

class OntologyCustomizationEditModal(ontologyCustomization: OntologyCustomization)
    extends Modal("Edit ontology customization", Nil, Some("Done"), None, false, "ontology-customization-modal")
{
    val classFillColorChanged = new UnitEvent[ColorPane, ClassCustomizationEventArgs[ColorPane]]

    val classRadiusDelayedChanged = new UnitEvent[InputControl, ClassCustomizationEventArgs[InputControl]]

    val classGlyphDelayedChanged = new UnitEvent[InputControl, ClassCustomizationEventArgs[InputControl]]

    val propertyStrokeColorChanged = new UnitEvent[ColorPane, PropertyCustomizationEventArgs[ColorPane]]

    val propertyStrokeWidthDelayedChanged = new UnitEvent[InputControl, PropertyCustomizationEventArgs[InputControl]]

    val ontologyCustomizationName = new TextInputControl("Name:", "name", ontologyCustomization.name, "", "span6")

    val shareButtonViewSpace = new Span(Nil)

    val deleteButton = new Button(new Text("Delete"), "btn-danger", new Icon(Icon.remove))

    private val classCustomizationListItems = ontologyCustomization.classCustomizations.map { classCustomization =>
        val classListItem = new ListItem(List(new Anchor(List(
            new Icon(Icon.tag),
            new Text(uriToName(classCustomization.uri)))
        )))
        classListItem.mouseClicked += { e =>
            onClassCustomizationSelected(classCustomization, classListItem)
            false
        }
        classListItem
    }

    private val propertiesDiv = new Div(Nil, "span6 modal-inner-view")

    override val body = List(
        new Div(List(
            new Div(List(
                ontologyCustomizationName,
                new Div(List(
                    shareButtonViewSpace,
                    new Span(List(deleteButton))),
                    "btn-group span6"
                ).setAttribute("style", "display: inline;")),
                "row-fluid"
            ),
            new Div(List(
                new Div(
                    List(new UnorderedList(classCustomizationListItems, "nav nav-list")),
                    "span6 modal-inner-view well no-padding"
                ).setAttribute("style", "padding: 8px 0;"),
                propertiesDiv),
                "row-fluid"
            )),
            "container-fluid"
        ).setAttribute("style", "padding: 0px;")
    )

    override def render(parent: dom.Element) {
        super.render(parent)
        ontologyCustomization.classCustomizations.headOption.foreach {
            onClassCustomizationSelected(_, classCustomizationListItems.head)
        }
    }

    private def onClassCustomizationSelected(classCustomization: ClassCustomization, listItem: ListItem) {
        classCustomizationListItems.foreach(_.removeCssClass("active"))
        listItem.addCssClass("active")

        propertiesDiv.removeAllChildNodes()
        renderClassCustomizationViews(classCustomization)
        classCustomization.propertyCustomizations.foreach(renderPropertyCustomizationViews(classCustomization, _))
    }

    private def renderClassCustomizationViews(classCustomization: ClassCustomization) {
        val fillColor = new ColorPane("", "Fill color:", Color.fromHex(classCustomization.fillColor))
        val radius = new NumericInputControl("Radius:", "", classCustomization.radius.toString, "")
        val glyph = new TextInputControl("Glyph:", "", classCustomization.glyph, "")

        val onFillColorChanged = { e: EventArgs[ColorPane] =>
            classFillColorChanged.trigger(new ClassCustomizationEventArgs(fillColor, classCustomization,
                fillColor.getColorHexString))
        }
        fillColor.closed += onFillColorChanged
        fillColor.cleared += onFillColorChanged
        radius.delayedChanged += { e =>
            classRadiusDelayedChanged.trigger(new ClassCustomizationEventArgs(radius, classCustomization,
                radius.input.value))
        }
        glyph.delayedChanged += { e =>
            classGlyphDelayedChanged.trigger(new ClassCustomizationEventArgs(glyph, classCustomization,
                glyph.input.value))
        }

        fillColor.render(propertiesDiv.domElement)
        radius.render(propertiesDiv.domElement)
        glyph.render(propertiesDiv.domElement)
    }

    private def renderPropertyCustomizationViews(classCustomization: ClassCustomization,
        propertyCustomization: PropertyCustomization) {

        val headingDiv = new Div(List(new Text(uriToName(propertyCustomization.uri))), "label label-info")
        val strokeColor = new ColorPane("", "Stroke color:", Color.fromHex(propertyCustomization.strokeColor))
        val strokeWidth = new NumericInputControl("Stroke width:", "", propertyCustomization.strokeWidth.toString, "")

        val onStrokeColorChanged = { e: EventArgs[ColorPane] =>
            propertyStrokeColorChanged.trigger(new PropertyCustomizationEventArgs(strokeColor, classCustomization,
                propertyCustomization, strokeColor.getColorHexString))
        }
        strokeColor.closed += onStrokeColorChanged
        strokeColor.cleared += onStrokeColorChanged
        strokeWidth.delayedChanged += { e =>
            propertyStrokeWidthDelayedChanged.trigger(new PropertyCustomizationEventArgs(strokeWidth,
                classCustomization, propertyCustomization, strokeWidth.input.value))
        }

        headingDiv.render(propertiesDiv.domElement)
        strokeColor.render(propertiesDiv.domElement)
        strokeWidth.render(propertiesDiv.domElement)
    }

    private def uriToName(uri: String): String = {
        val nameParts = uri.split("#")
        if (nameParts.length > 1) nameParts(1) else uri
    }
}
