package cz.payola.web.client.views.bootstrap.element

import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.elements.form.fields.TextInput
import cz.payola.web.client.views.elements.form.Label
import cz.payola.web.client.views.ComposedView

class AppendToUserCustButton (var availableValues: Seq[String], title: String, listTitle: String, cssClass: String = "",
    onAppendFunction: (String) => Boolean, customLabel: String = "Custom:",
    leftLocation: Int = -419, topLocation: Int = 0, width: Int = 740, height: Int = 340) extends ComposedView
{

    private var forbidPopupClose = false

    private val classDiv = new Div(Nil,"append-popup dropdown-menu")

    val appendButton = new Button(new Text(title), cssClass)

    classDiv.mouseClicked += { e =>
        closePopup()
        false
    }

    def createSubViews = List(appendButton, classDiv)

    def closePopup() {
        if (!forbidPopupClose) {
            classDiv.setAttribute("style","display: none")
            classDiv.removeAllChildNodes()
        }
        forbidPopupClose = false
    }

    def openPopup() {
        val addButton = new Button(new Text("Add"))
        val inputField = new TextInput("name", "", "", "span6")
        inputField.mouseClicked += { e => forbidPopupClose = true; false }
        val classNameInput = new Div(List(new Label(customLabel, inputField.formHtmlElement, ""), inputField, addButton))

        addButton.mouseClicked += { e =>
            if(inputField.value != null && inputField.value != "") {
                //create the class
                if (!onAppendFunction(inputField.value))
                    forbidPopupClose = true
            } else {
                forbidPopupClose = true
            }
            false
        }

        classNameInput.render(classDiv.htmlElement)


        new Heading(List(new Text(listTitle))).setAttribute(
            "style", "padding-top: 5px; padding-bottom: 5px;").render(classDiv.htmlElement)


        availableValues.foreach{ newClass => //list of classes available in the current graph

            val availableClassAnch = new Div(List(
                new Anchor(List(new Div(List(new Text(uriToName(newClass))),
                    "label label-info"))))).setAttribute("style", "padding-top: 5px;")

            availableClassAnch.mouseClicked += { e =>
                closePopup()

                //create the class
                onAppendFunction(newClass)
                false
            }
            availableClassAnch.render(classDiv.htmlElement)
        }

        classDiv.setAttribute("style","left: "+leftLocation+"px !important; top: "+topLocation+
            "px !important; display: block; width:"+width+"px; height:"+height+";")
    }

    private def uriToName(uri: String): String = {
        val nameParts = uri.split("#")
        if (nameParts.length > 1) nameParts(1) else uri
    }
}
