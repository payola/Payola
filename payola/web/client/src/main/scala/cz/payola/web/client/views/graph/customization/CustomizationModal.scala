package cz.payola.web.client.views.graph.customization

import cz.payola.common.entities.settings._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.events._
import cz.payola.web.client.views.bootstrap.inputs.TextInputControl
import cz.payola.web.client.views.graph.visual.settings.components.visualsetup.ColorPane
import cz.payola.web.client.views.graph.visual.Color
import s2js.adapters.js.browser._

class CustomizationModal(customization: OntologyCustomization)
    extends Modal("Customize ontology " + customization.name, Nil, Some("Done"), None, false,
        "wide-customization-modal")
{

    // Event handlers
    val classFillColorChanged = new UnitEvent[this.type, ClassCustomizationModificationEventArgs[this.type, String]]
    var classRadiusChanged = new UnitEvent[this.type, ClassCustomizationModificationEventArgs[this.type, Int]]
    var classGlyphChanged = new UnitEvent[this.type, ClassCustomizationModificationEventArgs[this.type, Option[Char]]]
    var classPropertyStrokeColorChanged = new UnitEvent[this.type, ClassPropertyCustomizationModificationEventArgs[this.type, String]]
    var classPropertyStrokeWidthChanged = new UnitEvent[this.type, ClassPropertyCustomizationModificationEventArgs[this.type, Int]]

    // Create a completely enclosing div
    val enclosingDiv = new Div()
    enclosingDiv.setAttribute("style", "padding: 0px 0;")
    enclosingDiv.addCssClass("container-fluid")

    val rowDiv = new Div()
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



    private def getClassElementByName(name: String): InputControl = {
        propertiesDiv.domElement.getElementsByTagName(name).item(0).asInstanceOf[InputControl]
    }

    def getFillColorInputForSelectedClass: InputControl = {
        getClassElementByName("class-fill-color-input")
    }

    def getRadiusInputForSelectedClass: InputControl = {
        getClassElementByName("class-radius")
    }


    private def setupDivAttributes() {
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
        val strokeColorInput = new ColorPane("property-stroke-color-" + propCustomization.uri, "Stroke color:", Color.fromHex(propCustomization.strokeColor))
        val widthInput = new TextInputControl("Stroke width:", "property-stroke-width-" + propCustomization.uri, propCustomization.strokeWidth.toString, "")

        strokeColorInput.changed += { e =>
            classPropertyStrokeColorChanged.trigger(new ClassPropertyCustomizationModificationEventArgs[this.type, String](selectedClassCustomization.uri, propCustomization.uri, strokeColorInput.getColorHexString, this))
        }

        widthInput.input.changed += { e =>
            classPropertyStrokeWidthChanged.trigger(new ClassPropertyCustomizationModificationEventArgs[this.type, Int](selectedClassCustomization.uri, propCustomization.uri, widthInput.input.value.toInt, this))
        }

        strokeColorInput.render(propertiesDiv.domElement)
        widthInput.render(propertiesDiv.domElement)
    }

    /** Appends fields for the selected class customization to the propertiesDiv div.
      *
      */
    private def appendCustomizablePropertiesForSelectedClass() {
        val fillColorInput = new ColorPane("class-fill-color-input", "Fill color:", Color.fromHex(selectedClassCustomization.fillColor))
        val radiusInput = new TextInputControl("Radius:", "class-radius", selectedClassCustomization.radius.toString, "")
        val glyphInput = new TextInputControl("Glyph:", "class-glyph", selectedClassCustomization.glyph.getOrElse('\0').toString, "")

        fillColorInput.changed += { e =>
            classFillColorChanged.trigger(new ClassCustomizationModificationEventArgs[this.type, String](selectedClassCustomization.uri, fillColorInput.getColorHexString, this))
        }
        radiusInput.input.changed += { e =>
            classRadiusChanged.trigger(new ClassCustomizationModificationEventArgs[this.type, Int](selectedClassCustomization.uri, radiusInput.input.value.toInt, this))
        }
        glyphInput.input.changed += { e =>
            val charOption = if (glyphInput.input.value == "") None else Some(glyphInput.input.value.charAt(0))
            classGlyphChanged.trigger(new ClassCustomizationModificationEventArgs[this.type, Option[Char]](selectedClassCustomization.uri, charOption, this))
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
            val classListItem = new ListItem(List(new Anchor(List(new Icon(Icon.tag), new Text(" " + classCustomization.uri.split("#")(1))))), "")
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
