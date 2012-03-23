package cz.payola.web.client.views.plugins.visual

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

    
    def createDefaultSetup() {

        //setup -> vertex -> colors -> medium color
        setItem(VertexColorMediumRed, "180")
        setItem(VertexColorMediumGreen, "240")
        setItem(VertexColorMediumBlue, "180")
        setItem(VertexColorMediumAlpha, "0.8")

        //setup -> vertex -> colors -> high color
        setItem(VertexColorHighRed, "240")
        setItem(VertexColorHighGreen, "180")
        setItem(VertexColorHighBlue, "180")
        setItem(VertexColorHighAlpha, "1")

        //setup -> vertex -> colors -> low color
        setItem(VertexColorLowRed, "180")
        setItem(VertexColorLowGreen, "180")
        setItem(VertexColorLowBlue, "180")
        setItem(VertexColorLowAlpha, "0.3")

        //setup -> vertex -> colors -> literal vertex color
        setItem(VertexColorLiteralRed, "200")
        setItem(VertexColorLiteralGreen, "150")
        setItem(VertexColorLiteralBlue, "0")
        setItem(VertexColorLiteralAlpha, "1")

        //setup -> vertex -> colors -> identified vertex color
        setItem(VertexColorIdentifiedRed, "0")
        setItem(VertexColorIdentifiedGreen, "200")
        setItem(VertexColorIdentifiedBlue, "150")
        setItem(VertexColorIdentifiedAlpha, "1")

        //setup -> vertex -> colors -> unknown vertex color
        setItem(VertexColorUnknownRed, "150")
        setItem(VertexColorUnknownGreen, "0")
        setItem(VertexColorUnknownBlue, "200")
        setItem(VertexColorUnknownAlpha, "1")

        //setup -> vertex -> icons -> literal vertex icon
        setItem(VertexIconLiteral, "/assets/images/book-icon.png")

        //setup -> vertex -> icons -> identified vertex icon
        setItem(VertexIconIdentified, "/assets/images/view-eye-icon.png")

        //setup -> vertex -> icons -> unknown vertex icon
        setItem(VertexIconUnknown, "/assets/images/question-mark-icon.png")

        //setup -> vertex -> dimensions -> corner radius
        setItem(VertexDimensionCornerRadius, "5")

        //setup -> vertex -> dimensions -> width
        setItem(VertexDimensionWidth, "30")

        //setup -> vertex -> dimensions -> height
        setItem(VertexDimensionHeight, "24")



        //setup -> edge -> colors -> selected edge color ##########################################
        setItem(EdgeColorHighRed, "50")
        setItem(EdgeColorHighGreen, "50")
        setItem(EdgeColorHighBlue, "50")
        setItem(EdgeColorHighAlpha, "1")
        
        //setup -> edge -> colors -> medium edge color
        setItem(EdgeColorMediumRed, "150")
        setItem(EdgeColorMediumGreen, "150")
        setItem(EdgeColorMediumBlue, "150")
        setItem(EdgeColorMediumAlpha, "0.5")

        //setup -> edge -> dimensions ->  line width
        setItem(EdgeDimensionWidth, "1")

        //setup -> edge -> dimensions ->  straight index
        setItem(EdgeDimensionStraightIndex, "-1")


        //setup -> text -> colors -> default text color ###########################################
        setItem(TextColorMediumRed, "50")
        setItem(TextColorMediumGreen, "50")
        setItem(TextColorMediumBlue, "50")
        setItem(TextColorMediumAlpha, "1")

        //setup -> text -> colors -> background text color
        setItem(TextColorBackgroundRed, "255")
        setItem(TextColorBackgroundGreen, "255")
        setItem(TextColorBackgroundBlue, "255")
        setItem(TextColorBackgroundAlpha, "0.5")
    }
    
    private def setItem(where: String, what: String) {
        if(window.localStorage.getItem(where) == null) {
            window.localStorage.setItem(where, what)
        }
    }
    
    private def getItem(where: String): String = {
        window.localStorage.getItem(where)
    }
        
    
    def createColor(localStorageKey: String): Option[Color] = {
        val red = getItem(localStorageKey + ".red")
        val green = getItem(localStorageKey + ".green")
        val blue = getItem(localStorageKey + ".blue")
        val alpha = getItem(localStorageKey + ".alpha")

        //TODO if correct create new color nebo new Color(200, 0, 0, 1)
        if(red == null || green == null || blue == null || alpha == null) {
            None
        } else {
            Some(new Color(red.toInt, green.toInt, blue.toInt, alpha.toDouble))
        }
    }
    
    def getValue(localStorageKey: String): Option[String] = {
        val value = getItem(localStorageKey)
        if(value == null) { //TODO is this a valid check?
            None
        } else {
            Some(value)
        }
    }




    def buildSetupArea() {
        val controlsArea = document.getElementById("controls")


        controlsArea.appendChild(document.createElement[Element]("br"))
        controlsArea.appendChild(document.createElement[Element]("br"))

        val settingsHideButton = document.createElement[Element]("button")
        controlsArea.appendChild(settingsHideButton)
        settingsHideButton.setAttribute("type", "button")
        settingsHideButton.setAttribute("id", "settingsHideButton")
        settingsHideButton.innerHTML = "Hide/show all your settings, baby!"
        settingsHideButton.setAttribute("onclick", "el = document.getElementById(\"visualPluginSettings\");" +
            "el.style.visibility = (el.style.visibility == \"visible\") ? \"hidden\" : \"visible\";")

        controlsArea.appendChild(document.createElement[Element]("br"))

        val settingsDiv = document.createElement[Element]("div")
        controlsArea.appendChild(settingsDiv)
        settingsDiv.setAttribute("id", "visualPluginSettings")

        val settingsForm = document.createElement[Element]("form")
        settingsDiv.appendChild(settingsForm)

        //Vertex settings #########################################################################
        buildVertexSettings(settingsForm)

        settingsForm.appendChild(document.createElement[Element]("br"))
        settingsForm.appendChild(document.createElement[Element]("br"))

        //Edge settings ###########################################################################
        buildEdgeSettings(settingsForm)

        //Text setttings ##########################################################################
        buildTextSettings(settingsForm)

        settingsForm.appendChild(document.createElement[Element]("br"))

        val submitButton = document.createElement[Element]("button")
        settingsForm.appendChild(submitButton)
        submitButton.setAttribute("type", "button")
        submitButton.innerHTML = "Yeah, I like this way, honey!"
        submitButton.setAttribute("onclick", "presenterIndex.updateSettings(true)")
    }

    private def buildTextSettings(parent: Element) {
        val section = document.createElement[Element]("label")
        parent.appendChild(section)
        section.innerHTML = "Text"

        parent.appendChild(document.createElement[Element]("br"))
        buildColorSetup(parent, "color", TextColorMedium)

        parent.appendChild(document.createElement[Element]("br"))
        buildColorSetup(parent, "color", TextColorBackground)
    }

    private def buildVertexSettings(parent: Element) {
        val vertexSection = document.createElement[Element]("label")
        parent.appendChild(vertexSection)
        vertexSection.innerHTML = "Vertex"

        parent.appendChild(document.createElement[Element]("br"))
        buildInput(parent, "corner radius", VertexDimensionCornerRadius)

        parent.appendChild(document.createElement[Element]("br"))
        buildInput(parent, "width", VertexDimensionWidth)

        parent.appendChild(document.createElement[Element]("br"))
        buildInput(parent, "height", VertexDimensionHeight)

        parent.appendChild(document.createElement[Element]("br"))
        buildColorSetup(parent, "low", VertexColorLow)

        parent.appendChild(document.createElement[Element]("br"))
        buildColorSetup(parent, "mediu", VertexColorMedium)

        parent.appendChild(document.createElement[Element]("br"))
        buildColorSetup(parent, "high", VertexColorHigh)

        parent.appendChild(document.createElement[Element]("br"))
        buildColorSetup(parent, "literal", VertexColorLiteral)

        parent.appendChild(document.createElement[Element]("br"))
        buildColorSetup(parent, "identif", VertexColorIdentified)

        parent.appendChild(document.createElement[Element]("br"))
        buildColorSetup(parent, "unkn", VertexColorUnknown)

        parent.appendChild(document.createElement[Element]("br"))
        buildInput(parent, "lit Icon", VertexIconLiteral).setAttribute("disabled", "disabled")

        parent.appendChild(document.createElement[Element]("br"))
        buildInput(parent, "ident Icon", VertexIconIdentified).setAttribute("disabled", "disabled")

        parent.appendChild(document.createElement[Element]("br"))
        buildInput(parent, "unkn Icon", VertexIconUnknown).setAttribute("disabled", "disabled")
    }

    private def buildEdgeSettings(parent: Element) {
        val edgeSection = document.createElement[Element]("label")
        parent.appendChild(edgeSection)
        edgeSection.innerHTML = "Edge"

        parent.appendChild(document.createElement[Element]("br"))
        buildInput(parent, "width", EdgeDimensionWidth)

        parent.appendChild(document.createElement[Element]("br"))
        buildInput(parent, "straigthten index", EdgeDimensionStraightIndex)

        parent.appendChild(document.createElement[Element]("br"))
        buildColorSetup(parent, "select", EdgeColorHigh)

        parent.appendChild(document.createElement[Element]("br"))
        buildColorSetup(parent, "base", EdgeColorMedium)
    }

    private def buildColorSetup(parent: Element, labelText: String, location: String) {

        val edgeColorHighLabel = document.createElement[Element]("label")
        parent.appendChild(edgeColorHighLabel)
        edgeColorHighLabel.innerHTML = labelText

        val spacer1 = document.createElement[Element]("label")
        parent.appendChild(spacer1)
        spacer1.innerHTML = " "

        //red
        buildInput(parent, "R", location+".red")
        val spacer2 = document.createElement[Element]("label")
        parent.appendChild(spacer2)
        spacer2.innerHTML = " "

        //green
        buildInput(parent, "G", location +".green")
        val spacer3 = document.createElement[Element]("label")
        parent.appendChild(spacer3)
        spacer3.innerHTML = " "

        //blue
        buildInput(parent, "B", location+".blue")
        val spacer4 = document.createElement[Element]("label")
        parent.appendChild(spacer4)
        spacer4.innerHTML = " "

        //alpha
        buildInput(parent, "A", location+".alpha")
    }

    private def buildInput(parent: Element, labelText: String, bindToLocation: String): Element = {

        val label = document.createElement[Element]("label")
        parent.appendChild(label)
        label.innerHTML = labelText

        val spacer = document.createElement[Element]("label")
        parent.appendChild(spacer)
        spacer.innerHTML = " "

        val inputField = document.createElement[Element]("input")
        parent.appendChild(inputField)
        inputField.setAttribute("size", "3")
        inputField.setAttribute("type", "text")
        inputField.setAttribute("onChange", "window.localStorage.setItem(\""+bindToLocation+"\", this.value)")
        inputField.setAttribute("value", getValue(bindToLocation).getOrElse(""))

        inputField
    }
}
