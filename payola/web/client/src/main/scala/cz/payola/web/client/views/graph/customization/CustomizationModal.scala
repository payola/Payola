package cz.payola.web.client.views.graph.customization

import cz.payola.common.entities.settings._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.events._
import scala.collection.mutable.ListBuffer
import scala.Some

class CustomizationModal(customization: OntologyCustomization) extends Modal("Customize ontology " + customization.name, Nil, Some("Done"), None, false, List("wide-customization-modal"))
{
    // Event handlers
    val classFillColorChanged = new BooleanEvent[this.type, ClassCustomizationModificationEventArgs[this.type, String]]
    var classRadiusChanged = new BooleanEvent[this.type, ClassCustomizationModificationEventArgs[this.type, Int]]
    var classGlyphChanged = new BooleanEvent[this.type, ClassCustomizationModificationEventArgs[this.type, Option[Char]]]

    var classPropertyStrokeColorChanged = new BooleanEvent[this.type, ClassPropertyCustomizationModificationEventArgs[this.type, String]]
    var classPropertyStrokeWidthChanged = new BooleanEvent[this.type, ClassPropertyCustomizationModificationEventArgs[this.type, Int]]

    // Create a completely enclosing div
    val enclosingDiv = new Div()
    enclosingDiv.addCssClass("container-fluid")

    val rowDiv = new Div()
    rowDiv.addCssClass("row-fluid")
    rowDiv.render(enclosingDiv.domElement)

    // Override body to have just that div
    override val body = List(enclosingDiv)

    // Create a class div and values div
    // TODO prettify
    val classListItems = createClassListItems
    val classUnorderedList = new UnorderedList(classListItems)
    classUnorderedList.addCssClass("nav")
    classUnorderedList.addCssClass("nav-list")

    val classListDiv = new Div(List(classUnorderedList))
    classListDiv.addCssClass("span5")
    classListDiv.addCssClass("modal-inner-view")
    classListDiv.addCssClass("well")

    val propertiesDiv = new Div(Nil)
    propertiesDiv.addCssClass("span7")
    propertiesDiv.addCssClass("modal-inner-view")

    classListDiv.render(rowDiv.domElement)
    propertiesDiv.render(rowDiv.domElement)

    var selectedClassCustomization: ClassCustomization = null
    selectClassItem(classListItems(0))

    private def appendCustomizablePropertiesForPropertyCustomization(propCustomization: PropertyCustomization) {
        val strokeColorInput = new TextInputControl("Stroke color:", "property-stroke-color-" + propCustomization.uri, propCustomization.strokeColor, "")
        val widthInput = new TextInputControl("Stroke width:", "property-stroke-width-" + propCustomization.uri, propCustomization.strokeWidth.toString, "")

        strokeColorInput.input.changed += { e =>
            classPropertyStrokeColorChanged.trigger(new ClassPropertyCustomizationModificationEventArgs[this.type, String](selectedClassCustomization.uri, propCustomization.uri, strokeColorInput.input.value, this))
        }

        widthInput.input.changed += { e =>
            classPropertyStrokeWidthChanged.trigger(new ClassPropertyCustomizationModificationEventArgs[this.type, Int](selectedClassCustomization.uri, propCustomization.uri, widthInput.input.value.toInt, this))
        }

        strokeColorInput.render(propertiesDiv.domElement)
        widthInput.render(propertiesDiv.domElement)
    }

    private def appendCustomizablePropertiesForSelectedClass() {
        val fillColorInput = new TextInputControl("Fill color:", "class-color-input", selectedClassCustomization.fillColor, "")
        val radiusInput = new TextInputControl("Radius:", "class-radius", selectedClassCustomization.radius.toString, "")
        val glyphInput = new TextInputControl("Glyph:", "class-glyph", selectedClassCustomization.glyph.getOrElse('\0').toString, "")

        fillColorInput.input.changed += { e =>
            classFillColorChanged.trigger(new ClassCustomizationModificationEventArgs[this.type, String](selectedClassCustomization.uri, fillColorInput.input.value, this))
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

    private def appendHeaderDivForName(name: String) {
        val div = new Div(List(new Text(name)))
        div.addCssClass("label")
        div.addCssClass("label-info")
        div.render(propertiesDiv.domElement)
    }

    private def selectClassItem(item: ListItem) {
        item.addCssClass("active")

        // We assume such a customization does exist.
        selectedClassCustomization = customization.classCustomizations.find(_.uri == item.getAttribute("name")).get
        createPropertyDivsForSelectedClass()
    }


    private def classSelectionHandler(e: BrowserEventArgs[ListItem]) = {
        classListItems.foreach { listItem: ListItem =>
            listItem.removeCssClass("active")
        }

        selectClassItem(e.target)

        true
    }

    private def createClassListItems: Seq[ListItem] = {
        val classItemsBuffer = new ListBuffer[ListItem]
        customization.classCustomizations foreach { classCustomization: ClassCustomization =>
            val classListItem = new ListItem(List(new Anchor(List(new Icon(Icon.tag), new Text(classCustomization.uri)))), "")
            classListItem.setAttribute("name", classCustomization.uri)

            classListItem.mouseClicked += classSelectionHandler

            classItemsBuffer += classListItem
        }

        classItemsBuffer
    }

    private def createPropertyDivsForSelectedClass() {
        // Clear properties
        propertiesDiv.domElement.innerHTML = ""

        appendCustomizablePropertiesForSelectedClass()

        selectedClassCustomization.propertyCustomizations.foreach { propCustomization: PropertyCustomization =>
            appendHeaderDivForName(propCustomization.uri)
            appendCustomizablePropertiesForPropertyCustomization(propCustomization)
        }

    }


}
