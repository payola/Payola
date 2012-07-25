package cz.payola.web.client.presenters

import s2js.adapters.js.dom.Element
import s2js.compiler.javascript
import s2js.adapters.js.browser._
import cz.payola.web.shared.managers.PluginManager
import cz.payola.web.client._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.modals.AlertModal
import cz.payola.model.ModelException
import s2js.runtime.shared.rpc.RpcException

// Can't pass the editor's pre ID as we're using it in the native JS, which needs to
// be compile-time ready
class PluginCreator(val buttonContainerID: String, val listPluginsURL: String) extends Presenter
{

    // Create the ACE editor
    createEditor()

    // Create submit button
    val buttonContainer = document.getElementById(buttonContainerID)
    val submitButton = new Button(new Text("Create Plugin"))
    submitButton.mouseClicked += { event =>
        val code = getCode
        if (code == "") {
            AlertModal.runModal("The code can't be empty!")
        }else{
            postCodeToServer(code)
        }
        false
    }
    submitButton.render(buttonContainer)

    /** Creates a new editor.
      *
      */
    @javascript("window.ace_editor = ace.edit(\"editor\"); window.ace_editor.setTheme(\"ace/theme/clouds\");" +
        " var ScalaMode = require(\"ace/mode/scala\").Mode; window.ace_editor.getSession().setMode(new ScalaMode());")
    private def createEditor() {

    }

    /** Gets code from the editor.
      *
      */
    @javascript("return window.ace_editor.getSession().getValue();")
    private def getCode: String = {
        ""
    }

    def initialize() {

    }

    /** A post fail callback. Shows an alert that the upload failed.
      *
      * @param t An instance of Throwable.
      */
    private def postFailedCallback(t: Throwable){
        t match {
            case exc: RpcException => AlertModal.runModal(exc.message, title = "Failed to upload plugin!")
            case t: Throwable => fatalErrorHandler(t)
        }
    }

    /** Post success callback. Shows a success alert and redirects back to listing.
      *
      * @param s Success string.
      */
    private def postWasSuccessfulCallback(s: String) {
        val alert = new AlertModal("Plugin uploaded successfully!", "Success!", "alert-success")
        alert.confirming += { e =>
            window.location.href = listPluginsURL
            true
        }
        alert.render()
    }

    /** Posts code to the server to be compiled and a new plugin created.
      *
      * @param code Code of the plugin.
      */
    private def postCodeToServer(code: String) {
        blockPage("Compiling plugin...")
       PluginManager.uploadPlugin(code) {
            s => postWasSuccessfulCallback(s)
               unblockPage()
        } {
            t => postFailedCallback(t)
               unblockPage()
        }
    }
}
