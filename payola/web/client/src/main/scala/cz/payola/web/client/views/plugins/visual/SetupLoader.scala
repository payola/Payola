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
        vertexColors.appendChild(vertexIcons)

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



        //setup -> edge
        val edgeSetup = document.createElement[Element]("edge")
        setup.appendChild(edgeSetup)

        //setup -> edge -> colors
        val edgeColors = document.createElement[Element]("colors")
        edgeSetup.appendChild(edgeColors)

        //setup -> vertex -> colors -> selected edge color
        val edgeSelectedColor = document.createElement[Element]("selected")
        vertexColors.appendChild(edgeSelectedColor)
        edgeSelectedColor.setAttribute("red", "0")
        edgeSelectedColor.setAttribute("green", "0")
        edgeSelectedColor.setAttribute("blue", "0")
        edgeSelectedColor.setAttribute("alpha", "1")

        //setup -> vertex -> colors -> default edge color
        val edgeDefaultColor = document.createElement[Element]("default")
        vertexColors.appendChild(edgeDefaultColor)
        edgeDefaultColor.setAttribute("red", "50")
        edgeDefaultColor.setAttribute("green", "50")
        edgeDefaultColor.setAttribute("blue", "50")
        edgeDefaultColor.setAttribute("alpha", "0.7")



        //setup -> text
        val textSetup = document.createElement[Element]("text")
        setup.appendChild(textSetup)

        //setup -> text -> colors
        val textColors = document.createElement[Element]("colors")
        textSetup.appendChild(textColors)

        //setup -> text -> colors -> default text color
        val textDefaultColor = document.createElement[Element]("default")
        textDefaultColor.appendChild(textDefaultColor)
        textDefaultColor.setAttribute("red", "0")
        textDefaultColor.setAttribute("green", "0")
        textDefaultColor.setAttribute("blue", "0")
        textDefaultColor.setAttribute("alpha", "1")

        setup
    }
    
    protected def getNodeByPath(parent: Element, path: String): Option[Element] = {
        val nodeNames = path.split('.')
        var currentElement: Option[Element] = Some(parent)
        nodeNames.foreach{ nodeName =>
            if(currentElement.isDefined) {
                currentElement = getChildByName(currentElement.get, nodeName)
            }
        }

        currentElement
    }

    private def getChildByName(parent: Element, name: String): Option[Element] = {
        
        var result: Option[Element] = None
        var pointer = 0
        while(result.isEmpty && pointer < parent.childNodes.length) {
            if(parent.childNodes.item(pointer).nodeName == name) {
                result = Some(parent.childNodes.item(pointer).asInstanceOf[Element])
            }
            pointer += 1
        }
        
        result
    }

    protected def createColor(setupElement: Element): Option[Color] = {
        var result: Option[Color] = None
        
        if(setupElement.hasAttribute("red") && setupElement.hasAttribute("green") &&
            setupElement.hasAttribute("blue") && setupElement.hasAttribute("alpha")) {
            
            val red = setupElement.getAttribute("red").toInt
            val green = setupElement.getAttribute("green").toInt
            val blue = setupElement.getAttribute("blue").toInt
            val alpha = setupElement.getAttribute("alpha").toInt
            
            result = Some(new Color(red, green, blue, alpha))
        }
        
        result
    }
}
