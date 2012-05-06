package cz.payola.web.client.views.plugins.visual

import components.visualsetup.{VisualSetup, ColorPane}
import s2js.adapters.js.browser._
import s2js.adapters.js.dom.Element

class  SetupLoader
{
    val VertexColorHigh = "setup.vertex.colors.selected"
    private val VertexColorHighRed = "setup.vertex.colors.selected.red"
    private val VertexColorHighGreen = "setup.vertex.colors.selected.green"
    private val VertexColorHighBlue = "setup.vertex.colors.selected.blue"
    private val VertexColorHighAlpha = "setup.vertex.colors.selected.alpha"

    val VertexColorLow = "setup.vertex.colors.hidden"
    private val VertexColorLowRed = "setup.vertex.colors.hidden.red"
    private val VertexColorLowGreen = "setup.vertex.colors.hidden.green"
    private val VertexColorLowBlue = "setup.vertex.colors.hidden.blue"
    private val VertexColorLowAlpha = "setup.vertex.colors.hidden.alpha"

    val VertexColorMedium = "setup.vertex.colors.default"
    private val VertexColorMediumRed = "setup.vertex.colors.default.red"
    private val VertexColorMediumGreen = "setup.vertex.colors.default.green"
    private val VertexColorMediumBlue = "setup.vertex.colors.default.blue"
    private val VertexColorMediumAlpha = "setup.vertex.colors.default.alpha"

    val VertexColorLiteral = "setup.vertex.colors.literal"
    private val VertexColorLiteralRed = "setup.vertex.colors.literal.red"
    private val VertexColorLiteralGreen = "setup.vertex.colors.literal.green"
    private val VertexColorLiteralBlue = "setup.vertex.colors.literal.blue"
    private val VertexColorLiteralAlpha = "setup.vertex.colors.literal.alpha"

    val VertexColorIdentified = "setup.vertex.colors.identified"
    private val VertexColorIdentifiedRed = "setup.vertex.colors.identified.red"
    private val VertexColorIdentifiedGreen = "setup.vertex.colors.identified.green"
    private val VertexColorIdentifiedBlue = "setup.vertex.colors.identified.blue"
    private val VertexColorIdentifiedAlpha = "setup.vertex.colors.identified.alpha"

    val VertexColorUnknown = "setup.vertex.colors.unknown"
    private val VertexColorUnknownRed = "setup.vertex.colors.unknown.red"
    private val VertexColorUnknownGreen = "setup.vertex.colors.unknown.green"
    private val VertexColorUnknownBlue = "setup.vertex.colors.unknown.blue"
    private val VertexColorUnknownAlpha = "setup.vertex.colors.unknown.alpha"

    val VertexIconLiteral = "setup.vertex.icons.literal"
    val VertexIconIdentified = "setup.vertex.icons.identified"
    val VertexIconUnknown = "setup.vertex.icons.unknown"

    val VertexDimensionCornerRadius = "setup.vertex.dimensions.corner-radius"
    val VertexDimensionWidth = "setup.vertex.dimensions.width"
    val VertexDimensionHeight = "setup.vertex.dimensions.height"


    val EdgeColorHigh = "setup.edge.colors.high"
    private val EdgeColorHighRed = "setup.edge.colors.high.red"
    private val EdgeColorHighGreen = "setup.edge.colors.high.green"
    private val EdgeColorHighBlue = "setup.edge.colors.high.blue"
    private val EdgeColorHighAlpha = "setup.edge.colors.high.alpha"

    val EdgeColorMedium = "setup.edge.colors.medium"
    private val EdgeColorMediumRed = "setup.edge.colors.medium.red"
    private val EdgeColorMediumGreen = "setup.edge.colors.medium.green"
    private val EdgeColorMediumBlue = "setup.edge.colors.medium.blue"
    private val EdgeColorMediumAlpha = "setup.edge.colors.medium.alpha"

    val EdgeDimensionStraightIndex = "setup.edge.dimensions.straight-index"
    val EdgeDimensionWidth = "setup.edge.dimensions.width"


    val TextColorMedium = "setup.text.colors.medium"
    private val TextColorMediumRed = "setup.text.colors.medium.red"
    private val TextColorMediumGreen = "setup.text.colors.medium.green"
    private val TextColorMediumBlue = "setup.text.colors.medium.blue"
    private val TextColorMediumAlpha = "setup.text.colors.medium.alpha"

    val TextColorBackground = "setup.text.colors.background"
    private val TextColorBackgroundRed = "setup.text.colors.background.red"
    private val TextColorBackgroundGreen = "setup.text.colors.background.green"
    private val TextColorBackgroundBlue = "setup.text.colors.background.blue"
    private val TextColorBackgroundAlpha = "setup.text.colors.background.alpha"


    def prepare() {
        buildSetup(false)
    }

    def reset() {
        buildSetup(true)
    }

    private def buildSetup(reset: Boolean) {

        //setup -> vertex -> colors -> medium color
        setItem(VertexColorMediumRed, "200", reset)
        setItem(VertexColorMediumGreen, "240", reset)
        setItem(VertexColorMediumBlue, "200", reset)
        setItem(VertexColorMediumAlpha, "0.8", reset)

        //setup -> vertex -> colors -> high color
        setItem(VertexColorHighRed, "240", reset)
        setItem(VertexColorHighGreen, "240", reset)
        setItem(VertexColorHighBlue, "150", reset)
        setItem(VertexColorHighAlpha, "1", reset)

        //setup -> vertex -> colors -> low color
        setItem(VertexColorLowRed, "180", reset)
        setItem(VertexColorLowGreen, "180", reset)
        setItem(VertexColorLowBlue, "180", reset)
        setItem(VertexColorLowAlpha, "0.3", reset)

        //setup -> vertex -> colors -> literal vertex color
        setItem(VertexColorLiteralRed, "180", reset)
        setItem(VertexColorLiteralGreen, "50", reset)
        setItem(VertexColorLiteralBlue, "50", reset)
        setItem(VertexColorLiteralAlpha, "1", reset)

        //setup -> vertex -> colors -> identified vertex color
        setItem(VertexColorIdentifiedRed, "50", reset)
        setItem(VertexColorIdentifiedGreen, "180", reset)
        setItem(VertexColorIdentifiedBlue, "50", reset)
        setItem(VertexColorIdentifiedAlpha, "1", reset)

        //setup -> vertex -> colors -> unknown vertex color
        setItem(VertexColorUnknownRed, "0", reset)
        setItem(VertexColorUnknownGreen, "0", reset)
        setItem(VertexColorUnknownBlue, "0", reset)
        setItem(VertexColorUnknownAlpha, "1", reset)

        //setup -> vertex -> icons -> literal vertex icon
        setItem(VertexIconLiteral, "/assets/images/book-icon.png", reset)

        //setup -> vertex -> icons -> identified vertex icon
        setItem(VertexIconIdentified, "/assets/images/view-eye-icon.png", reset)

        //setup -> vertex -> icons -> unknown vertex icon
        setItem(VertexIconUnknown, "/assets/images/question-mark-icon.png", reset)

        //setup -> vertex -> dimensions -> corner radius
        setItem(VertexDimensionCornerRadius, "5", reset)

        //setup -> vertex -> dimensions -> width
        setItem(VertexDimensionWidth, "30", reset)

        //setup -> vertex -> dimensions -> height
        setItem(VertexDimensionHeight, "24", reset)



        //setup -> edge -> colors -> selected edge color ##########################################
        setItem(EdgeColorHighRed, "50", reset)
        setItem(EdgeColorHighGreen, "50", reset)
        setItem(EdgeColorHighBlue, "50", reset)
        setItem(EdgeColorHighAlpha, "1", reset)

        //setup -> edge -> colors -> medium edge color
        setItem(EdgeColorMediumRed, "150", reset)
        setItem(EdgeColorMediumGreen, "150", reset)
        setItem(EdgeColorMediumBlue, "150", reset)
        setItem(EdgeColorMediumAlpha, "0.5", reset)

        //setup -> edge -> dimensions ->  line width
        setItem(EdgeDimensionWidth, "1", reset)

        //setup -> edge -> dimensions ->  straight index
        setItem(EdgeDimensionStraightIndex, "-1", reset)


        //setup -> text -> colors -> default text color ###########################################
        setItem(TextColorMediumRed, "50", reset)
        setItem(TextColorMediumGreen, "50", reset)
        setItem(TextColorMediumBlue, "50", reset)
        setItem(TextColorMediumAlpha, "1", reset)

        //setup -> text -> colors -> background text color
        setItem(TextColorBackgroundRed, "255", reset)
        setItem(TextColorBackgroundGreen, "255", reset)
        setItem(TextColorBackgroundBlue, "255", reset)
        setItem(TextColorBackgroundAlpha, "0.5", reset)
    }

    private def setItem(where: String, what: String, reset: Boolean) {
        if(reset || getItem(where).isEmpty) {
            window.localStorage.setItem(where, what)
        }
    }

    private def getItem(where: String): Option[String] = {
        val gotFromMemory = window.localStorage.getItem(where)
        if(gotFromMemory == null) {
            None
        } else {
            Some(gotFromMemory)
        }
    }


    def createColor(keyName: String): Option[Color] = {
        val red = getItem(keyName + ".red")
        val green = getItem(keyName + ".green")
        val blue = getItem(keyName + ".blue")
        val alpha = getItem(keyName + ".alpha")

        if(red.isEmpty || green.isEmpty || blue.isEmpty || alpha.isEmpty) {
            None
        } else {
            Some(new Color(red.get.toInt, green.get.toInt, blue.get.toInt, alpha.get.toDouble))
        }
    }

    def getValue(localStorageKey: String): Option[String] = {
        val value = getItem(localStorageKey)
        if(value.isEmpty) {
            None
        } else {
            value
        }
    }




    def buildSetupArea(visible: Boolean) {

    }

    private def buildTextSettings(parent: Element) {
        buildColorSetup(parent, "medium", TextColorMedium)
        buildColorSetup(parent, "backg", TextColorBackground)
    }

    private def buildVertexSettings(parent: Element) {
        val vertexSection = document.createElement[Element]("label")
        parent.appendChild(vertexSection)
        vertexSection.innerHTML = "Vertex"

        buildInput(parent, "corner radius", VertexDimensionCornerRadius)

        buildInput(parent, "width", VertexDimensionWidth)

        buildInput(parent, "height", VertexDimensionHeight)

        buildColorSetup(parent, "low", VertexColorLow)

        buildColorSetup(parent, "mediu", VertexColorMedium)

        buildColorSetup(parent, "high", VertexColorHigh)

        buildColorSetup(parent, "literal", VertexColorLiteral)

        buildColorSetup(parent, "identif", VertexColorIdentified)

        buildColorSetup(parent, "unkn", VertexColorUnknown)

        buildInput(parent, "lit Icon", VertexIconLiteral)

        buildInput(parent, "ident Icon", VertexIconIdentified)

        buildInput(parent, "unkn Icon", VertexIconUnknown)
    }

    private def buildEdgeSettings(parent: Element) {
        buildInput(parent, "width", EdgeDimensionWidth)
        buildInput(parent, "straigthten index", EdgeDimensionStraightIndex)
        buildColorSetup(parent, "select", EdgeColorHigh)
        buildColorSetup(parent, "base", EdgeColorMedium)
    }

    private def buildColorSetup(parent: Element, labelText: String, location: String) {

        val colorPane = new ColorPane(location, Color.Black)
        colorPane.changed += {
            event => true
        }

        colorPane.render(parent)
    }

    private def buildInput(parent: Element, labelText: String, bindToLocation: String) {

        val label = document.createElement[Element]("label")
        parent.appendChild(label)
        label.innerHTML = labelText

        val spacer = document.createElement[Element]("label")
        parent.appendChild(spacer)
        spacer.innerHTML = " "

//        new InputViewModel(parent, "input", getValue(bindToLocation).getOrElse(""), bindToLocation) //TODO type aware?

        /*parent.appendChild(inputField)
        inputField.className = "visualPluginSettings textField"
        inputField.setAttribute("type", "text")
        inputField.setAttribute("onChange", "window.localStorage.setItem(\""+bindToLocation+"\", this.value)")
        inputField.setAttribute("value", getValue(bindToLocation).getOrElse(""))*/
    }
}
