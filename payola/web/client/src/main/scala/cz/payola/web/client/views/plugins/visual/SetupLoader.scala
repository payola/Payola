package cz.payola.web.client.views.plugins.visual

import s2js.adapters.js.browser._
import s2js.adapters.js.dom.Element

trait  SetupLoader
{
    def createDefaultSetup(): Element = {
        val setup = document.createElement[Element]("setup")

        //setup -> vertex
        val vertexSetup = document.createElement[Element]("vertex")
        setup.appendChild(vertexSetup)

        //setup -> vertex -> colors
        val vertexColors = document.createElement[Element]("colors")
        vertexSetup.appendChild(vertexColors)

        //setup -> vertex -> colors -> default color
        val vertexColorDefault = document.createElement[Element]("default")
        vertexColors.appendChild(vertexColorDefault)
        vertexColorDefault.setAttribute("red", "100")
        vertexColorDefault.setAttribute("green", "100")
        vertexColorDefault.setAttribute("blue", "100")
        vertexColorDefault.setAttribute("alpha", "0.8")

        //setup -> vertex -> colors -> selected color
        val vertexColorSelected = document.createElement[Element]("selected")
        vertexColors.appendChild(vertexColorSelected)
        vertexColorSelected.setAttribute("red", "255")
        vertexColorSelected.setAttribute("green", "0")
        vertexColorSelected.setAttribute("blue", "0")
        vertexColorSelected.setAttribute("alpha", "1")

        //setup -> vertex -> colors -> hidden color
        val vertexColorHidden = document.createElement[Element]("hidden")
        vertexColors.appendChild(vertexColorHidden)
        vertexColorHidden.setAttribute("red", "240")
        vertexColorHidden.setAttribute("green", "240")
        vertexColorHidden.setAttribute("blue", "240")
        vertexColorHidden.setAttribute("alpha", "0.5")

        //setup -> vertex -> colors -> literal vertex color
        val vertexLiteralColor = document.createElement[Element]("literal")
        vertexColors.appendChild(vertexLiteralColor)
        vertexLiteralColor.setAttribute("red", "240")
        vertexLiteralColor.setAttribute("green", "0")
        vertexLiteralColor.setAttribute("blue", "0")
        vertexLiteralColor.setAttribute("alpha", "1")

        //setup -> vertex -> colors -> identified vertex color
        val vertexIdentifiedColor = document.createElement[Element]("identified")
        vertexColors.appendChild(vertexIdentifiedColor)
        vertexIdentifiedColor.setAttribute("red", "0")
        vertexIdentifiedColor.setAttribute("green", "240")
        vertexIdentifiedColor.setAttribute("blue", "0")
        vertexIdentifiedColor.setAttribute("alpha", "1")

        //setup -> vertex -> colors -> unknown vertex color
        val vertexUnknownColor = document.createElement[Element]("unknown")
        vertexColors.appendChild(vertexUnknownColor)
        vertexUnknownColor.setAttribute("red", "0")
        vertexUnknownColor.setAttribute("green", "0")
        vertexUnknownColor.setAttribute("blue", "0")
        vertexUnknownColor.setAttribute("alpha", "1")


        //setup -> vertex -> icons
        val vertexIcons = document.createElement[Element]("icons")
        vertexSetup.appendChild(vertexIcons)

        //setup -> vertex -> icons -> literal vertex icon
        val vertexLiteralIcon = document.createElement[Element]("literal")
        vertexIcons.appendChild(vertexLiteralIcon)
        vertexLiteralIcon.setAttribute("value", "/assets/images/book-icon.png")

        //setup -> vertex -> icons -> identified vertex icon
        val vertexIdentifiedIcon = document.createElement[Element]("identified")
        vertexIcons.appendChild(vertexIdentifiedIcon)
        vertexIdentifiedIcon.setAttribute("value", "/assets/images/view-eye-icon.png")

        //setup -> vertex -> icons -> unknown vertex icon
        val vertexUnknownIcon = document.createElement[Element]("identified")
        vertexIcons.appendChild(vertexUnknownIcon)
        vertexUnknownIcon.setAttribute("value", "/assets/images/question-mark-icon.png")


        //setup -> vertex -> dimensions
        val vertexDimensions = document.createElement[Element]("dimensions")
        vertexSetup.appendChild(vertexDimensions)

        //setup -> vertex -> dimensions -> corner radius
        val vertexDimensionCornerRadius = document.createElement[Element]("corner-radius")
        vertexDimensions.appendChild(vertexDimensionCornerRadius)
        vertexDimensionCornerRadius.setAttribute("value", "5")

        //setup -> vertex -> dimensions -> width
        val vertexDimensionWidth = document.createElement[Element]("width")
        vertexDimensions.appendChild(vertexDimensionWidth)
        vertexDimensionWidth.setAttribute("value", "30")

        //setup -> vertex -> dimensions -> height
        val vertexDimensionHeight = document.createElement[Element]("height")
        vertexDimensions.appendChild(vertexDimensionHeight)
        vertexDimensionHeight.setAttribute("value", "24")



        //setup -> edge ###########################################################################
        val edgeSetup = document.createElement[Element]("edge")
        setup.appendChild(edgeSetup)

        //setup -> edge -> colors
        val edgeColors = document.createElement[Element]("colors")
        edgeSetup.appendChild(edgeColors)

        //setup -> edge -> colors -> selected edge color
        val edgeSelectedColor = document.createElement[Element]("selected")
        edgeColors.appendChild(edgeSelectedColor)
        edgeSelectedColor.setAttribute("red", "0")
        edgeSelectedColor.setAttribute("green", "0")
        edgeSelectedColor.setAttribute("blue", "0")
        edgeSelectedColor.setAttribute("alpha", "1")

        //setup -> edge -> colors -> default edge color
        val edgeDefaultColor = document.createElement[Element]("default")
        edgeColors.appendChild(edgeDefaultColor)
        edgeDefaultColor.setAttribute("red", "50")
        edgeDefaultColor.setAttribute("green", "50")
        edgeDefaultColor.setAttribute("blue", "50")
        edgeDefaultColor.setAttribute("alpha", "0.7")

        //setup -> edge -> dimensions
        val edgeDimensions = document.createElement[Element]("dimensions")
        edgeSetup.appendChild(edgeDimensions)

        //setup -> edge -> dimensions ->  line width
        val edgeLineWidth = document.createElement[Element]("width")
        edgeDimensions.appendChild(edgeLineWidth)
        edgeLineWidth.setAttribute("value", "1")

        //setup -> edge -> dimensions ->  straight index
        val edgeStraightIndex = document.createElement[Element]("straight-index")
        edgeDimensions.appendChild(edgeStraightIndex)
        edgeStraightIndex.setAttribute("value", "2")


        //setup -> text ###########################################################################
        val textSetup = document.createElement[Element]("text")
        setup.appendChild(textSetup)

        //setup -> text -> colors
        val textColors = document.createElement[Element]("colors")
        textSetup.appendChild(textColors)

        //setup -> text -> colors -> default text color
        val textDefaultColor = document.createElement[Element]("default")
        textColors.appendChild(textDefaultColor)
        textDefaultColor.setAttribute("red", "0")
        textDefaultColor.setAttribute("green", "0")
        textDefaultColor.setAttribute("blue", "0")
        textDefaultColor.setAttribute("alpha", "1")

        //setup -> text -> colors -> background text color
        val textBackgroundColor = document.createElement[Element]("background")
        textColors.appendChild(textBackgroundColor)
        textBackgroundColor.setAttribute("red", "0")
        textBackgroundColor.setAttribute("green", "0")
        textBackgroundColor.setAttribute("blue", "0")
        textBackgroundColor.setAttribute("alpha", "1")


        setup
    }
    
    protected def createColor(localStorageKey: String): Option[Color] = {
        val red = window.localStorage.getItem(localStorageKey + ".red")
        val green = window.localStorage.getItem(localStorageKey + ".green")
        val blue = window.localStorage.getItem(localStorageKey + ".blue")
        val alpha = window.localStorage.getItem(localStorageKey + ".alpha")

        window.alert("red: "+ red+" green: "+green+" blue: "+ blue+ " alpha: "+alpha)
        //TODO if correct create new color nebo new Color(200, 0, 0, 1)
        if(red.isEmpty || green.isEmpty || blue.isEmpty || alpha.isEmpty) {
            None
        } else {
            Some(new Color(red.toInt, green.toInt, blue.toInt, alpha.toDouble))
        }
    }
    
    protected def getValue(localStorageKey: String): Option[String] = {
        val value = window.localStorage.getItem(localStorageKey)
        window.alert("value: "+value)
        if(value.isEmpty) { //TODO is this a valid check?
            None
        } else {
            Some(value)
        }
    }
}
