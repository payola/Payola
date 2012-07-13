package cz.payola.web.client.presenters

import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.dom.Element
import s2js.compiler.javascript
import s2js.adapters.js.browser._
import cz.payola.web.client.mvvm.element.extensions.Bootstrap._
import cz.payola.web.shared.PluginManager

// Can't pass the editor's pre ID as we're using it in the native JS, which needs to
// be compile-time ready
class PluginCreator(val buttonContainerID: String, val listPluginsURL: String) extends Component
{

    createEditor()

    val buttonContainer = document.getElementById(buttonContainerID)
    val submitButton = new Button("Create Plugin")
    submitButton.clicked += { event =>
        val code = getCode
        if (code == "") {
            window.alert("The code can't be empty!")
            false
        }else{
            postCodeToServer(code)

            true
        }
    }
    submitButton.render(buttonContainer)

    @javascript("window.ace_editor = ace.edit(\"editor\"); window.ace_editor.setTheme(\"ace/theme/clouds\");" +
        " var ScalaMode = require(\"ace/mode/scala\").Mode; window.ace_editor.getSession().setMode(new ScalaMode());")
    private def createEditor() {

    }

    @javascript("return window.ace_editor.getSession().getValue();")
    private def getCode: String = {
        ""
    }

    def getDomElement() : Element = {
        null
    }

    private def postFailedCallback(t: Throwable){
        val exceptionMessage = t.asInstanceOf[s2js.runtime.shared.Exception].message
        window.alert("Failed to upload plugin!\n\n" + exceptionMessage)
    }

    private def postWasSuccessfulCallback(s: String) {
        window.alert("Plugin uploaded successfully!")
        window.location.href = listPluginsURL
    }

    private def postCodeToServer(code: String) {
       PluginManager.uploadPlugin(code) {
            s => postWasSuccessfulCallback(s)
        } {
            t => postFailedCallback(t)
        }
    }

    def render(parent: Element) = {
        // TODO
    }


}
