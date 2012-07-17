package cz.payola.web.client.views.elements

class Checkbox(name: String, initialValue: String, title: Option[String], cssClass: String = "",
    inputType: String = "checkbox")
    extends Input(name, initialValue, title, cssClass, inputType)
{
    domElement.onclick = {e =>
        value_=(if(value.toBoolean){"false"}else{"true"})
        changed.triggerDirectly(this, e)
        true
    }

    if (initialValue.toBoolean){
        domElement.setAttribute("checked","checked")
    }

    override def value_=(value: String) {
        if (value.toBoolean){
            domElement.setAttribute("checked","checked")
            domElement.value = "true"
        }else{
            domElement.removeAttribute("checked")
            domElement.value = "false"
        }
    }
}
