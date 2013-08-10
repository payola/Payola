package cz.payola.web.client.views.entity.settings

import cz.payola.common.entities.settings._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.events._
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
import cz.payola.web.client.views.graph.visual.graph._
import scala.collection.mutable.ListBuffer

class UserCustomizationEditModal (currentGraphView: Option[GraphView], var userCustomization: OntologyCustomization, onClose: () => Unit)
    extends Modal("Edit user customization", Nil, Some("Done"), None, false, "large-modal")
{
    private val currentGraphVertices: List[Vertex] = if(currentGraphView.isDefined) {
        val vertices = ListBuffer[Vertex]()
        currentGraphView.get.getAllVertices.foreach{ _ match {
            case group: VertexViewGroup => vertices ++= group.getAllVertices
            case view: VertexView => vertices += view.vertexModel
        }}
        vertices.toList
    } else { List[Vertex]() }

    private val currentGraphEdges: List[Edge] = if(currentGraphView.isDefined) {
        val edges = ListBuffer[Edge]()
        currentGraphView.get.getAllEdges.foreach{ edge => edges += edge.edgeModel }
        edges.toList
    } else { List[Edge]() }

    private val currentGraphGroups: List[VertexViewGroup] = if(currentGraphView.isDefined) {
        val groups = ListBuffer[VertexViewGroup]()
        currentGraphView.get.getAllVertices.foreach{ _ match {
            case group: VertexViewGroup => groups += group
        }}
        groups.toList
    } else { List[VertexViewGroup]() }

    private var classCustomizations = userCustomization.classCustomizations.filter(e =>
        e.uri != "properties" && !e.isGroupCustomization).map{
        userClassCust => userClassCust.asInstanceOf[ClassCustomization]
    }

    private var groupCustomizations = userCustomization.classCustomizations.filter(e =>
        e.isGroupCustomization).map{ userClassCust => userClassCust.asInstanceOf[ClassCustomization] }

    private def propertiesContainer = userCustomization.classCustomizations.find(_.uri == "properties")
    
    private var propertyCustomizations = if(propertiesContainer.isDefined) {
        propertiesContainer.get.propertyCustomizations
    } else {
        List[PropertyCustomization]()
    }

    private var selectedItem: Option[CustomizationItem] =
        if(classCustomizations.isEmpty) {
            if(groupCustomizations.isEmpty) {
                if(propertyCustomizations.isEmpty) None
                else Some(CustomizationItem.create(propertyCustomizations.head))
            } else { Some(CustomizationItem.create(groupCustomizations.head))}
        } else {
            Some(CustomizationItem.create(classCustomizations.head))
        }

    val customizationChanged = new UnitEvent[OntologyCustomization, OntologyCustomizationEventArgs]

    val classFillColorChanged = new UnitEvent[InputControl[_], ClassCustomizationEventArgs[InputControl[_]]]

    val classRadiusDelayedChanged = new UnitEvent[InputControl[_], ClassCustomizationEventArgs[InputControl[_]]]

    val classGlyphChanged = new UnitEvent[InputControl[_], ClassCustomizationEventArgs[InputControl[_]]]

    val propertyStrokeColorChanged = new UnitEvent[InputControl[_], PropertyCustomizationEventArgs[InputControl[_]]]

    val classLabelsChanged = new UnitEvent[InputControl[_], ClassCustomizationEventArgs[InputControl[_]]]

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
        currentGraphVertices.filter{ vertex =>
            vertex match {
                case i: IdentifiedVertex => ! classCustomizations.exists{ classCust => i.uri == classCust.uri }
                case _ => false
            }
        }.map{vertex => vertex.asInstanceOf[IdentifiedVertex].uri},
        "Append class", "Vertices available in the current graph: ", "", onAppendClass)

    appendClassButton.appendButton.mouseClicked += { e =>
        appendClassButton.availableValues = currentGraphVertices.filter{ vertex =>
            vertex match {
                case i: IdentifiedVertex => ! classCustomizations.exists(_.uri == i.uri)
                case _ => false
            }
        }.map{vertex => vertex.asInstanceOf[IdentifiedVertex].uri}
        appendClassButton.openPopup()
        false
    }

    val appendGroupClassButton = new AppendToUserCustButton(
        currentGraphGroups.filter{ group =>
            group.getName != null && group.getName != "" && !groupCustomizations.exists(_.uri == group.getName) }.map(_.getName),
        "Append group", "Groups available in current graph: ", "", onAppendGroup, "Custom group name")

    appendGroupClassButton.appendButton.mouseClicked += { e =>
        appendGroupClassButton.availableValues = currentGraphGroups.filter{ group => group.getName != null && group.getName != ""
        }.map(_.getName)
        appendGroupClassButton.openPopup()
        false
    }

    val appendPropertyButton = new AppendToUserCustButton(classCustomizations.map(_.uri),
        "Append Property", "Attributes available in the current graph: ", "", onAppendProperty)

    appendPropertyButton.appendButton.mouseClicked += { e =>
        val availablePropertyURIs = currentGraphEdges.filter{ edge =>
            //all properties (edges) that are not already in the propertyCustomizations
            !propertyCustomizations.exists(_.uri == edge.uri)
        }.map(_.uri)

        appendPropertyButton.availableValues = availablePropertyURIs
        appendPropertyButton.openPopup()

        false
    }

    private var classCustomizationsListItems = classCustomizations.map { customization =>
        val listItem = new ListItem(List(new Anchor(List(
            new Icon(Icon.tag),
            new Text(uriToName(customization.uri)))
        )))
        listItem.mouseClicked += { e => onListItemSelected(customization, listItem, renderClassCustomizationViews)
            false
        }
        listItem
    }

    private var groupCustomizationListItems = groupCustomizations.map { customization =>
        val listItem = new ListItem(List(new Anchor(List(
            new Icon(Icon.ccShare),
            new Text(customization.uri.substring(6)))
        )))
        listItem.mouseClicked += { e => onListItemSelected(customization, listItem, renderGroupCustomizationViews)
            false
        }
        listItem
    }

    private var propertyCustomizationsListItems = propertyCustomizations.map { customization =>
        val listItem = new ListItem(List(new Anchor(List(
            new Icon(Icon.list),
            new Text(uriToName(customization.uri)))
        )))
        listItem.mouseClicked += { e => onListItemSelected(customization, listItem, renderPropertyCustomizationViews)
            false
        }
        listItem
    }

    private val settingsDiv = new Div(Nil, "span8")
    private val listDiv = new Div(List(new UnorderedList(
        classCustomizationsListItems ++ groupCustomizationListItems ++ propertyCustomizationsListItems, "nav nav-list")),
        "span4 modal-inner-view well no-padding").setAttribute("style", "padding: 8px 0;")

    override val body = List(
        new Div(List(
            new Div(List(
                userCustomizationName,
                new Div(List(appendClassButton, appendGroupClassButton, appendPropertyButton, deleteButton), "btn-group inline-block pull-right")),
                "row-fluid button-row"
            ),
            new Div(List(listDiv, settingsDiv), "row-fluid")),
            "container-fluid"
        ).setAttribute("style", "padding: 0;")
    )

    override def render(parent: html.Element) {
        super.render(parent)
    }

    private def onAppendClass(newClassURI: String): Boolean = {
        if(!classCustomizations.exists(_.uri == newClassURI)) { //if this name does not already exist

            classCustomizationsListItems.foreach(_.removeCssClass("active"))
            groupCustomizationListItems.foreach(_.removeCssClass("active"))
            propertyCustomizationsListItems.foreach(_.removeCssClass("active"))

            settingsDiv.removeAllChildNodes()
            selectedItem = None
            block("Creating class...")

            //create the class
            OntologyCustomizationManager.createClassCustomization(
                userCustomization.id, newClassURI, List[String]()) { ontologyCustomization =>
                unblock()
                val newClass = ontologyCustomization.classCustomizations.last.asInstanceOf[ClassCustomization]
                classCustomizations ++= List(newClass)
                renderDefinedClass(newClass)
            }{ error =>
                unblock()
                //TODO what shall I do if a class customization can not be created??
            }
            true
        } else {
            AlertModal.display("Information", newClassURI + " is already defined.", "", Some(4000))
            false
        }
    }

    private def onAppendGroup(groupName: String): Boolean = {
        if(!groupCustomizations.exists(_.uri == groupName)) { //if this name does not already exist

            groupCustomizationListItems.foreach(_.removeCssClass("active"))
            groupCustomizationListItems.foreach(_.removeCssClass("active"))
            propertyCustomizationsListItems.foreach(_.removeCssClass("active"))

            settingsDiv.removeAllChildNodes()
            selectedItem = None
            block("Creating group class...")

            //create the class
            OntologyCustomizationManager.createGroupCustomization(
                userCustomization.id, groupName, List[String]()) { ontologyCustomization =>
                unblock()
                val newClass = ontologyCustomization.classCustomizations.last.asInstanceOf[ClassCustomization]
                groupCustomizations ++= List(newClass)
                renderDefinedGroup(newClass)
            }{ error =>
                unblock()
                //TODO what shall I do if a class customization can not be created??
            }
            true
        } else {
            AlertModal.display("Information", groupName + " is already defined.", "", Some(4000))
            false
        }
    }

    private def onAppendProperty(newPropertyURI: String): Boolean = {
        if(!propertyCustomizations.exists(_.uri == newPropertyURI)) { //if this name does not already exist

            classCustomizationsListItems.foreach(_.removeCssClass("active"))
            groupCustomizationListItems.foreach(_.removeCssClass("active"))
            propertyCustomizationsListItems.foreach(_.removeCssClass("active"))

            settingsDiv.removeAllChildNodes()
            selectedItem = None
            block("Creating property...")

            if(propertiesContainer.isEmpty) { //must create the classCustomization container for propertiesCustomizations
                OntologyCustomizationManager.createClassCustomization(
                    userCustomization.id, "properties", List[String]()) { ocAddClass =>
                    val newClass = ocAddClass.classCustomizations.last.asInstanceOf[ClassCustomization]
                    classCustomizations ++= List(newClass)

                    addPropertyCall(propertiesContainer.get, newPropertyURI)

                }{ error =>
                    unblock()
                    //TODO what shall I do if a class customization can not be created??
                }
                true
            } else {
                //create the property
                addPropertyCall(propertiesContainer.get, newPropertyURI)
                true
            }
        } else {
            AlertModal.display("Information", "Property " + newPropertyURI + " is already defined.", "", Some(4000))
            false
        }
    }

    private def addPropertyCall(propertiesContainer: ClassCustomization, newPropertyURI: String) {

        OntologyCustomizationManager.createPropertyCustomization(
            userCustomization.id, propertiesContainer.uri, newPropertyURI) { ocAddProperty =>
            unblock()
            val updatedCurrentClassOpt = ocAddProperty.classCustomizations.find(_.uri == propertiesContainer.uri)
            if (updatedCurrentClassOpt.isDefined) {
                val newPropertyCustomizationOpt = updatedCurrentClassOpt.get.propertyCustomizations.find(_.uri == newPropertyURI)
                if(newPropertyCustomizationOpt.isDefined) {
                    propertyCustomizations ++= List(newPropertyCustomizationOpt.get)
                    renderDefinedProperty(newPropertyCustomizationOpt.get, updatedCurrentClassOpt.get)
                }
            }
        }{ error =>
            unblock()
            //TODO what shall I do if a class customization can not be created??
        }
        true
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
            onListItemSelected(definedClass, classListItem, renderClassCustomizationViews)
            false
        }

        classCustomizationsListItems ++= List(classListItem)
        listDiv.removeAllChildNodes()
        val list = new UnorderedList(classCustomizationsListItems ++ groupCustomizationListItems ++ propertyCustomizationsListItems, "nav nav-list")
        list.render(listDiv.htmlElement)
    }

    private def renderDefinedGroup(definedGroup: ClassCustomization) {
        val gorupListItem = new ListItem(List(new Anchor(List(
            new Icon(Icon.ccShare),
            new Text(uriToName(definedGroup.uri.substring(6))))
        )))
        gorupListItem.mouseClicked += { e =>
            onListItemSelected(definedGroup, gorupListItem, renderGroupCustomizationViews)
            false
        }

        groupCustomizationListItems ++= List(gorupListItem)
        listDiv.removeAllChildNodes()
        val list = new UnorderedList(classCustomizationsListItems ++ groupCustomizationListItems ++ propertyCustomizationsListItems, "nav nav-list")
        list.render(listDiv.htmlElement)
    }

    private def renderDefinedProperty(newProperty: PropertyCustomization, updatedClass: ClassCustomization): ListItem = {

        classCustomizations = classCustomizations.filter(_.uri != updatedClass.uri) // remove the old classCustomization (without the new property)
        classCustomizations ++= List(updatedClass) //add the updated classCustomization (with the new property)

        val propertyListItem = new ListItem(List(new Anchor(List(
            new Icon(Icon.list),
            new Text(uriToName(newProperty.uri)))
        )))
        propertyListItem.mouseClicked += { e =>
            onListItemSelected(newProperty, propertyListItem, renderPropertyCustomizationViews)
            false
        }

        propertyCustomizationsListItems ++= List(propertyListItem)
        listDiv.removeAllChildNodes()
        val list = new UnorderedList(classCustomizationsListItems ++ groupCustomizationListItems ++ propertyCustomizationsListItems, "nav nav-list")
        list.render(listDiv.htmlElement)

        propertyListItem
    }

    private def onListItemSelected[A <: Entity](customization: A, listItem: ListItem, renderSettingsDivFn: A => Unit) {
        classCustomizationsListItems.foreach(_.removeCssClass("active"))
        groupCustomizationListItems.foreach(_.removeCssClass("active"))
        propertyCustomizationsListItems.foreach(_.removeCssClass("active"))
        listItem.addCssClass("active")
        selectedItem = Some(CustomizationItem.create(customization))

        settingsDiv.removeAllChildNodes()
        renderSettingsDivFn(customization)
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

        val labels = new InputControl(
            "Labels:",
            new OrderedItemsList("",
                if(classCustomization.labels == null || classCustomization.labels == "") {
                    List(new LabelItem("rdfs:label", false, false), new LabelItem("dcterms:title", false, false),
                        new LabelItem("skos:prefLabel", false, false), new LabelItem("skod:altLabel", false, false),
                        new LabelItem("URI", false, false))
                } else { classCustomization.labelsSplitted }),
            Some("span2")
        )

        fillColor.delayedChanged += { _ =>
            //classCustomization.fillColor = fillColor.field.value.map(_.toString).getOrElse("")
            classFillColorChanged.trigger(new ClassCustomizationEventArgs(fillColor, classCustomization,
                fillColor.field.value.map(_.toString).getOrElse("")))
            customizationChanged.trigger(new OntologyCustomizationEventArgs(userCustomization))
        }
        radius.delayedChanged += { _ =>
            //classCustomization.radius = validateInt(radius.field.value.toString, "radius")
            classRadiusDelayedChanged.trigger(new ClassCustomizationEventArgs(radius, classCustomization,
                radius.field.value.toString))
            customizationChanged.trigger(new OntologyCustomizationEventArgs(userCustomization))
        }
        glyph.field.changed += { _ =>
            //classCustomization.glyph = glyph.field.value.getOrElse("")
            classGlyphChanged.trigger(new ClassCustomizationEventArgs(glyph, classCustomization,
                glyph.field.value.getOrElse("")))
            customizationChanged.trigger(new OntologyCustomizationEventArgs(userCustomization))
        }
        labels.delayedChanged += { _ =>
            classLabelsChanged.trigger(new ClassCustomizationEventArgs(labels, classCustomization,
                labels.field.value.getOrElse("")))
            customizationChanged.trigger(new OntologyCustomizationEventArgs(userCustomization))
        }

        fillColor.render(settingsDiv.htmlElement)
        radius.render(settingsDiv.htmlElement)
        glyph.render(settingsDiv.htmlElement)
        labels.render(settingsDiv.htmlElement)
    }

    private def renderGroupCustomizationViews(classCustomization: ClassCustomization) {
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

        val labels = new InputControl(
            "Labels:",
            new OrderedItemsList("",
                if(classCustomization.labels == null || classCustomization.labels == "") {
                    List(new LabelItem("Group name", false, false))
                } else { classCustomization.labelsSplitted }),
            Some("span2")
        )

        fillColor.delayedChanged += { _ =>
        //classCustomization.fillColor = fillColor.field.value.map(_.toString).getOrElse("")
            classFillColorChanged.trigger(new ClassCustomizationEventArgs(fillColor, classCustomization,
                fillColor.field.value.map(_.toString).getOrElse("")))
            customizationChanged.trigger(new OntologyCustomizationEventArgs(userCustomization))
        }
        radius.delayedChanged += { _ =>
        //classCustomization.radius = validateInt(radius.field.value.toString, "radius")
            classRadiusDelayedChanged.trigger(new ClassCustomizationEventArgs(radius, classCustomization,
                radius.field.value.toString))
            customizationChanged.trigger(new OntologyCustomizationEventArgs(userCustomization))
        }
        glyph.field.changed += { _ =>
        //classCustomization.glyph = glyph.field.value.getOrElse("")
            classGlyphChanged.trigger(new ClassCustomizationEventArgs(glyph, classCustomization,
                glyph.field.value.getOrElse("")))
            customizationChanged.trigger(new OntologyCustomizationEventArgs(userCustomization))
        }
        labels.delayedChanged += { _ =>
            classLabelsChanged.trigger(new ClassCustomizationEventArgs(labels, classCustomization,
                labels.field.value.getOrElse("")))
            customizationChanged.trigger(new OntologyCustomizationEventArgs(userCustomization))
        }

        fillColor.render(settingsDiv.htmlElement)
        radius.render(settingsDiv.htmlElement)
        glyph.render(settingsDiv.htmlElement)
        labels.render(settingsDiv.htmlElement)
    }

    private def renderPropertyCustomizationViews(propertyCustomization: PropertyCustomization) {
        val strokeColor = new InputControl(
            "Stroke color:",
            new ColorInput("strokeColor", Color(propertyCustomization.strokeColor), ""), Some("span2")
        )
        val strokeWidth = new InputControl(
            "Stroke width:",
            new NumericInput("strokeWidth", propertyCustomization.strokeWidth, ""), Some("span2")
        )

        val classCustomization = propertiesContainer
        if(classCustomization.isDefined) {
            strokeColor.delayedChanged += { _ =>
                propertyCustomization.strokeColor = strokeColor.field.value.map(_.toString).getOrElse("")
                propertyStrokeColorChanged.trigger(new PropertyCustomizationEventArgs(strokeColor,
                    classCustomization.get, propertyCustomization, strokeColor.field.value.map(_.toString).getOrElse("")))
            }
            strokeWidth.delayedChanged += { _ =>
                propertyCustomization.strokeWidth = validateInt(strokeWidth.field.value.toString, "strokeWidth")
                propertyStrokeWidthDelayedChanged.trigger(new PropertyCustomizationEventArgs(strokeWidth,
                    classCustomization.get, propertyCustomization, strokeWidth.field.value.toString))
            }
        }

        strokeColor.render(settingsDiv.htmlElement)
        strokeWidth.render(settingsDiv.htmlElement)
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

class CustomizationItem(private var propertyCustomization: Option[PropertyCustomization] = None,
    private var classCustomization: Option[ClassCustomization] = None) {

    def uri = if (propertyCustomization.isDefined) propertyCustomization.get.uri else classCustomization.get.uri
}

object CustomizationItem {
    def create[A <: Entity](cust: A): CustomizationItem = {
        cust match {
            case i: ClassCustomization =>
                new CustomizationItem(None, Some(i))
            case i: PropertyCustomization =>
                new CustomizationItem(Some(i), None)
            case _ => null
        }
    }
}

