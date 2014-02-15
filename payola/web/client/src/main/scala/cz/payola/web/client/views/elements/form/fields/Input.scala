package cz.payola.web.client.views.elements.form.fields

import s2js.adapters.html

abstract class Input[A <: html.Element with html.elements.Input, B](
    inputType: String,
    name: String,
    initialValue: B,
    title: String,
    cssClass: String)
    extends InputLikeView[A, B]("input", Nil, name, initialValue, title, cssClass)
{
    setAttribute("type", inputType)
}

class TextInput(name: String, initialValue: String, title: String = "", cssClass: String = "")
    extends Input[html.elements.TextInput, String]("text", name, initialValue, title, "form-control "+cssClass)
{
    triggerChangedOnKeyReleased()

    def value = htmlElement.value

    def updateValue(newValue: String) {
        htmlElement.value = newValue
    }

    def maxLength: Int = {
        htmlElement.maxLength
    }

    def maxLength_=(newValue: Int) {
        htmlElement.maxLength = newValue
    }
}

class Hidden(name: String, initialValue: String, title: String = "", cssClass: String = "")
    extends Input[html.elements.TextInput, String]("text", name, initialValue, title, cssClass)
{
    def value = htmlElement.value

    def updateValue(newValue: String) {
        htmlElement.value = newValue
    }
}

class FileInput(name: String, title: String = "", cssClass: String = "")
    extends Input[html.elements.Input, String]("file", name, "", title, cssClass)
{
    def value = htmlElement.value

    def updateValue(newValue: String) {
        htmlElement.value = newValue
    }
}

class ColorHTML5Input(name: String, title: String = "", cssClass: String = "")
    extends Input[html.elements.Input, String]("color", name, "", title, cssClass)
{
    def value = htmlElement.value

    def updateValue(newValue: String) {
        htmlElement.value = newValue
    }
}

class NumericInput(name: String, initialValue: Int, title: String = "", cssClass: String = "")
    extends Input[html.elements.Input, Int]("number", name, initialValue, title, cssClass)
{
    triggerChangedOnKeyReleased()

    def value = htmlElement.value.toInt

    def updateValue(newValue: Int) {
        htmlElement.value = newValue.toString
    }
}

abstract class CheckInput(
    inputType: String,
    name: String,
    initialValue: Boolean,
    title: String,
    cssClass: String)
    extends Input[html.elements.CheckInput, Boolean](inputType, name, initialValue, title, cssClass)
{
    def value = htmlElement.checked

    def updateValue(newValue: Boolean) {
        htmlElement.checked = newValue
    }
}

class CheckBox(name: String, initialValue: Boolean, title: String = "", cssClass: String = "")
    extends CheckInput("checkbox", name, initialValue, title, cssClass)

class Radio(name: String, initialValue: Boolean, title: String = "", cssClass: String = "")
    extends CheckInput("radio", name, initialValue, title, cssClass)
