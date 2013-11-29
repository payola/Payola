package cz.payola.web.client.views.entity.settings

import cz.payola.common.entities.settings._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.events._
import cz.payola.web.client.presenters.entity.settings._
import cz.payola.web.client.views.elements.form.fields._
import cz.payola.web.client.View
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.elements.lists._
import s2js.adapters.html
import cz.payola.common.visual.Color
import cz.payola.web.client.views.bootstrap.element._
import cz.payola.common.rdf._
import cz.payola.web.shared.managers.CustomizationManager
import cz.payola.common._
import cz.payola.web.client.views.bootstrap.modals.AlertModal
import cz.payola.web.client.views.graph.visual.graph._
import scala.collection.mutable.ListBuffer
import s2js.compiler.javascript
import cz.payola.web.client.models.PrefixApplier

class UserCustomizationEditModal (currentGraphView: Option[GraphView], var userCustomization: UserCustomization,
    onClose: () => Unit, prefixApplier: PrefixApplier)
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
        e.uri != "properties" && !e.isGroupCustomization && !e.isConditionalCustomization).map{
        userClassCust => userClassCust.asInstanceOf[ClassCustomization]
    }

    private var groupCustomizations = userCustomization.classCustomizations.filter(e =>
        e.isGroupCustomization).map{ userClassCust => userClassCust.asInstanceOf[ClassCustomization] }

    private def propertiesContainer = classCustomizations.find(_.uri == "properties")
    
    private var propertyCustomizations = if(propertiesContainer.isDefined) {
        propertiesContainer.get.propertyCustomizations
    } else {
        List[PropertyCustomization]()
    }

    private var conditionalClassCustomizations = userCustomization.classCustomizations.filter(
        _.isConditionalCustomization).map(_.asInstanceOf[ClassCustomization]).sortWith((a, b) => a.orderNumber < b.orderNumber)

    private var selectedItem: Option[CustomizationItem[Entity]] =
        if(classCustomizations.isEmpty) {
            if(groupCustomizations.isEmpty) {
                if(propertyCustomizations.isEmpty) None
                else Some(new CustomizationItem(propertyCustomizations.head))
            } else { Some(new CustomizationItem(groupCustomizations.head))}
        } else {
            Some(new CustomizationItem(classCustomizations.head))
        }

    val customizationChanged = new UnitEvent[UserCustomization, UserCustomizationEventArgs]

    val classFillColorChanged = new UnitEvent[InputControl[_], ClassCustomizationEventArgs[InputControl[_]]]

    val classRadiusDelayedChanged = new UnitEvent[InputControl[_], ClassCustomizationEventArgs[InputControl[_]]]

    val classGlyphChanged = new UnitEvent[InputControl[_], ClassCustomizationEventArgs[InputControl[_]]]

    val propertyStrokeColorChanged = new UnitEvent[InputControl[_], PropertyCustomizationEventArgs[InputControl[_]]]

    val classLabelsChanged = new UnitEvent[InputControl[_], ClassCustomizationEventArgs[InputControl[_]]]

    val classConditionChanged = new UnitEvent[InputControl[_], ClassCustomizationEventArgs[InputControl[_]]]

    val classConditionalOrderChanged = new UnitEvent[InputControl[_], ClassCustomizationEventArgs[InputControl[_]]]

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
        "Class", "Vertices available in the current graph: ", "", onAppendClass, prefixApplier)

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
        "Group", "Groups available in current graph: ", "", onAppendGroup, prefixApplier, "Custom group name")

    appendGroupClassButton.appendButton.mouseClicked += { e =>
        appendGroupClassButton.availableValues = currentGraphGroups.filter{ group => group.getName != null && group.getName != ""
        }.map(_.getName)
        appendGroupClassButton.openPopup()
        false
    }

    val appendPropertyButton = new AppendToUserCustButton(classCustomizations.map(_.uri),
        "Property", "Attributes available in the current graph: ", "", onAppendProperty, prefixApplier)

    appendPropertyButton.appendButton.mouseClicked += { e =>
        val availablePropertyURIs = currentGraphEdges.filter{ edge =>
            //all properties (edges) that are not already in the propertyCustomizations
            !propertyCustomizations.exists(_.uri == edge.uri)
        }.map(_.uri).distinct

        appendPropertyButton.availableValues = availablePropertyURIs
        appendPropertyButton.openPopup()

        false
    }

    val appendConditionalClassButton = new AppendToUserCustButton(classCustomizations.map(_.uri),
        "Global Property", "Attributes available in the current graph: ", "", onAppendConditionalClass, prefixApplier)

    appendConditionalClassButton.appendButton.mouseClicked += { e =>
        val availablePropertyURIs = currentGraphEdges.filter{ edge =>
        //all properties (edges) that are not already in the propertyCustomizations
            !conditionalClassCustomizations.exists(_.uri == edge.uri)
        }.map(_.uri).distinct

        appendConditionalClassButton.availableValues = availablePropertyURIs
        appendConditionalClassButton.openPopup()

        false
    }

    private var classCustomizationsListItems: Seq[ButtonedListItem] = classCustomizations.map { customization =>
        val a = new Anchor(List(new Icon(Icon.tag), new Text(uriToName(customization.uri))))
        val listItem = new ButtonedListItem(Icon.remove, List(a))
        a.mouseClicked += { e =>
            onListItemSelected(customization, listItem, renderClassCustomizationViews)
            false
        }
        listItem.buttonEvent += { e =>
            removeFromCustomization(customization, { userCust =>
                classCustomizationsListItems = classCustomizationsListItems.filter(_ != listItem)
                classCustomizations = classCustomizations.filter(_ != customization)
                customizationChanged.trigger(new UserCustomizationEventArgs(userCust))

                updateListDiv()
            })
            true
        }
        listItem
    }

    private var groupCustomizationListItems: Seq[ButtonedListItem] = groupCustomizations.map { customization =>
        val a = new Anchor(List(new Icon(Icon.tag), new Text(uriToName(customization.uri))))
        val listItem = new ButtonedListItem(Icon.remove, List(a))
        a.mouseClicked += { e =>
            onListItemSelected(customization, listItem, renderGroupCustomizationViews)
            false
        }
        listItem.buttonEvent += { e =>
            removeFromCustomization(customization, { userCust =>
                groupCustomizationListItems = groupCustomizationListItems.filter(_ != listItem)
                groupCustomizations = groupCustomizations.filter(_ != customization)
                customizationChanged.trigger(new UserCustomizationEventArgs(userCust))

                updateListDiv()
            })
            false
        }
        listItem
    }

    private var conditionalClassCustomizationListItems: Seq[ButtonedListItem] = conditionalClassCustomizations.map { customization =>
        val a = new Anchor(List(new Icon(Icon.tag), new Text(uriToName(customization.uri))))
        val listItem = new ButtonedListItem(Icon.remove, List(a)).setAttribute("orderNumber", ""+customization.orderNumber)
        a.mouseClicked += { e =>
            onConditionalListItemSelected(customization, listItem, renderConditionalClassCustomizationViews)
            false
        }
        listItem.buttonEvent += { e =>
            removeFromCustomization(customization, { userCust =>
                conditionalClassCustomizationListItems = conditionalClassCustomizationListItems.filter(_ != listItem)
                conditionalClassCustomizations = conditionalClassCustomizations.filter(_ != customization)
                customizationChanged.trigger(new UserCustomizationEventArgs(userCust))

                updateConditionalClassListDiv()
            })
            false
        }
        listItem
    }

    private var propertyCustomizationsListItems: Seq[ButtonedListItem] = propertyCustomizations.map { customization =>
        val a = new Anchor(List(new Icon(Icon.tag), new Text(uriToName(customization.uri))))
        val listItem = new ButtonedListItem(Icon.remove, List(a))
        a.mouseClicked += { e =>
            onListItemSelected(customization, listItem, renderPropertyCustomizationViews)
            false
        }
        listItem.buttonEvent += { e =>
            removeFromCustomization(customization, { userCust =>
                propertyCustomizationsListItems = propertyCustomizationsListItems.filter(_ != listItem)
                propertyCustomizations = propertyCustomizations.filter(_ != customization)
                customizationChanged.trigger(new UserCustomizationEventArgs(userCust))

                updateListDiv()
            })
            false
        }
        listItem
    }

    private val settingsDiv = new Div(Nil, "span8").setAttribute("style","width:100%;").setAttribute("rowspan", "2")
    private val conditionalClassListDiv = new Div(List(new UnorderedList(
        conditionalClassCustomizationListItems, "nav-deep nav-deep-list").setAttribute("id", "sortableConditionalClasses")),
        "span4 modal-inner-view well no-padding").setAttribute("style", "padding: 8px 0; width:100%; max-width: 260px;")

    private val listDiv = new Div(List(new UnorderedList(
        classCustomizationsListItems ++ groupCustomizationListItems ++ propertyCustomizationsListItems, "nav-deep nav-deep-list")),
        "span4 modal-inner-view well no-padding").setAttribute("style", "padding: 8px 0; width:100%; max-width: 260px;")

    override val body = List(
        new Div(List(                            //TODO make it  nice and move the styles to *.css
            new Div(List(
                userCustomizationName,
                new Div(List(appendClassButton, appendGroupClassButton, appendPropertyButton, appendConditionalClassButton,
                    deleteButton), "btn-group inline-block pull-right")),
                "row-fluid button-row"
            ),
            new Table(List(new TableRow(List(
                new TableCell(List(
                    new Table(List(new TableRow(List(new TableCell(List(listDiv)))),
                        new TableRow(List(new TableCell(List(conditionalClassListDiv))))),
                        "row-fluid").setAttribute("style", "height: 100%;"))
                ).setAttribute("style", "vertical-align: top;"),
                new TableCell(List(settingsDiv), "span8 row-fluid"))))
                , "row-fluid")), "container-fluid"
        ).setAttribute("style", "padding: 0;")
    )

    override def render(parent: html.Element) {
        super.render(parent)
        initSortable()
    }

    private def onAppendClass(newClassURI: String): Boolean = {
        if(!classCustomizations.exists(_.getUri == newClassURI)) { //if this name does not already exist

            deactivateAll()

            block("Creating class...")

            //create the class
            CustomizationManager.createClassCustomization(
                userCustomization.id, newClassURI, List[String]()) { userCustomization =>
                unblock()
                val newClass = userCustomization.classCustomizations.find(_.getUri == newClassURI).get.asInstanceOf[ClassCustomization]
                classCustomizations ++= List(newClass)
                renderDefinedClass(newClass)
                customizationChanged.trigger(new UserCustomizationEventArgs(userCustomization))
            }{ error =>
                unblock()
                AlertModal.display("Error", "Failed to create class customization.")
            }
            true
        } else {
            AlertModal.display("Information", newClassURI + " is already defined.", "", Some(4000))
            false
        }
    }

    private def onAppendGroup(groupName: String): Boolean = {
        if(!groupCustomizations.exists(_.getUri == groupName)) { //if this name does not already exist

            deactivateAll()

            block("Creating group class...")

            //create the class
            CustomizationManager.createGroupCustomization(userCustomization.id, groupName) { userCustomization =>
                unblock()
                val newClass = userCustomization.classCustomizations.find(_.getUri == groupName).get.asInstanceOf[ClassCustomization]
                groupCustomizations ++= List(newClass)
                renderDefinedGroup(newClass)
                customizationChanged.trigger(new UserCustomizationEventArgs(userCustomization))
            }{ error =>
                AlertModal.display("Error", "Failed to create group customization.")
            }
            true
        } else {
            AlertModal.display("Information", groupName + " is already defined.", "", Some(4000))
            false
        }
    }

    private def onAppendConditionalClass(newClassURI: String): Boolean = {

        //conditionalClassCustomization may contain multiple customizations with one uri
        deactivateAll()

        block("Creating global property...")

        //create the class
        CustomizationManager.createConditionalCustomization(userCustomization.id, newClassURI) { updatedCust =>
            unblock()
            val newClass = updatedCust.classCustomizations.find(_.getUri == newClassURI).get.asInstanceOf[ClassCustomization]
            conditionalClassCustomizations ++= List(newClass)
            renderDefinedConditionalClass(newClass)
            customizationChanged.trigger(new UserCustomizationEventArgs(updatedCust))
        }{ error =>
            unblock()
            AlertModal.display("Error", "Failed to create conditional class customization.")
        }
        true
    }

    private def onAppendProperty(newPropertyURI: String): Boolean = {
        if(!propertyCustomizations.exists(_.uri == newPropertyURI)) { //if this name does not already exist

            deactivateAll()

            block("Creating property...")

            if(propertiesContainer.isEmpty) { //must create the classCustomization container for propertiesCustomizations
                CustomizationManager.createClassCustomization(
                    userCustomization.id, "properties", List[String]()) { ocAddClass =>
                    val newClass = ocAddClass.classCustomizations.find(_.getUri == "properties").get.asInstanceOf[ClassCustomization]
                    classCustomizations ++= List(newClass)

                    addPropertyCall(propertiesContainer.get, newPropertyURI)
                    customizationChanged.trigger(new UserCustomizationEventArgs(userCustomization))
                }{ error =>
                    unblock()
                    AlertModal.display("Error", "Failed to create property customization.")
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

        CustomizationManager.createPropertyCustomization(
            userCustomization.id, propertiesContainer.uri, newPropertyURI) { ocAddProperty =>
            unblock()
            val updatedCurrentClassOpt = ocAddProperty.classCustomizations.find(_.uri == propertiesContainer.uri)
            if (updatedCurrentClassOpt.isDefined) {
                val newPropertyCustomizationOpt = updatedCurrentClassOpt.get.propertyCustomizations.find(_.uri == newPropertyURI)
                if(newPropertyCustomizationOpt.isDefined) {
                    propertyCustomizations ++= List(newPropertyCustomizationOpt.get)
                    renderDefinedProperty(newPropertyCustomizationOpt.get, updatedCurrentClassOpt.get)
                }
            } else {
                unblock()
                AlertModal.display("Error", "Failed to create property customization container.")
            }
        }{ error =>
            unblock()
            AlertModal.display("Error", "Failed to create property customization.")
        }
        true
    }

    /**
     * Adds a created ClassCustomization to the list of defined ClassCustomizations (classesDiv);
     * reaction to pressed button of a undefined ClassCustomization in the available ClassCustomizations list
     * @param definedClass new ClassCustomization to add
     */
    private def renderDefinedClass(definedClass: ClassCustomization) {
        val a = new Anchor(List(new Icon(Icon.tag), new Text(uriToName(definedClass.uri))))
        val classListItem = new ButtonedListItem(Icon.remove, List(a))
        a.mouseClicked += { e =>
            onListItemSelected(definedClass, classListItem, renderClassCustomizationViews)
            false
        }
        classListItem.buttonEvent += { e =>
            removeFromCustomization(definedClass, { userCust =>
                classCustomizationsListItems = classCustomizationsListItems.filter(_ != classListItem)
                classCustomizations = classCustomizations.filter(_ != definedClass)
                customizationChanged.trigger(new UserCustomizationEventArgs(userCust))

                updateListDiv()
            })
            false
        }

        classCustomizationsListItems ++= List(classListItem)
        updateListDiv()
    }

    private def renderDefinedConditionalClass(definedClass: ClassCustomization) {
        val a = new Anchor(List(new Icon(Icon.tag), new Text(uriToName(definedClass.uri))))
        val classListItem = new ButtonedListItem(Icon.remove, List(a)).setAttribute("orderNumber", ""+definedClass.orderNumber)
        a.mouseClicked += { e =>
            onListItemSelected(definedClass, classListItem, renderConditionalClassCustomizationViews)
            false
        }
        classListItem.buttonEvent += { e =>
            removeFromCustomization(definedClass, {userCust =>
                conditionalClassCustomizationListItems = conditionalClassCustomizationListItems.filter(_ != classListItem)
                conditionalClassCustomizations = conditionalClassCustomizations.filter(_ != definedClass)
                customizationChanged.trigger(new UserCustomizationEventArgs(userCust))

                updateConditionalClassListDiv()
            })
            false
        }

        conditionalClassCustomizationListItems ++= List(classListItem)
        //TODO call jquery update
        updateConditionalClassListDiv()
    }

    private def renderDefinedGroup(definedGroup: ClassCustomization) {
        val a = new Anchor(List(new Icon(Icon.tag), new Text(uriToName(definedGroup.uri))))
        val groupListItem = new ButtonedListItem(Icon.remove, List(a))
        a.mouseClicked += { e =>
            onListItemSelected(definedGroup, groupListItem, renderGroupCustomizationViews)
            false
        }
        groupListItem.buttonEvent += { e =>
            removeFromCustomization(definedGroup, { userCust =>
                groupCustomizationListItems = groupCustomizationListItems.filter(_ != groupListItem)
                groupCustomizations = groupCustomizations.filter(_ != definedGroup)
                customizationChanged.trigger(new UserCustomizationEventArgs(userCust))

                updateListDiv()
            })
            false
        }

        groupCustomizationListItems ++= List(groupListItem)
        updateListDiv()
    }

    private def renderDefinedProperty(newProperty: PropertyCustomization, updatedClass: ClassCustomization): ButtonedListItem = {

        classCustomizations = classCustomizations.filter(_.uri != updatedClass.uri) // remove the old classCustomization (without the new property)
        classCustomizations ++= List(updatedClass) //add the updated classCustomization (with the new property)

        val a = new Anchor(List(new Icon(Icon.tag), new Text(uriToName(newProperty.uri))))
        val propertyListItem = new ButtonedListItem(Icon.remove, List(a))
        a.mouseClicked += { e =>
            onListItemSelected(newProperty, propertyListItem, renderPropertyCustomizationViews)
            false
        }
        propertyListItem.buttonEvent += { e =>
            removeFromCustomization(newProperty, { userCust =>
                propertyCustomizationsListItems = propertyCustomizationsListItems.filter(_ != propertyListItem)
                propertyCustomizations = propertyCustomizations.filter(_ != newProperty)
                customizationChanged.trigger(new UserCustomizationEventArgs(userCust))

                updateListDiv()
            })
            false
        }

        propertyCustomizationsListItems ++= List(propertyListItem)
        updateListDiv()
        propertyListItem
    }

    private def updateListDiv() {
        listDiv.removeAllChildNodes()
        val list = new UnorderedList(classCustomizationsListItems ++ groupCustomizationListItems ++ propertyCustomizationsListItems, "nav-deep nav-deep-list")
        list.render(listDiv.htmlElement)
    }

    private def updateConditionalClassListDiv() {
        disableSortable()
        conditionalClassListDiv.removeAllChildNodes()
        val list = new UnorderedList(conditionalClassCustomizationListItems, "nav-deep nav-deep-list").setAttribute("id", "sortableConditionalClasses")
        list.render(conditionalClassListDiv.htmlElement)
        initSortable()
    }

    private def onListItemSelected[A <: Entity](customization: A, listItem: ButtonedListItem, renderSettingsDivFn: A => Unit) {
        deactivateAll()
        listItem.addCssClass("active")
        selectedItem = Some(new CustomizationItem(customization))

        settingsDiv.removeAllChildNodes()
        renderSettingsDivFn(customization)
    }

    private def onConditionalListItemSelected[A <: Entity](customization: A, listItem: ButtonedListItem, renderSettingsDivFn: A => Unit) {
        deactivateAll()
        listItem.addCssClass("active")
        selectedItem = Some(new CustomizationItem(customization))

        settingsDiv.removeAllChildNodes()
        renderSettingsDivFn(customization)
    }

    private def deactivateAll() {
        conditionalClassCustomizationListItems.foreach(_.removeCssClass("active"))
        classCustomizationsListItems.foreach(_.removeCssClass("active"))
        groupCustomizationListItems.foreach(_.removeCssClass("active"))
        propertyCustomizationsListItems.foreach(_.removeCssClass("active"))

        settingsDiv.removeAllChildNodes()
        selectedItem = None
    }

    private def uriToName(uri: String): String = {
        val prefxedUri = prefixApplier.applyPrefix(uri)
        val nameParts = uri.split("#")
        if(prefxedUri == uri) {if (nameParts.length > 1) { nameParts(1) } else { uri } } else prefxedUri
    }

    def renderClassCustomizationViews(classCustomization: ClassCustomization) {
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
            customizationChanged.trigger(new UserCustomizationEventArgs(userCustomization))
        }
        radius.delayedChanged += { _ =>
        //classCustomization.radius = validateInt(radius.field.value.toString, "radius")
            classRadiusDelayedChanged.trigger(new ClassCustomizationEventArgs(radius, classCustomization,
                radius.field.value.toString))
            customizationChanged.trigger(new UserCustomizationEventArgs(userCustomization))
        }
        glyph.field.changed += { _ =>
        //classCustomization.glyph = glyph.field.value.getOrElse("")
            classGlyphChanged.trigger(new ClassCustomizationEventArgs(glyph, classCustomization,
                glyph.field.value.getOrElse("")))
            customizationChanged.trigger(new UserCustomizationEventArgs(userCustomization))
        }
        labels.delayedChanged += { _ =>
            classLabelsChanged.trigger(new ClassCustomizationEventArgs(labels, classCustomization,
                labels.field.value.getOrElse("")))
            customizationChanged.trigger(new UserCustomizationEventArgs(userCustomization))
        }

        fillColor.render(settingsDiv.htmlElement)
        radius.render(settingsDiv.htmlElement)
        glyph.render(settingsDiv.htmlElement)
        labels.render(settingsDiv.htmlElement)
    }

    def renderConditionalClassCustomizationViews(conClassCustomization: ClassCustomization) {

        val conditionValueField = new InputControl(
            "Property value:",
            new ConditionTextInput(
                currentGraphEdges.filter(_.uri == conClassCustomization.getUri).map(_.destination.toString).distinct,
                "Select value", conClassCustomization.conditionalValue, prefixApplier),
            Some("span2")
        )
        val labelField = new InputControl(
            "Custom label:",
            new TextInput("customLabel",
                if(conClassCustomization.labels == null || conClassCustomization.labels == "") { "" }
                else { conClassCustomization.labelsSplitted(0).value }), Some("span2")
        )
        val fillColor = new InputControl(
            "Fill color:",
            new ColorInput("fillColor", Color(conClassCustomization.fillColor), ""), Some("span2")
        )
        val radius = new InputControl(
            "Radius:",
            new NumericInput("radius", conClassCustomization.radius, "")   , Some("span2")
        )
        val glyph = new InputControl(
            "Glyph:",
            new GlyphInput("glyph", Some(conClassCustomization.glyph), "")  , Some("span2")
        )

        conditionValueField.delayedChanged += { _ =>
            classConditionChanged.trigger(new ClassCustomizationEventArgs(conditionValueField, conClassCustomization,
                conditionValueField.field.value))
            customizationChanged.trigger(new UserCustomizationEventArgs(userCustomization))
        }
        labelField.delayedChanged += { _ =>
            classLabelsChanged.trigger(new ClassCustomizationEventArgs(labelField, conClassCustomization,
            "TU-"+ labelField.field.value))
            customizationChanged.trigger(new UserCustomizationEventArgs(userCustomization))
        }
        fillColor.delayedChanged += { _ =>
        //classCustomization.fillColor = fillColor.field.value.map(_.toString).getOrElse("")
            classFillColorChanged.trigger(new ClassCustomizationEventArgs(fillColor, conClassCustomization,
                fillColor.field.value.map(_.toString).getOrElse("")))
            customizationChanged.trigger(new UserCustomizationEventArgs(userCustomization))
        }
        radius.delayedChanged += { _ =>
        //classCustomization.radius = validateInt(radius.field.value.toString, "radius")
            classRadiusDelayedChanged.trigger(new ClassCustomizationEventArgs(radius, conClassCustomization,
                radius.field.value.toString))
            customizationChanged.trigger(new UserCustomizationEventArgs(userCustomization))
        }
        glyph.field.changed += { _ =>
        //classCustomization.glyph = glyph.field.value.getOrElse("")
            classGlyphChanged.trigger(new ClassCustomizationEventArgs(glyph, conClassCustomization,
                glyph.field.value.getOrElse("")))
            customizationChanged.trigger(new UserCustomizationEventArgs(userCustomization))
        }

        val conditionalClassSwitch = new InputControl(
                "Use property value",
                new CheckBox("useValue",
                    conClassCustomization.labels != null
                        && conClassCustomization.labels != ""
                        && !conClassCustomization.labelsSplitted(0).userDefined, "UseValue"),
                None
            )
        if(conClassCustomization.labels != null && conClassCustomization.labels != ""
            && !conClassCustomization.labelsSplitted(0).userDefined) {
            labelField.field.disable()
        }


        conditionalClassSwitch.delayedChanged += { e =>
            conditionalClassSwitch.field.value match {
                case true => {
                    labelField.field.disable()
                    classLabelsChanged.trigger(new ClassCustomizationEventArgs(labelField, conClassCustomization, "T-"))
                }
                case _ => {
                    labelField.field.enable()
                    classLabelsChanged.trigger(new ClassCustomizationEventArgs(labelField, conClassCustomization,
                        "TU-" + labelField.field.value))
                }
            }
            customizationChanged.trigger(new UserCustomizationEventArgs(userCustomization))
        }



        conditionalClassSwitch.render(settingsDiv.htmlElement)
        conditionValueField.render(settingsDiv.htmlElement)
        labelField.render(settingsDiv.htmlElement)
        fillColor.render(settingsDiv.htmlElement)
        radius.render(settingsDiv.htmlElement)
        glyph.render(settingsDiv.htmlElement)
    }

    def renderGroupCustomizationViews(classCustomization: ClassCustomization) {
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
            customizationChanged.trigger(new UserCustomizationEventArgs(userCustomization))
        }
        radius.delayedChanged += { _ =>
        //classCustomization.radius = validateInt(radius.field.value.toString, "radius")
            classRadiusDelayedChanged.trigger(new ClassCustomizationEventArgs(radius, classCustomization,
                radius.field.value.toString))
            customizationChanged.trigger(new UserCustomizationEventArgs(userCustomization))
        }
        glyph.field.changed += { _ =>
        //classCustomization.glyph = glyph.field.value.getOrElse("")
            classGlyphChanged.trigger(new ClassCustomizationEventArgs(glyph, classCustomization,
                glyph.field.value.getOrElse("")))
            customizationChanged.trigger(new UserCustomizationEventArgs(userCustomization))
        }
        labels.delayedChanged += { _ =>
            classLabelsChanged.trigger(new ClassCustomizationEventArgs(labels, classCustomization,
                labels.field.value.getOrElse("")))
            customizationChanged.trigger(new UserCustomizationEventArgs(userCustomization))
        }

        fillColor.render(settingsDiv.htmlElement)
        radius.render(settingsDiv.htmlElement)
        glyph.render(settingsDiv.htmlElement)
        labels.render(settingsDiv.htmlElement)
    }

    def renderPropertyCustomizationViews(propertyCustomization: PropertyCustomization) {
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

    private def validateInt(value: String, field: String): Int = {
        if (!value.matches("^[0-9]+$")){ throw new ValidationException(field, "Value can contain only digits") }

        try {
            value.toInt
        } catch { case t: Throwable => throw new ValidationException(field, "Value is out of range") }
    }

    @javascript (
        """
          $( "#sortableConditionalClasses" ).sortable();
          $( "#sortableConditionalClasses" ).disableSelection();
          $( "#sortableConditionalClasses" ).on( "sortupdate", function( event, ui ) {
            var customizationsOrder = scala.collection.mutable.ListBuffer.get().$apply();
            var listElements = document.getElementById("sortableConditionalClasses").children;
            for(var i = 0; i < listElements.length; ++i) {
                customizationsOrder.$plus$eq(listElements[i].attributes["orderNumber"].value)
            }
            self.updateConditionalClassCustomizationsOrder(customizationsOrder.toList());
          } );
        """)
    private def initSortable(){}

    @javascript (
        """
          $( "#sortableConditionalClasses" ).sortable('cancel');
        """)
    private def disableSortable(){}

    private def updateConditionalClassCustomizationsOrder(order: List[Int]) {

        //update conditionalClasses list
        val reorderedList = new ListBuffer[ClassCustomization]()
        val reorderedItemsList = new ListBuffer[ButtonedListItem]()


        order.foreach{ orderNumber =>
            val foundCust = conditionalClassCustomizations.filter(_.orderNumber == orderNumber)
            val foundListItem = conditionalClassCustomizationListItems.filter(_.getAttribute("orderNumber") == orderNumber)

            foundCust.foreach{ orderedCust =>
                //orderedCust.orderNumber = positionNumber
                reorderedList += orderedCust
            }
            foundListItem.foreach{ listItem =>
                reorderedItemsList += listItem
            }
        }

        var positionNumber = 0
        reorderedList.foreach{ item =>
            classConditionalOrderChanged.trigger(
                new ClassCustomizationEventArgs(null, item, ""+positionNumber))
            positionNumber += 1
        }

        positionNumber = 0
        reorderedItemsList.foreach{ item =>
            item.setAttribute("orderNumber", ""+positionNumber)
            positionNumber += 1
        }

        conditionalClassCustomizations = reorderedList.toList

        customizationChanged.trigger(new UserCustomizationEventArgs(userCustomization))
    }

    private def removeFromCustomization(customization: Entity, success: UserCustomization => Unit) {
        deactivateAll()

        customization match {
            case cc: ClassCustomization =>
                block("Deleting class...")
                CustomizationManager.deleteClassCustomization(userCustomization.id, cc.id)
                { userCust =>
                    unblock()
                    success(userCust)
                }
                { error =>
                    unblock()
                    AlertModal.display("Error", "Failed to remove class customization.")
                }
            case pc: PropertyCustomization =>
                block("Deleting property...")
                CustomizationManager.deletePropertyCustomization(userCustomization.id, pc.id)
                { userCust =>
                    unblock()
                    success(userCust)
                }
                { error =>
                    unblock()
                    AlertModal.display("Error", "Failed to remove property customization.")
                }
        }


    }
}

class CustomizationItem[A <: Entity](private var customizationEntity: A) {

    def uri = customizationEntity match {
        case i: ClassCustomization => i.uri
        case i: PropertyCustomization => i.uri
        case _ => null
    }
}
