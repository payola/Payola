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
        val controlsArea = document.getElementById("controls")


        controlsArea.appendChild(document.createElement[Element]("br"))
        controlsArea.appendChild(document.createElement[Element]("br"))

        val settingsHideButton = document.createElement[Element]("button")
        controlsArea.appendChild(settingsHideButton)
        settingsHideButton.setAttribute("type", "button")
        settingsHideButton.setAttribute("id", "settingsHideButton")
        settingsHideButton.innerHTML = "Hide/show all your settings"
        settingsHideButton.setAttribute("onclick", "el = document.getElementById(\"visualPluginSettings\");" +
            "el.style.visibility = (el.style.visibility == \"visible\") ? \"hidden\" : \"visible\";")

        controlsArea.appendChild(document.createElement[Element]("br"))
        controlsArea.appendChild(document.createElement[Element]("br"))

        val settingsDiv = document.createElement[Element]("div")
        controlsArea.appendChild(settingsDiv)
        settingsDiv.setAttribute("id", "visualPluginSettings")
        if(visible) {
            settingsDiv.setAttribute("style", "visibility: visible;")
        } else {
            settingsDiv.setAttribute("style", "visibility: hidden;")
        }

        val settingsForm = document.createElement[Element]("form")
        settingsDiv.appendChild(settingsForm)

        //Vertex settings #########################################################################
        buildVertexSettings(settingsForm)

        settingsForm.appendChild(document.createElement[Element]("br"))
        settingsForm.appendChild(document.createElement[Element]("br"))

        //Edge settings ###########################################################################
        buildEdgeSettings(settingsForm)

        settingsForm.appendChild(document.createElement[Element]("br"))
        settingsForm.appendChild(document.createElement[Element]("br"))

        //Text setttings ##########################################################################
        buildTextSettings(settingsForm)

        settingsForm.appendChild(document.createElement[Element]("br"))
        settingsForm.appendChild(document.createElement[Element]("br"))

        //submit button ###########################################################################
        val submitButton = document.createElement[Element]("button")
        settingsForm.appendChild(submitButton)
        submitButton.setAttribute("type", "button")
        submitButton.innerHTML = "Yeah, I like this way"
        submitButton.setAttribute("onclick", "presenterIndex.updateSettings(true)")

        //reset button ############################################################################
        val resetButton = document.createElement[Element]("button")
        settingsForm.appendChild(resetButton)
        resetButton.setAttribute("type", "button")
        resetButton.innerHTML = "I want it all at default"
        resetButton.setAttribute("onclick", "presenterIndex.resetSettings(true)")
    }

    private def buildTextSettings(parent: Element) {
        val section = document.createElement[Element]("label")
        parent.appendChild(section)
        section.innerHTML = "Text"

        parent.appendChild(document.createElement[Element]("br"))
        buildColorSetup(parent, "medium", TextColorMedium)

        parent.appendChild(document.createElement[Element]("br"))
        buildColorSetup(parent, "backg", TextColorBackground)
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
        buildColorSetup(parent, "literal", VertexColorLiteral).setAttribute("disabled", "disabled")

        parent.appendChild(document.createElement[Element]("br"))
        buildColorSetup(parent, "identif", VertexColorIdentified).setAttribute("disabled", "disabled")

        parent.appendChild(document.createElement[Element]("br"))
        buildColorSetup(parent, "unkn", VertexColorUnknown).setAttribute("disabled", "disabled")

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

    private def buildColorSetup(parent: Element, labelText: String, location: String): Element = {

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
        inputField.className = "visualPluginSettings textField"
        inputField.setAttribute("type", "text")
        inputField.setAttribute("onChange", "window.localStorage.setItem(\""+bindToLocation+"\", this.value)")
        inputField.setAttribute("value", getValue(bindToLocation).getOrElse(""))

        inputField
    }
}
