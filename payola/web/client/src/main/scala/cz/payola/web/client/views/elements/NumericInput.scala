package cz.payola.web.client.views.elements

class NumericInput(name: String, initialValue: String, title: Option[String], cssClass: String = "",
    inputType: String = "number")
    extends Input(name, initialValue, title, cssClass, inputType)
