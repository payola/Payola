package cz.payola.web.client.views.elements.form.fields

import s2js.adapters.html

class TextArea(name: String, initialValue: String, title: String = "", cssClass: String = "")
    extends InputLikeView[html.elements.TextArea, String]("textarea", Nil, name, initialValue, title, "form-control "+cssClass)
{
    triggerChangedOnKeyReleased()

    def value: String = htmlElement.value

    def updateValue(newValue: String) {
        htmlElement.value = newValue
    }
}
