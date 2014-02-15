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
import cz.payola.web.client.presenters.entity.ShareButtonPresenter

class UserCustomizationEditModal (currentGraphView: Option[GraphView], var userCustomization: UserCustomization,
    onClose: () => Unit, prefixApplier: PrefixApplier)
    extends Modal("Edit user customization", Nil, Some("Done"), None, true, "large-modal")
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

    private def propertiesContainer = userCustomization.classCustomizations.find(_.uri == "properties")
    
    private var propertyCustomizations = if(propertiesContainer.isDefined) {
        propertiesContainer.get.propertyCustomizations
    } else {
        List[PropertyCustomization]()
    }

    private var conditionalClassCustomizations = userCustomization.classCustomizations.filter(
        _.isConditionalCustomization).map(_.asInstanceOf[ClassCustomization]).sortWith((a, b) => a.orderNumber < b.orderNumber)

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
        new TextInput("name", userCustomization.name, "", ""),
        Some("col-lg-2"), Some("col-lg-10")
    )

    saveButton.mouseClicked += { e =>
        onClose()
        true
    }

    val deleteButton = new Button(new Text("Delete"), "btn-danger", new Icon(Icon.remove))

    val shareButtonViewSpace = new Span(Nil)

    val appendClassButton = new AppendToUserCustButton(
        currentGraphVertices.filter{ vertex =>
            vertex match {
                case i: IdentifiedVertex => ! classCustomizations.exists{ classCust => i.uri == classCust.uri }
                case _ => false
            }
        }.map{vertex => vertex.asInstanceOf[IdentifiedVertex].uri},
        "Node", "Add entity visual customization", "Node customization", "Nodes available in the current graph: ", "",
        onAppendClass, prefixApplier)

    appendClassButton.appendButton.mouseClicked += { e =>
        appendClassButton.openPopup()
        false
    }

    val appendGroupClassButton = new AppendToUserCustButton(
        currentGraphGroups.filter{ group =>
            group.getName != null && group.getName != "" && !groupCustomizations.exists(_.uri == group.getName) }.map(_.getName),
        "Group", "Add group visual customization", "Group customization", "Groups available in current graph: ", "",
        onAppendGroup, prefixApplier, "Custom group name")

    appendGroupClassButton.appendButton.mouseClicked += { e =>
        appendGroupClassButton.openPopup()
        false
    }

    val appendPropertyButton = new AppendToUserCustButton(
        currentGraphEdges.filter{ edge =>
        //all properties (edges) that are not already in the propertyCustomizations
            !propertyCustomizations.exists(_.uri == edge.uri)
        }.map(_.uri).distinct,
        "Edge", "Add property visual customization", "Edge customization", "Edges available in the current graph: ", "",
        onAppendProperty, prefixApplier)

    appendPropertyButton.appendButton.mouseClicked += { e =>
        appendPropertyButton.openPopup()
        false
    }

    val appendConditionalClassButton = new AppendToUserCustButton(
        currentGraphEdges.filter{ edge =>
        //all properties (edges) that are not already in the propertyCustomizations
            !conditionalClassCustomizations.exists(_.uri == edge.uri)
        }.map(_.uri).distinct,
        "Node based on edge", "Add class visual customization", "Node based on edge customization",
        "Edges available in the current graph: ", "dropdown-toggle", onAppendConditionalClass, prefixApplier)

    appendConditionalClassButton.appendButton.mouseClicked += { e =>
        appendConditionalClassButton.openPopup()
        false
    }

    private var classCustomizationsListItems: Seq[ButtonedListItem] = classCustomizations.map { customization =>
        val anchor = new Anchor(List(new Icon(Icon.tag), new Text(uriToName(customization.uri))))
        val listItem = new ButtonedListItem(Icon.remove, List(anchor))
        anchor.mouseClicked += { e =>
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
        val anchor = new Anchor(List(new Icon(Icon.ccShare), new Text(uriToName(customization.getUri))))
        val listItem = new ButtonedListItem(Icon.remove, List(anchor))
        anchor.mouseClicked += { e =>
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

    //order changing button in conditionalClassCustomizations list
    private val conditionalClassOrderReverse = new Anchor(List(new Icon(Icon.arrow_down), new Text("Reverse order")))
    private val conditionalClassOrderReverseItem = new ButtonedListItem(
        "", List(conditionalClassOrderReverse), false, "").setAttribute("title", "Applyed from top to bottom")
    conditionalClassOrderReverse.mouseClicked += { e =>
        if(conditionalClassCustomizationListItems.size != 1) {
            val numbers = new ListBuffer[Int]()
            var index: Int = conditionalClassCustomizations.size - 1
            conditionalClassCustomizations.foreach{a => numbers += index; index -= 1} //s2js can not handle scala's range

            reverseConditionalClassCustomizationsOrder()

            updateConditionalClassCustomizationsOrder(numbers.toList)
        }
        false
    }

    private var conditionalClassCustomizationListItems: Seq[ButtonedListItem] =
        List(conditionalClassOrderReverseItem) ++ conditionalClassCustomizations.map { customization =>
            val anchor = new Anchor(List(new Icon(Icon.tag), new Text(uriToName(customization.getUri))))
            val listItem = new ButtonedListItem(Icon.remove, List(anchor), true, "sortedConditionalClass").setAttribute(
                "orderNumber", ""+customization.orderNumber)
            anchor.mouseClicked += { e =>
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
        val anchor = new Anchor(List(new Icon(Icon.list), new Text(uriToName(customization.uri))))
        val listItem = new ButtonedListItem(Icon.remove, List(anchor))
        anchor.mouseClicked += { e =>
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

    private val settingsDiv = new Div(Nil, "col-lg-8").setAttribute("style","width:100%;").setAttribute("rowspan", "2")
    private val conditionalClassListDiv = new Div(List(new UnorderedList(
        conditionalClassCustomizationListItems, "nav-deep nav-deep-list").setAttribute("id", "sortableConditionalClasses")),
        "col-lg-4 well no-padding").setAttribute("style", "padding: 8px 0; width:100%; max-width: 260px; min-height: 150px;" +
        "")

    private val listDiv = new Div(List(new UnorderedList(
        classCustomizationsListItems ++ groupCustomizationListItems ++ propertyCustomizationsListItems, "nav-deep nav-deep-list")),
        "col-lg-4 well no-padding").setAttribute("style", "padding: 8px 0; width:100%; max-width: 260px; min-height: 150px;")

    override val body = List(
        new Div(List(
            new Div(List(
                userCustomizationName,
                new Div(List(deleteButton), "btn-group inline-block pull-right"),
                new Div(List(shareButtonViewSpace), "btn-group inline-block pull-right"),
                new Div(List(appendClassButton, appendGroupClassButton, appendPropertyButton, appendConditionalClassButton),
                    "btn-group inline-block pull-right")),
                "row button-row"),

            new Table(List(new TableRow(List(
                new TableCell(List(
                    new Table(List(new TableRow(List(new TableCell(List(listDiv)))),
                        new TableRow(List(new TableCell(List(conditionalClassListDiv))))),
                        "row").setAttribute("style", "height: 100%;"))
                ).setAttribute("style", "vertical-align: top;"),
                new TableCell(List(settingsDiv), "col-lg-8 row").setAttribute("style", "width: 100%; padding-top: 20px;"))))
                , "row")), "container-fluid"
        ).setAttribute("style", "padding: 0;")
    )

    override def render(parent: html.Element) {
        super.render(parent)
        initSortable()
    }

    private def onAppendClass(newClassURI: String) {
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
        } else {
            AlertModal.display("Information", newClassURI + " is already defined.", "", Some(4000))
        }
    }

    private def onAppendGroup(groupName: String) {
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
        } else {
            AlertModal.display("Information", groupName + " is already defined.", "", Some(4000))
        }
    }

    private def onAppendConditionalClass(newClassURI: String) {

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
    }

    private def onAppendProperty(newPropertyURI: String) {
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
            } else {
                //create the property
                addPropertyCall(propertiesContainer.get, newPropertyURI)
            }
        } else {
            AlertModal.display("Information", "Property " + newPropertyURI + " is already defined.", "", Some(4000))
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
    }

    /**
     * Adds a created ClassCustomization to the list of defined ClassCustomizations (classesDiv);
     * reaction to pressed button of a undefined ClassCustomization in the available ClassCustomizations list
     * @param definedClass new ClassCustomization to add
     */
    private def renderDefinedClass(definedClass: ClassCustomization) {
        val anchor = new Anchor(List(new Icon(Icon.tag), new Text(uriToName(definedClass.uri))))
        val classListItem = new ButtonedListItem(Icon.remove, List(anchor))
        anchor.mouseClicked += { e =>
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
        val anchor = new Anchor(List(new Icon(Icon.tag), new Text(uriToName(definedClass.getUri))))
        val classListItem = new ButtonedListItem(Icon.remove, List(anchor), true, "sortedConditionalClass").setAttribute(
            "orderNumber", ""+definedClass.orderNumber)
        anchor.mouseClicked += { e =>
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
        updateConditionalClassListDiv()
    }

    private def renderDefinedGroup(definedGroup: ClassCustomization) {
        val anchor = new Anchor(List(new Icon(Icon.ccShare), new Text(uriToName(definedGroup.getUri))))
        val groupListItem = new ButtonedListItem(Icon.remove, List(anchor))
        anchor.mouseClicked += { e =>
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

        val anchor = new Anchor(List(new Icon(Icon.list), new Text(uriToName(newProperty.uri))))
        val propertyListItem = new ButtonedListItem(Icon.remove, List(anchor))
        anchor.mouseClicked += { e =>
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

        settingsDiv.removeAllChildNodes()
        renderSettingsDivFn(customization)
    }

    private def onConditionalListItemSelected[A <: Entity](customization: A, listItem: ButtonedListItem, renderSettingsDivFn: A => Unit) {
        deactivateAll()
        listItem.addCssClass("active")

        settingsDiv.removeAllChildNodes()
        renderSettingsDivFn(customization)
    }

    private def deactivateAll() {
        conditionalClassCustomizationListItems.foreach(_.removeCssClass("active"))
        classCustomizationsListItems.foreach(_.removeCssClass("active"))
        groupCustomizationListItems.foreach(_.removeCssClass("active"))
        propertyCustomizationsListItems.foreach(_.removeCssClass("active"))

        settingsDiv.removeAllChildNodes()
    }

    private def uriToName(uri: String): String = {
        val prefxedUri = prefixApplier.applyPrefix(uri)
        val nameParts = uri.split("#")
        if(prefxedUri == uri) {if (nameParts.length > 1) { nameParts(1) } else { uri } } else prefxedUri
    }

    def renderClassCustomizationViews(classCustomization: ClassCustomization) {
        val fillColor = new InputControl(
            "Fill color:",
            new ColorInput("fillColor", Color(classCustomization.fillColor), ""), Some("col-lg-2"), Some("col-lg-10")
        )
        val radius = new InputControl(
            "Radius:",
            new NumericInput("radius", classCustomization.radius, "")   , Some("col-lg-2"), Some("col-lg-10")
        )
        val glyph = new InputControl(
            "Glyph:",
            new GlyphInput("glyph", Some(classCustomization.glyph), "")  , Some("col-lg-2") , Some("col-lg-10")
        )

        val labels = new InputControl(
            "Labels:",
            new OrderedItemsList("",
                if(classCustomization.labels == null || classCustomization.labels == "") {
                    List(new LabelItem("rdfs:label", false, false), new LabelItem("dcterms:title", false, false),
                        new LabelItem("skos:prefLabel", false, false), new LabelItem("skod:altLabel", false, false),
                        new LabelItem("URI", false, false))
                } else { classCustomization.labelsSplitted }),
            Some("col-lg-2"), Some("col-lg-10")
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
                "Select value", conClassCustomization.conditionalValue, prefixApplier, "", "margin: 0px"), None, None
        )
        val labelField = new InputControl(
            "Custom label:",
            new TextInput("customLabel",
                if(conClassCustomization.labels == null || conClassCustomization.labels == "") { "" }
                else { conClassCustomization.labelsSplitted(0).value }), Some("col-lg-2"), Some("col-lg-10")
        )
        val fillColor = new InputControl(
            "Fill color:",
            new ColorInput("fillColor", Color(conClassCustomization.fillColor), ""), Some("col-lg-2"), Some("col-lg-10")
        )
        val radius = new InputControl(
            "Radius:",
            new NumericInput("radius", conClassCustomization.radius, "")   , Some("col-lg-2"), Some("col-lg-10")
        )
        val glyph = new InputControl(
            "Glyph:",
            new GlyphInput("glyph", Some(conClassCustomization.glyph), "")  , Some("col-lg-2"), Some("col-lg-10")
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
                "Use property value as label",
                new CheckBox("useValue",
                    conClassCustomization.labels != null
                        && conClassCustomization.labels != ""
                        && !conClassCustomization.labelsSplitted(0).userDefined, "UseValue"),
                None, None
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



        conditionValueField.render(settingsDiv.htmlElement)
        conditionalClassSwitch.render(settingsDiv.htmlElement)
        labelField.render(settingsDiv.htmlElement)
        fillColor.render(settingsDiv.htmlElement)
        radius.render(settingsDiv.htmlElement)
        glyph.render(settingsDiv.htmlElement)
    }

    def renderGroupCustomizationViews(classCustomization: ClassCustomization) {
        val fillColor = new InputControl(
            "Fill color:",
            new ColorInput("fillColor", Color(classCustomization.fillColor), ""), Some("col-lg-2"), Some("col-lg-10")
        )
        val radius = new InputControl(
            "Radius:",
            new NumericInput("radius", classCustomization.radius, "")   , Some("col-lg-2"), Some("col-lg-10")
        )
        val glyph = new InputControl(
            "Glyph:",
            new GlyphInput("glyph", Some(classCustomization.glyph), "")  , Some("col-lg-2"), Some("col-lg-10")
        )

        val labels = new InputControl(
            "Labels:",
            new OrderedItemsList("",
                if(classCustomization.labels == null || classCustomization.labels == "") {
                    List(new LabelItem("Group name", false, false))
                } else { classCustomization.labelsSplitted }),
            Some("col-lg-2"), Some("col-lg-10")
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
            new ColorInput("strokeColor", Color(propertyCustomization.strokeColor), ""), Some("col-lg-2"), Some("col-lg-10")
        )
        val strokeWidth = new InputControl(
            "Stroke width:",
            new NumericInput("strokeWidth", propertyCustomization.strokeWidth, ""), Some("col-lg-2"), Some("col-lg-10")
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
          $( "#sortableConditionalClasses" ).sortable({
                axis: 'y',
                items: '.sortedConditionalClass',
                start: function() {
                    $(this).find("li:not(.sortedConditionalClass)").each(function() {
                        $(this).data("fixedIndex", $(this).index());
                    })
                },
                change: function() {
                    $(this).find("li:not(.sortedConditionalClass)").each(function() {
                        if($(this).data("fixedIndex") != 0) {
                            $(this).detach().insertAfter(
                                $("#sortableConditionalClasses li:eq(" + ($(this).data("fixedIndex")-1) + ")"));
                        }
                    });
                }
          });
          $( "#sortableConditionalClasses" ).disableSelection();
          $( "#sortableConditionalClasses" ).on( "sortupdate", function( event, ui ) {
                var customizationsOrder = scala.collection.mutable.ListBuffer.get().$apply();
                var listElements = document.getElementById("sortableConditionalClasses").children;
                for(var i = 1; i < listElements.length; ++i) { //skip first non soratble element
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

    @javascript (
        """
          var list = $("#sortableConditionalClasses");
          var listItems = $(".sortedConditionalClass");
          list.append(listItems.get().reverse());
        """
    )
    private def reverseConditionalClassCustomizationsOrder() {} //reverse the order of elements

    private def updateConditionalClassCustomizationsOrder(order: List[Int]) {

        //update conditionalClasses list
        val reorderedList = new ListBuffer[ClassCustomization]()
        val reorderedItemsList = new ListBuffer[ButtonedListItem]()

        order.foreach{ orderNumber =>
            conditionalClassCustomizations.filter(
                _.orderNumber == orderNumber).foreach(reorderedList += _)
            conditionalClassCustomizationListItems.filter(
                _.getAttribute("orderNumber") == orderNumber).foreach(reorderedItemsList += _)
        }

        var positionNumber = 0
        reorderedList.foreach{ item =>
            classConditionalOrderChanged.trigger(new ClassCustomizationEventArgs(null, item, ""+positionNumber))
            item.orderNumber = positionNumber
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
