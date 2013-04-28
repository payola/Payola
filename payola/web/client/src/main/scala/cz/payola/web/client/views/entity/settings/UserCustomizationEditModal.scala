package cz.payola.web.client.views.entity.settings

import cz.payola.common.entities.settings._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.events.UnitEvent
import cz.payola.web.client.presenters.entity.settings._
import cz.payola.web.client.views.elements.form.fields._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.elements.lists._
import s2js.adapters.html
import cz.payola.common.visual.Color
import cz.payola.web.client.views.bootstrap.element._
import cz.payola.common.rdf._
import cz.payola.web.shared.managers.OntologyCustomizationManager
import cz.payola.common._
import cz.payola.web.client.views.bootstrap.modals.AlertModal
import scala.Some
import scala.Some

class UserCustomizationEditModal (currentGraph: Option[Graph], var userCustomization: OntologyCustomization, onClose: () => Unit)
    extends Modal("Edit user customization", Nil, Some("Done"), None, false, "large-modal")
{
    private var classCustomizations = userCustomization.classCustomizations.map { userClassCust =>
        userClassCust.asInstanceOf[ClassCustomization]
    }

    private var selectedClassCustomization = classCustomizations.head

    val classFillColorChanged = new UnitEvent[InputControl[_], ClassCustomizationEventArgs[InputControl[_]]]

    val classRadiusDelayedChanged = new UnitEvent[InputControl[_], ClassCustomizationEventArgs[InputControl[_]]]

    val classGlyphChanged = new UnitEvent[InputControl[_], ClassCustomizationEventArgs[InputControl[_]]]

    val propertyStrokeColorChanged = new UnitEvent[InputControl[_], PropertyCustomizationEventArgs[InputControl[_]]]

    val propertyStrokeWidthDelayedChanged =
        new UnitEvent[InputControl[_], PropertyCustomizationEventArgs[InputControl[_]]]

    val userCustomizationName = new InputControl(
        "Name:",
        new TextInput("name", userCustomization.name, "", "span6"),
        Some("span2")
    )

    saveButton.mouseClicked += { e =>
        onClose()
        true
    }

    val deleteButton = new Button(new Text("Delete"), "btn-danger", new Icon(Icon.remove))

    val appendClassButton = new AppendToUserCustButton(
        currentGraph.get.vertices.filter{ vertex =>
            vertex match {
                case i: IdentifiedVertex => ! classCustomizations.exists{ classCust => i.uri == classCust.uri }
                case _ => false
            }
        }.map{vertex => vertex.asInstanceOf[IdentifiedVertex].uri},
        "Append class", "Classes available in the current graph: ", "", onAppendClass)

    appendClassButton.appendButton.mouseClicked += { e =>
        appendClassButton.availableURIs = currentGraph.get.vertices.filter{ vertex =>
            vertex match {
                case i: IdentifiedVertex => ! classCustomizations.exists{ classCust => i.uri == classCust.uri }
                case _ => false
            }
        }.map{vertex => vertex.asInstanceOf[IdentifiedVertex].uri}
        false
    }

    val appendPropertyButton = new AppendToUserCustButton(classCustomizations.map(_.uri),
        "Append Property", "Properties available in the current graph: ", "", onAppendProperty)

    appendPropertyButton.appendButton.mouseClicked += { e =>
        val availablePropertyURIs = currentGraph.get.edges.filter{ edge =>
            //all properties (edges) of the selectedClassCustomization (vertex in graph)
            //that is not already in its (selectedClassCustomization) propertyCustomizations list
            edge.origin.uri == selectedClassCustomization.uri && !selectedClassCustomization.propertyCustomizations.exists(_.uri == edge.uri)
        }.map(_.uri)

        appendPropertyButton.availableURIs = availablePropertyURIs
        appendPropertyButton.openPopup()
        false
    }

    private var classCustomizationListItems = userCustomization.classCustomizations.map { classCustomization =>
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
    private val classesDiv = new Div(List(new UnorderedList(classCustomizationListItems, "nav nav-list")),
        "span4 modal-inner-view well no-padding").setAttribute("style", "padding: 8px 0;")

    override val body = List(
        new Div(List(
            new Div(List(
                userCustomizationName,
                new Div(List(appendClassButton,appendPropertyButton, deleteButton), "btn-group inline-block pull-right")),
                "row-fluid button-row"
            ),
            new Div(List(classesDiv, propertiesDiv), "row-fluid")),
            "container-fluid"
        ).setAttribute("style", "padding: 0;")
    )

    override def render(parent: html.Element) {
        super.render(parent)
        userCustomization.classCustomizations.headOption.foreach {
            onClassCustomizationSelected(_, classCustomizationListItems.head)
        }
    }

    private def onAppendClass(newClassURI: String): Boolean = {
        if(!classCustomizations.exists{ customization => //if this name does not already exist
            customization.uri == newClassURI
        }) {
            propertiesDiv.removeAllChildNodes()

            //create the class
            OntologyCustomizationManager.createClassCustomization(
                userCustomization.id, newClassURI, List[String]()) { ontologyCustomization =>

                val newClass = ontologyCustomization.classCustomizations.last.asInstanceOf[ClassCustomization]
                classCustomizations ++= List(newClass)
                renderDefinedClass(newClass)
            }{ error =>
                //TODO what shall I do if a class customization can not be created??
            }
            true
        } else {
            AlertModal.display("Information", newClassURI + " is already defined.", "", Some(4000))
            false
        }
    }

    private def onAppendProperty(newPropertyURI: String): Boolean = {
        if(!selectedClassCustomization.propertyCustomizations.exists{ propCust => //if this name does not already exist
            propCust.uri == newPropertyURI
        }) {
            propertiesDiv.removeAllChildNodes()

            //create the class
            OntologyCustomizationManager.createPropertyCustomization(
                userCustomization.id, selectedClassCustomization.uri, newPropertyURI) { ontologyCustomization =>

                val updatedCurrentClassOpt = ontologyCustomization.classCustomizations.find(_.uri == selectedClassCustomization.uri)
                if (updatedCurrentClassOpt.isDefined) {
                    onClassCustomizationSelected(updatedCurrentClassOpt.get,
                        renderDefinedProperty(updatedCurrentClassOpt.get))
                }
            }{ error =>
                //TODO what shall I do if a class customization can not be created??
            }
            true
        } else {
            AlertModal.display("Information", newPropertyURI + " is already defined.", "", Some(4000))
            false
        }
    }

    /**
     * Adds a created ClassCustomization to the list of defined ClassCustomizations (classesDiv);
     * reaction to pressed button of a undefined ClassCustomization in the available ClassCustomizations list
     * @param definedClass new ClassCustomization to add
     */
    private def renderDefinedClass(definedClass: ClassCustomization) {

        val classListItem = new ListItem(List(new Anchor(List(
            new Icon(Icon.tag),
            new Text(uriToName(definedClass.uri)))
        )))
        classListItem.mouseClicked += { e =>
            onClassCustomizationSelected(definedClass, classListItem)
            false
        }

        classListItem.mouseClicked.clear()

        classCustomizationListItems ++= List(classListItem)
        classesDiv.removeAllChildNodes()
        val list = new UnorderedList(classCustomizationListItems, "nav nav-list")
        list.render(classesDiv.htmlElement)
    }

    private def renderDefinedProperty(updatedClass: ClassCustomization): ListItem = {


        classCustomizations = classCustomizations.filter(_.uri != updatedClass.uri) // remove the old classCustomization (without the new property)
        classCustomizations ++= List(updatedClass) //add the updated classCustomization (with the new property)


        val updatedClassListItem = classCustomizationListItems.find(
            _.subViews.head.asInstanceOf[Anchor].subViews(1).asInstanceOf[Text].text == uriToName(updatedClass.uri))

        if(updatedClassListItem.isEmpty) {
            throw new PayolaException("Class customization: "+updatedClass.uri+" was not found.")
        }

        updatedClassListItem.get.mouseClicked.clear()
        updatedClassListItem.get.mouseClicked += { e =>
            onClassCustomizationSelected(updatedClass, updatedClassListItem.get)
            false
        }

        updatedClassListItem.get
    }

    /**
     * Generates the content of a ClassCustomization and its PropertyCustomizations;
     * reaction to "ClassCustomization" selected from the list of defined ClassCustomizations
     * @param classCustomization what customization is selected
     * @param listItem html container of the selected customization
     */
    private def onClassCustomizationSelected(classCustomization: ClassCustomization, listItem: ListItem) {
        classCustomizationListItems.foreach(_.removeCssClass("active"))
        listItem.addCssClass("active")
        selectedClassCustomization = classCustomization

        propertiesDiv.removeAllChildNodes()
        renderClassCustomizationViews(classCustomization)
        classCustomization.propertyCustomizations.foreach(renderPropertyCustomizationViews(classCustomization, _))
    }

    private def renderClassCustomizationViews(classCustomization: ClassCustomization) {
        val fillColor = new InputControl(
            "Fill color:",
            new ColorInput("fillColor", Color(classCustomization.fillColor), ""), Some("span2")
        )
        val radius = new InputControl(
            "Radius:",
            new NumericInput("radius", classCustomization.radius, "")   , Some("span2")
        )
        val glyph = new InputControl(
            "Glyph:",
            new GlyphInput("glyph", Some(classCustomization.glyph), "")  , Some("span2")
        )

        fillColor.delayedChanged += { _ =>
            classCustomization.fillColor = fillColor.field.value.map(_.toString).getOrElse("")
            classFillColorChanged.trigger(new ClassCustomizationEventArgs(fillColor, classCustomization,
                fillColor.field.value.map(_.toString).getOrElse("")))
        }
        radius.delayedChanged += { _ =>
            classCustomization.radius = validateInt(radius.field.value.toString, "radius")
            classRadiusDelayedChanged.trigger(new ClassCustomizationEventArgs(radius, classCustomization,
                radius.field.value.toString))
        }
        glyph.field.changed += { _ =>
            classCustomization.glyph = glyph.field.value.getOrElse("")
            classGlyphChanged.trigger(new ClassCustomizationEventArgs(glyph, classCustomization,
                glyph.field.value.getOrElse("")))
        }

        fillColor.render(propertiesDiv.htmlElement)
        radius.render(propertiesDiv.htmlElement)
        glyph.render(propertiesDiv.htmlElement)
    }

    private def renderPropertyCustomizationViews(classCustomization: ClassCustomization, propertyCustomization: PropertyCustomization) {
        val headingDiv = new Div(List(new Text("Class " + uriToName(propertyCustomization.uri))), "label label-info")
        headingDiv.setAttribute("style", "padding: 5px; margin: 10px 0;")

        val strokeColor = new InputControl(
            "Stroke color:",
            new ColorInput("strokeColor", Color(propertyCustomization.strokeColor), ""), Some("span2")
        )
        val strokeWidth = new InputControl(
            "Stroke width:",
            new NumericInput("strokeWidth", propertyCustomization.strokeWidth, ""), Some("span2")
        )

        strokeColor.delayedChanged += { _ =>
            propertyCustomization.strokeColor = strokeColor.field.value.map(_.toString).getOrElse("")
            propertyStrokeColorChanged.trigger(new PropertyCustomizationEventArgs(strokeColor, classCustomization,
                propertyCustomization, strokeColor.field.value.map(_.toString).getOrElse("")))
        }
        strokeWidth.delayedChanged += { _ =>
            propertyCustomization.strokeWidth = validateInt(strokeWidth.field.value.toString, "strokeWidth")
            propertyStrokeWidthDelayedChanged.trigger(new PropertyCustomizationEventArgs(strokeWidth,
                classCustomization, propertyCustomization, strokeWidth.field.value.toString))
        }

        headingDiv.render(propertiesDiv.htmlElement)
        strokeColor.render(propertiesDiv.htmlElement)
        strokeWidth.render(propertiesDiv.htmlElement)
    }

    private def uriToName(uri: String): String = {
        val nameParts = uri.split("#")
        if (nameParts.length > 1) nameParts(1) else uri
    }

    private def validateInt(value: String, field: String): Int = {
        if (!value.matches("^[0-9]+$")){ throw new ValidationException(field, "Value can contain only digits") }

        try {
            value.toInt
        } catch { case t: Throwable => throw new ValidationException(field, "Value is out of range") }
    }
}