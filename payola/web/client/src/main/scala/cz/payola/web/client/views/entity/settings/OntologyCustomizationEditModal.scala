package cz.payola.web.client.views.entity.settings

import s2js.adapters.js.dom
import cz.payola.common.entities.settings._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.events._
import element.ColorInput
import inputs._
import cz.payola.web.client.presenters.entity.settings._

class OntologyCustomizationEditModal(ontologyCustomization: OntologyCustomization)
    extends Modal("Edit ontology customization", Nil, Some("Done"), None, false, "large-modal")
{
    val classFillColorChanged = new UnitEvent[InputControl[_], ClassCustomizationEventArgs[InputControl[_]]]

    val classRadiusDelayedChanged = new UnitEvent[InputControl[_], ClassCustomizationEventArgs[InputControl[_]]]

    val classGlyphDelayedChanged = new UnitEvent[InputControl[_], ClassCustomizationEventArgs[InputControl[_]]]

    val propertyStrokeColorChanged = new UnitEvent[InputControl[_], PropertyCustomizationEventArgs[InputControl[_]]]

    val propertyStrokeWidthDelayedChanged =
        new UnitEvent[InputControl[_], PropertyCustomizationEventArgs[InputControl[_]]]

    val ontologyCustomizationName = new TextInputControl("Name:", "name", ontologyCustomization.name, "", "span6")

    val shareButtonViewSpace = new Span(Nil)

    val deleteButton = new Button(new Text("Delete"), "btn-danger", new Icon(Icon.remove))

    private val classCustomizationListItems = ontologyCustomization.classCustomizations.map {
        classCustomization =>
            val classListItem = new ListItem(List(new Anchor(List(
                new Icon(Icon.tag),
                new Text(uriToName(classCustomization.uri)))
            )))
            classListItem.mouseClicked += {
                e =>
                    onClassCustomizationSelected(classCustomization, classListItem)
                    false
            }
            classListItem
    }

    private val propertiesDiv = new Div(Nil, "span8")

    override val body = List(
        new Div(List(
            new Div(List(
                ontologyCustomizationName,
                new Div(List(shareButtonViewSpace), "btn-group span3"),
                new Div(List(deleteButton), "btn-group span3")),
                "row-fluid"
            ),
            new Div(List(
                new Div(
                    List(new UnorderedList(classCustomizationListItems, "nav nav-list")),
                    "span4 modal-inner-view well no-padding"
                ).setAttribute("style", "padding: 8px 0;"),
                propertiesDiv),
                "row-fluid"
            )),
            "container-fluid"
        ).setAttribute("style", "padding: 0;")
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
        val fillColor = new ColorInputControl("Fill color:", "", classCustomization.fillColor, "")
        val radius = new NumericInputControl("Radius:", "", classCustomization.radius.toString, "")
        val glyph = new TextInputControl("Glyph:", "", classCustomization.glyph, "")

        val onFillColorChanged = {
            e: EventArgs[ColorInput] =>
                classFillColorChanged.trigger(new ClassCustomizationEventArgs(fillColor, classCustomization,
                    fillColor.input.getColorHexString))
        }
        fillColor.input.closed += onFillColorChanged
        fillColor.input.cleared += onFillColorChanged
        radius.delayedChanged += {
            e =>
                classRadiusDelayedChanged.trigger(new ClassCustomizationEventArgs(radius, classCustomization,
                    radius.input.value))
        }
        glyph.delayedChanged += {
            e =>
                classGlyphDelayedChanged.trigger(new ClassCustomizationEventArgs(glyph, classCustomization,
                    glyph.input.value))
        }

        fillColor.render(propertiesDiv.domElement)
        radius.render(propertiesDiv.domElement)
        glyph.render(propertiesDiv.domElement)
    }

    private def renderPropertyCustomizationViews(classCustomization: ClassCustomization,
        propertyCustomization: PropertyCustomization) {
        val headingDiv = new Div(List(new Text("Property " + uriToName(propertyCustomization.uri))), "label label-info")
        headingDiv.setAttribute("style", "padding: 5px; margin: 10px 0;")
        val strokeColor = new ColorInputControl("Stroke color:", "", propertyCustomization.strokeColor, "")
        val strokeWidth = new NumericInputControl("Stroke width:", "", propertyCustomization.strokeWidth.toString, "")

        val onStrokeColorChanged = {
            e: EventArgs[ColorInput] =>
                propertyStrokeColorChanged.trigger(new PropertyCustomizationEventArgs(strokeColor, classCustomization,
                    propertyCustomization, strokeColor.input.getColorHexString))
        }
        strokeColor.input.closed += onStrokeColorChanged
        strokeColor.input.cleared += onStrokeColorChanged
        strokeWidth.delayedChanged += {
            e =>
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
