package cz.payola.web.client.views.bootstrap.element

import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.elements.form.fields.TextInput
import cz.payola.web.client.views.elements.form.Label
import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.models.PrefixApplier

class AppendToUserCustButton (var availableValues: Seq[String], buttonLabel: String, buttonTitle: String,
    listHeader: String, listTitle: String, cssClass: String = "",
    onAppendFunction: (String) => Unit,
    prefixApplier: PrefixApplier, customLabel: String = "Custom:") extends ComposedView
{
    private val heading = new Heading(List(new Text(listTitle))).setAttribute(
        "style", "padding-top: 5px; padding-bottom: 5px;")

    private val inputField = new TextInput("name", "", "Input custom", "col-lg-6")

    private val addButton = new Button(new Text("Add"))
    addButton.mouseClicked += { e =>
        if(inputField.value != null && inputField.value != "") {
            closePopup()
            //create the class
            onAppendFunction(inputField.value)
        }
        false
    }
    private val closeButton = new Button(new Text("x"), "close")
    closeButton.mouseClicked += { e => closePopup(); false}

    private val classNameInput = new Div(List(
        new Div(List(closeButton, new Heading(List(new Text(listHeader)))), "modal-header"),
        new Label(customLabel, inputField.formHtmlElement, ""), inputField, addButton))

    private val valuesDiv = new Div(
        availableValues.map{ newClass => //list of classes available in the current graph

            val availableClassAnch = new Div(List(
                new Anchor(List(new Div(List(new Text(uriToName(newClass))),
                    "label label-info"))))).setAttribute("style", "padding-top: 5px;")

            availableClassAnch.mouseClicked += { e =>
                closePopup()

                //create the class
                onAppendFunction(newClass)
                false
            }
            availableClassAnch
        }).setAttribute("style", "overflow: auto; height: 350px;")

    private val classDiv = new Div(List(classNameInput, heading, valuesDiv),"append-popup dropdown-menu").setAttribute(
        "style","position: fixed !important; left: 50% !important; top: 50% !important;"+
        " display: none; width: 740px; height: 500px; margin: -250px 0 0 -370px;")

    val appendButton = new Button(new Text(buttonLabel), cssClass).setAttribute("title", buttonTitle)

    def createSubViews = List(appendButton, classDiv)

    def closePopup() {
        classDiv.setAttribute("style","display: none")
    }

    def openPopup() {
        classDiv.setAttribute(
            "style","position: fixed !important; left: 50% !important; top: 50% !important;"+
                " display: block; width: 740px; height: 500px; margin: -250px 0 0 -370px;")
    }

    private def uriToName(uri: String): String = {
        val prefxedUri = prefixApplier.applyPrefix(uri)
        val nameParts = uri.split("#")
        if(prefxedUri == uri) {if (nameParts.length > 1) { nameParts(1) } else { uri } } else prefxedUri
    }
}
