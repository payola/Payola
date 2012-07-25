package cz.payola.web.client.views.entity

import cz.payola.common.entities.settings._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.events._
import cz.payola.web.client.views.bootstrap.inputs.TextInputControl
import cz.payola.web.client.views.graph.visual.settings.components.visualsetup.ColorPane
import cz.payola.web.client.views.graph.visual.Color

class OntologyCustomizationEditModal(customization: OntologyCustomization)
    extends Modal("Edit ontology customization", Nil, Some("Done"), None, false,
        "ontology-customization-modal")
{
    // Event handlers
    val classFillColorChanged = new UnitEvent[this.type, ClassCustomizationModificationEventArgs[this.type]]

    val classRadiusChanged = new UnitEvent[this.type, ClassCustomizationModificationEventArgs[this.type]]

    val classGlyphChanged = new UnitEvent[this.type, ClassCustomizationModificationEventArgs[this.type]]

    val ontologyNameChanged = new UnitEvent[this.type, EventArgs[this.type]]

    val propertyStrokeColorChanged = new UnitEvent[this.type, PropertyCustomizationModificationEventArgs[this.type]]

    val propertyStrokeWidthChanged = new UnitEvent[this.type, PropertyCustomizationModificationEventArgs[this.type]]

    // Create a completely enclosing div
    val enclosingDiv = new Div()

    enclosingDiv.setAttribute("style", "padding: 0px 0;")
    enclosingDiv.addCssClass("container-fluid")

    // Add the input control and other buttons to the customizationNameRowDiv
    val customizationNameFieldTitle = new Div(List(new Text("Name: ")), "span1 inline-display")

    val customizationNameField = new TextInputControl("", "custom-name", customization.name, "", "span5")
    customizationNameField.input.keyReleased += { e =>
        ontologyNameChanged.trigger(new EventArgs[OntologyCustomizationEditModal.this.type](this))
        true
    }

    val shareButtonViewSpace = new Span(Nil)

    val deleteButton = new Anchor(List(new Icon(Icon.remove), new Text(" Delete")), "#", "btn btn-danger")
    val deleteButtonSpan = new Span(List(deleteButton))

    val buttonsDiv = new Div(List(shareButtonViewSpace, deleteButtonSpan), "btn-group span6")
    buttonsDiv.setAttribute("style", "display: inline;")

    val customizationNameRowDiv = new Div(List(customizationNameFieldTitle, customizationNameField, buttonsDiv))
    val rowDiv = new Div()

    customizationNameRowDiv.render(enclosingDiv.domElement)
    rowDiv.render(enclosingDiv.domElement)

    // Override body to have just that div
    override val body = List(enclosingDiv)

    // Create a class div and values div
    val classListItems = createClassListItems

    val classUnorderedList = new UnorderedList(classListItems)

    val classListDiv = new Div(List(classUnorderedList))

    val propertiesDiv = new Div(Nil)

    setupDivAttributes()

    classListDiv.render(rowDiv.domElement)
    propertiesDiv.render(rowDiv.domElement)

    var selectedClassCustomization: ClassCustomization = null

    selectClassItem(classListItems(0))

    /** Retrieves a property element by name.
      *
      * @param name Name of the element.
      * @return InputControl with the name.
      */
    private def getPropertyElementByName(name: String): InputControl = {
        propertiesDiv.domElement.getElementsByTagName(name).item(0).asInstanceOf[InputControl]
    }

    /** Fill color input control;
      *
      * @return Fill color input control;
      */
    def getFillColorInputForSelectedClass: InputControl = {
        getPropertyElementByName("class-fill-color-input")
    }

    /** Glyph input control;
      *
      * @return Glyph input control;
      */
    def getGlyphInputForSelectedClass: InputControl = {
        getPropertyElementByName("class-glyph")
    }

    /** Radius input control;
      *
      * @return Radius input control;
      */
    def getRadiusInputForSelectedClass: InputControl = {
        getPropertyElementByName("class-radius")
    }

    /** Stroke color input control;
      *
      * @return Stroke color input control;
      */
    def getStrokeColorForPropertyOfSelectedClass(propertyURI: String): InputControl = {
        getPropertyElementByName("property-stroke-color-" + propertyURI)
    }

    /** Stroke width input control;
      *
      * @return Stroke width input control;
      */
    def getStrokeWidthForPropertyOfSelectedClass(propertyURI: String): InputControl = {
        getPropertyElementByName("property-stroke-width-" + propertyURI)
    }

    /** Sets up div attributes (mostly adding CSS classes).
      *
      */
    private def setupDivAttributes() {
        customizationNameRowDiv.addCssClass("row-fluid")
        rowDiv.addCssClass("row-fluid")

        classUnorderedList.addCssClass("nav")
        classUnorderedList.addCssClass("nav-list")

        classListDiv.setAttribute("style", "padding: 8px 0;")
        classListDiv.addCssClass("span6")
        classListDiv.addCssClass("modal-inner-view")
        classListDiv.addCssClass("well")
        classListDiv.addCssClass("no-padding")

        propertiesDiv.addCssClass("span6")
        propertiesDiv.addCssClass("modal-inner-view")
    }

    /** Appends fields for the property customization to the propertiesDiv div.
      *
      * @param propCustomization Property customization.
      */
    private def appendCustomizablePropertiesForPropertyCustomization(propCustomization: PropertyCustomization) {
        val strokeColorInput = new ColorPane("property-stroke-color-" + propCustomization.uri, "Stroke color:",
            Color.fromHex(propCustomization.strokeColor))
        val widthInput = new TextInputControl("Stroke width:", "property-stroke-width-" + propCustomization.uri,
            propCustomization.strokeWidth.toString, "")

        // Place the event on closed. When not closed, the color is changing with
        // every single change - we'd flood the server with dozens of requests
        // instead of just one
        val event = { e: EventArgs[ColorPane] =>
            val value = strokeColorInput.getColorHexString
            propertyStrokeColorChanged.trigger(new PropertyCustomizationModificationEventArgs[this.type](
                strokeColorInput.colorInput, selectedClassCustomization.uri, propCustomization.uri, value, this))
        }
        strokeColorInput.closed += event
        strokeColorInput.cleared += event

        widthInput.input.changed += { e =>
            propertyStrokeWidthChanged.trigger(new PropertyCustomizationModificationEventArgs[this.type](
                widthInput, selectedClassCustomization.uri, propCustomization.uri, widthInput.input.value, this))
        }

        strokeColorInput.render(propertiesDiv.domElement)
        widthInput.render(propertiesDiv.domElement)
    }

    /** Appends fields for the selected class customization to the propertiesDiv div.
      *
      */
    private def appendCustomizablePropertiesForSelectedClass() {
        val fillColorInput = new
                ColorPane("class-fill-color-input", "Fill color:", Color.fromHex(selectedClassCustomization.fillColor))
        val radiusInput = new TextInputControl("Radius:", "class-radius", selectedClassCustomization.radius.toString, "")
        val glyphInput = new
                TextInputControl("Glyph:", "class-glyph", selectedClassCustomization.glyph.getOrElse('\0').toString, "")

        // Place the event on closed. When not closed, the color is changing with
        // every single change - we'd flood the server with dozens of requests
        // instead of just one
        val event = { e: EventArgs[ColorPane] =>
            classFillColorChanged.trigger(new ClassCustomizationModificationEventArgs[this.type](
                fillColorInput.colorInput, selectedClassCustomization.uri, fillColorInput.getColorHexString, this))
        }
        fillColorInput.closed += event
        fillColorInput.cleared += event

        radiusInput.input.changed += { e =>
            classRadiusChanged.trigger(new ClassCustomizationModificationEventArgs[this.type](
                radiusInput, selectedClassCustomization.uri, radiusInput.input.value, this))
        }

        glyphInput.input.changed += { e =>
            classGlyphChanged.trigger(new ClassCustomizationModificationEventArgs[this.type](
                glyphInput, selectedClassCustomization.uri, glyphInput.input.value, this))
        }

        fillColorInput.render(propertiesDiv.domElement)
        radiusInput.render(propertiesDiv.domElement)
        glyphInput.render(propertiesDiv.domElement)
    }

    /** Appends a header div - it contains the name. Is used for property titles.
      *
      * @param name Name to be used.
      */
    private def appendHeaderDivForName(name: String) {
        val div = new Div(List(new Text(name)))
        div.addCssClass("label")
        div.addCssClass("label-info")
        div.render(propertiesDiv.domElement)
    }

    /** Makes a list item representing the newly active class customization active
      * and assigns the selectedClassCustomization variable.
      *
      * @param item The list item.
      */
    private def selectClassItem(item: ListItem) {
        item.addCssClass("active")

        // We assume such a customization does exist.
        selectedClassCustomization = customization.classCustomizations.find(_.uri == item.getAttribute("name")).get
        createPropertyDivsForSelectedClass()
    }

    /** Handler for a class being selected.
      *
      * @param e Event arguments.
      * @return Always true at this moment.
      */
    private def classSelectionHandler(e: BrowserEventArgs[ListItem]) = {
        classListItems.foreach { listItem: ListItem =>
            listItem.removeCssClass("active")
        }

        selectClassItem(e.target)
        true
    }

    /** Creates list items representing classes within the ontology customization.
      *
      * @return List items representing classes within the ontology customization.
      */
    private def createClassListItems: Seq[ListItem] = {
        customization.classCustomizations.map { classCustomization: ClassCustomization =>
            val classListItem = new ListItem(
                List(new Anchor(List(new Icon(Icon.tag), new Text(" " + classCustomization.uri.split("#")(1))))), "")
            classListItem.setAttribute("name", classCustomization.uri)
            classListItem.mouseClicked += classSelectionHandler
            classListItem
        }
    }

    /** Creates the properties elements for selected class and fills the properties
      * div with them.
      */
    private def createPropertyDivsForSelectedClass() {
        // Clear properties
        propertiesDiv.domElement.innerHTML = ""

        appendCustomizablePropertiesForSelectedClass()

        selectedClassCustomization.propertyCustomizations.foreach { propCustomization: PropertyCustomization =>
            appendHeaderDivForName(propCustomization.uri.split("#")(1))
            appendCustomizablePropertiesForPropertyCustomization(propCustomization)
        }
    }
}
